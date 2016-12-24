package edu.vt.ideal;

import org.apache.commons.lang.NotImplementedException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.response.ResultContext;
import org.apache.solr.search.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

/**
 * Author: dedocibula
 * Created on: 19.4.2016.
 */
public class SolrTopicSupplementComponent extends SearchComponent {
    private static final Logger logger = Logger.getLogger(SolrTopicSupplementComponent.class);

    private static Configuration conf = null;
    private static final Integer MIN_DOCS = 20;


    static {
        conf = HBaseConfiguration.create();
    }

    @Override
    public String getDescription() {
        throw new NotImplementedException();
    }

    @Override
    public String getSource() {
        throw new NotImplementedException();
    }

    @Override
    public void prepare(ResponseBuilder responseBuilder) throws IOException {


    }

    private String getDominantCollection(ResponseBuilder rb) throws IOException {


        String winningCollection = "election";
        Integer winningCount = 0;

        Map<String, Integer> collectionCounts = new HashMap<String, Integer>();

        DocIterator iterator = rb.getResults().docList.iterator();
        while (iterator.hasNext()) {

            int docId = iterator.nextDoc();
            Document d = rb.req.getSearcher().doc(docId);
            String collectionName = null;
            IndexableField collectionField = d.getField("collection");
            if (collectionField != null) {
                collectionName = collectionField.stringValue();
                Integer collectionCount = collectionCounts.get(collectionName);
                if(collectionCount == null) {
                    collectionCounts.put(collectionName, 1);
                }
                else {
                    collectionCounts.put(collectionName, collectionCount+1);
                }
            }

        }
        for (Object o : collectionCounts.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if ((Integer) pair.getValue() > winningCount) {
                winningCollection = (String) pair.getKey();
                winningCount = (Integer) pair.getValue();
            }
        }
        return winningCollection;
    }

//    private TopicModel getCollectionTopicModel(String collectionName) throws IOException {
//
//
//        TopicModel topicModel =new TopicModel(collectionName);
//
//        // Uncomment in production
//        HTable table = new HTable(conf, "collection_metadata");
//        Get get = new Get(Bytes.toBytes(collectionName));
//        byte[] val = table.get(get).getValue(b("analysis"), b("lda"));
//        String jsonTopicModel = new String(val, StandardCharsets.UTF_8);
//
//        Map<String, Double> map = new Gson().fromJson(jsonTopicModel, new TypeToken<HashMap<String, Double>>() {}.getType());
//        topicModel.setTopicProbabiliities(map);
//
//
//        return topicModel;
//
//    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {

        DocListAndSet results = rb.getResults();

        SortSpec sortSpec = rb.getSortSpec();
        int len = sortSpec.getCount();
        int offset = sortSpec.getOffset();

        DocList docList = rb.getResults().docList;
        if (docList.size() < MIN_DOCS) {
            String collectionName = getDominantCollection(rb);
//            TopicModel topicModel = getCollectionTopicModel(collectionName);
//            String topTopics = topicModel.getTopTopics();

            QueryParser queryParser = new QueryParser("name", new StandardAnalyzer());
            Query query = null;
            try {
                query = queryParser.parse("q=" + "sampleTopic");

            } catch (ParseException e) {
                e.printStackTrace();
            }

            TopDocs additionalDocs = rb.req.getSearcher().search(query, 100);
            ScoreDoc[] scoreDocs = additionalDocs.scoreDocs;
            int totalHits = additionalDocs.totalHits + docList.matches();

            int[] docs = new int[scoreDocs.length];
            float[] scores = new float[scoreDocs.length];
            for (int i = 0; i < scoreDocs.length; i++) {
                docs[i] = scoreDocs[i].doc;
                scores[i] = scoreDocs[i].score;
            }

            if (docs.length > 0) {

                out.println("Extra docs");

                results.docList = new DocSlice(offset, len, docs, scores, totalHits,
                        2.0f);
                ResultContext ctx = (ResultContext) rb.rsp.getValues().get("response");
                ctx.docs = results.docList;

            }
            else {
                out.println("No extra docs");
            }

        }
        else {
            out.println("Enough docs. Not supplementing.");
        }

    }
    private byte[] b(String s) {
        return Bytes.toBytes(s);
    }
}
