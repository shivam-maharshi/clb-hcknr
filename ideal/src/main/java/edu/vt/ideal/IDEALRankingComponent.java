package edu.vt.ideal;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Author: dedocibula
 * Created on: 10.3.2016.
 */
public class IDEALRankingComponent extends SearchComponent {
    private static Logger logger = Logger.getLogger(IDEALRankingComponent.class);

    private final Map<String, Float> fieldWeights;
    private boolean verboseMode = false;
    private IDEALTopicIndexer topicIndexer;

    public IDEALRankingComponent() {
        fieldWeights = new HashMap<>();
    }

    @Override
    public void init(NamedList args) {
        super.init(args);

        // enable debugging - logs
        String verboseMode = (String) args.get("verbose");
        this.verboseMode = verboseMode != null && "true".equals(verboseMode.toLowerCase());

        // load weights
        String path = (String) args.get("weights-file");
        if (path == null || path.isEmpty()) {
            logger.warn(String.format("Initializing IDEAL Ranking Component without weight file and verbose mode [ %s ].", this.verboseMode));
            return;
        }

        File weightFile = new File(path);
        if (!weightFile.exists() || !weightFile.isFile()) {
            logger.error(String.format("IDEAL Ranking Component couldn't access weight file at path [ %s ]. " +
                    "Make sure the file exists and access permissions are set.", path));
            return;
        }

        // create topic index
        topicIndexer = new IDEALTopicIndexer(this.verboseMode);

        initializeWeights(weightFile);
    }

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (verboseMode)
            logger.info("IDEAL Ranking Component prepare phase invoked.");

        Set<Term> termSet = new HashSet<>();
        Query originalQuery = rb.getQuery();
        originalQuery.extractTerms(termSet);

        // tries to expand original query with topic labels derived from terms
        Set<String> labels = topicIndexer.searchTopicLabels(termSet);
        if (labels != null) {
            BooleanQuery query = new BooleanQuery();
            query.add(originalQuery, BooleanClause.Occur.SHOULD);

            for (String label : labels) {
                // text field contains all the text/string values of the document
                TermQuery supplementQuery = new TermQuery(new Term("text", label.toLowerCase()));
                // not to overwhelm original query
                // TODO replace with word probabilities (IDEALTopicIndexer)
                supplementQuery.setBoost(originalQuery.getBoost() * .9f);
                query.add(supplementQuery, BooleanClause.Occur.SHOULD);
            }

            if (verboseMode)
                logger.info(String.format("Original query [ %s ], supplemented query [ %s ]", originalQuery, query));

            originalQuery = query;
        }

        // adds custom scores as tf-idf + fieldValue1 * weight1 + fieldValue2 * weight2...
        rb.setQuery(new ScoreBoostingQuery(originalQuery, fieldWeights, verboseMode));
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        if (verboseMode)
            logger.info("IDEAL Ranking Component process phase invoked.");
    }

    @Override
    public String getDescription() {
        return "IDEAL ranking component for CS5604";
    }

    @Override
    public String getSource() {
        return "https://github.com/shivam-maharshi/IDEAL";
    }

    private void initializeWeights(File weightFile) {
        Properties props = new Properties();

        try (InputStream stream = new FileInputStream(weightFile)) {
            props.load(stream);

            for (String propName : props.stringPropertyNames()) {
                try {
                    fieldWeights.put(propName, Float.parseFloat(props.getProperty(propName)));
                } catch (NumberFormatException e) {
                    logger.error(String.format("Ignoring property [ %s ]. Failed to parse value as float.", propName), e);
                }
            }

            logger.info(String.format("Initializing IDEAL Ranking Component with weight file [ %s ] and verbose mode [ %s ].\n" +
                    "Used weights: %s", weightFile.getAbsolutePath(), verboseMode ? "on" : "off", fieldWeights));
        } catch (IOException e) {
            logger.error("IDEAL Ranking Component encountered error while reading file", e);
        }
    }

    private static final class ScoreBoostingQuery extends CustomScoreQuery {
        private final Map<String, Float> fieldWeights;
        private final boolean verboseMode;

        ScoreBoostingQuery(Query subQuery, Map<String, Float> fieldWeights, boolean verboseMode) {
            super(subQuery);

            this.fieldWeights = fieldWeights;
            this.verboseMode = verboseMode;
        }

        @Override
        protected CustomScoreProvider getCustomScoreProvider(AtomicReaderContext context) throws IOException {
            return new CustomScoreProvider(context) {
                @Override
                public float customScore(int doc, float subQueryScore, float[] valSrcScores) throws IOException {
                    // original score - tf-idf
                    float score = super.customScore(doc, subQueryScore, valSrcScores);

                    if (verboseMode)
                        logger.info(String.format("DocId [ %s ], original score [ %s ]", doc, score));

                    Document d = context.reader().document(doc);
                    // boosts to the score based on normalized value of certain fields
                    for (String field : fieldWeights.keySet()) {
                        IndexableField scoreField = d.getField(field);
                        if (scoreField != null && scoreField.numericValue() != null) {
                            float scoreBoost = scoreField.numericValue().floatValue();
                            float weight = fieldWeights.get(field);
                            if (verboseMode)
                                logger.info(String.format("DocId [ %s ], field [ %s ], score boost [ %s ], weight [ %s ]",
                                        doc, field, scoreBoost, weight));
                            score += weight * scoreBoost;
                        }
                    }

                    if (verboseMode)
                        logger.info(String.format("DocId [ %s ], final score [ %s ]", doc, score));

                    return score;
                }
            };
        }
    }
}
