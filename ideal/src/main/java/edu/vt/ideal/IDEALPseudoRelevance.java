package edu.vt.ideal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.DocListAndSet;

import com.google.common.base.Strings;

/**
 * This Pseudo Relevance Feedback Component is responsible for reformulating the
 * expanded query by analyzing the top-k results of the expanded query. Here we
 * assume the fact that the top-k document must be highly relevant to the
 * information that the user intends to seek with his query. Hence it would be
 * fair to present more search results similar to the top-k documents. This is
 * possible by tweaking the query to fit the top-k search results. This
 * mechanism can either improve or degrade either precision or recall or both.
 * The performance of this strategy depends on fine tuning based on empirical
 * observations.
 * 
 * Reference:
 * http://nlp.stanford.edu/IR-book/html/htmledition/pseudo-relevance-feedback-1.
 * html
 * 
 * @author shivam.maharshi
 */
public class IDEALPseudoRelevance extends SearchComponent {
	private static Logger logger = Logger.getLogger(IDEALPseudoRelevance.class);
	private boolean verboseMode;
	private final Map<String, Float> fieldWeights;
	private static int PRF_TOP_K = 5;
	private static final String TOPIC_FIELD = "topic_label_s";
	private static final String TOPIC_PROB_FIELD = "topic_probability_list_fs";
	private static final String CLUSTER_FIELD = "cluster_label_s"; // StrField
	private static final String CLUSTER_PROB_FIELD = "cluster_probability_f"; // TrieFloatField
	private static final String COLLECTION_FIELD = "collection_name_s";
	private static final String COLLECTION_PROB_FIELD = "classification_relevance_f";

	public IDEALPseudoRelevance() {
		fieldWeights = new HashMap<>();
	}

	@Override
	public void init(NamedList args) {
		// enable debugging - logs
		String verboseMode = (String) args.get("verbose");
		this.verboseMode = verboseMode != null && "true".equals(verboseMode.toLowerCase());
		// load weights
		String path = (String) args.get("weights-file");
		if (path == null || path.isEmpty()) {
			logger.warn(
					String.format("Initializing IDEAL Ranking Component without weight file and verbose mode [ %s ].",
							this.verboseMode));
			return;
		}
		File weightFile = new File(path);
		if (!weightFile.exists() || !weightFile.isFile()) {
			logger.error(String.format("IDEAL Ranking Component couldn't access weight file at path [ %s ]. "
					+ "Make sure the file exists and access permissions are set.", path));
			return;
		}
		initializeWeights(weightFile);
	}

	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
		// Do nothing here.
	}

	@Override
	public void process(ResponseBuilder rb) throws IOException {
		if (verboseMode)
			logger.info("IDEAL Ranking Component prepare phase invoked.");

		DocListAndSet searchResults = rb.getResults();
		DocList resultDocList = searchResults.docList;
		DocIterator docIt = resultDocList.iterator();
		int count = 0;
		// Fetch top-k results from the expanded query.
		List<Document> topKRes = new ArrayList<Document>();
		while (docIt.hasNext() && count < PRF_TOP_K) {
			Document document = rb.req.getSearcher().doc(docIt.nextDoc());
			if (document != null) {
				topKRes.add(document);
			}
			count++;
		}

		// Create a new query by using the fields from these top-k results.
		Query expandedQuery = rb.getQuery();
		BooleanQuery prfQuery = new BooleanQuery();
		for (Document doc : topKRes) {
			// Take relevance from collection field.
			TermQuery collectionQuery = null;
			String collectionField = doc.get(COLLECTION_FIELD);
			String collectionProb = doc.get(COLLECTION_PROB_FIELD);
			if (!Strings.isNullOrEmpty(collectionField) && !Strings.isNullOrEmpty(collectionProb)) {
				collectionQuery = new TermQuery(new Term("text", collectionField));
				collectionQuery.setBoost(expandedQuery.getBoost() * ((Float.valueOf(collectionProb))));
				prfQuery.add(collectionQuery, BooleanClause.Occur.SHOULD);
			}
			// Add relevance from topic field.
			TermQuery topicQuery = null;
			String topicField = doc.get(TOPIC_FIELD);
			String topicProb = doc.get(TOPIC_PROB_FIELD);
			if (!Strings.isNullOrEmpty(topicField) && !Strings.isNullOrEmpty(topicProb)) {
				topicQuery = new TermQuery(new Term("text", topicField));
				topicQuery.setBoost(expandedQuery.getBoost() * ((Float.valueOf(topicProb))));
				prfQuery.add(topicQuery, BooleanClause.Occur.SHOULD);
			}
			// Add relevance from the cluster field.
			TermQuery clusterQuery = null;
			String clusterField = doc.get(CLUSTER_FIELD);
			String clusterProb = doc.get(CLUSTER_PROB_FIELD);
			if (!Strings.isNullOrEmpty(clusterField) && !Strings.isNullOrEmpty(clusterProb)) {
				clusterQuery = new TermQuery(new Term("text", clusterField));
				clusterQuery.setBoost(expandedQuery.getBoost() * ((Float.valueOf(clusterProb))));
				prfQuery.add(clusterQuery, BooleanClause.Occur.SHOULD);
			}
		}
		// Custom scores = tf-idf*1 + topics * tWeight + cluster *cWeight...
		rb.setQuery(new ScoreBoostingQuery(prfQuery, fieldWeights, verboseMode));
	}

	@Override
	public String getDescription() {
		return "IDEAL Pseudo Relevance Feedback component for CS5604.";
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
					logger.error(String.format("Ignoring property [ %s ]. Failed to parse value as float.", propName),
							e);
				}
			}
			logger.info(String.format(
					"Initializing IDEAL Ranking Component with weight file [ %s ] and verbose mode [ %s ].\n"
							+ "Used weights: %s",
					weightFile.getAbsolutePath(), verboseMode ? "on" : "off", fieldWeights));
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
					for (String field : fieldWeights.keySet()) {
						IndexableField scoreField = d.getField(field);
						if (scoreField != null && scoreField.numericValue() != null) {
							float scoreBoost = scoreField.numericValue().floatValue();
							float weight = fieldWeights.get(field);
							if (verboseMode)
								logger.info(
										String.format("DocId [ %s ], field [ %s ], score boost [ %s ], weight [ %s ]",
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
