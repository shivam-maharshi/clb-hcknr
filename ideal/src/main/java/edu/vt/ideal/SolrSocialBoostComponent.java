package edu.vt.ideal;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.ScoreDoc;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.response.ResultContext;
import org.apache.solr.search.*;

import java.io.IOException;

/**
 * Author: dedocibula
 * Created on: 28.2.2016.
 */
public class SolrSocialBoostComponent extends SearchComponent {
    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        // do nothing
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        //original docs, retrieved and ordered with VSM tf-idf score
        DocListAndSet results = rb.getResults();

        DocList docList = results.docList;

        if (docList.size() > 1) {
            int idx = 0;

            //our new scores
            ScoreDoc[] scoreDocs = new ScoreDoc[docList.size()];

            DocIterator iterator = docList.iterator();
            while (iterator.hasNext()) {

                int docId = iterator.nextDoc();

                float score = iterator.score();

                Document d = rb.req.getSearcher().doc(docId);
                float socialBoost = 0;
                IndexableField socialImportanceField = d.getField("social_importance");
                if (socialImportanceField != null) {
                    Number socialImportance = socialImportanceField.numericValue();
                    if (socialImportance != null) {
                        socialBoost = socialImportance.floatValue();
                    }
                    score = score + socialBoost;
                }
                scoreDocs[idx++] = new ScoreDoc(docId, score);
            }

            SortSpec sortSpec = rb.getSortSpec();

            int len = sortSpec.getCount();
            int offset = sortSpec.getOffset();

            int[] docs = new int[scoreDocs.length];
            float[] scores = new float[scoreDocs.length];
            for (int i = 0; i < scoreDocs.length; i++) {
                docs[i] = scoreDocs[i].doc;
                scores[i] = scoreDocs[i].score;
            }

            int totalHits = results.docList.matches();

            results.docList = new DocSlice(offset, len, docs, scores, totalHits, 2.0f);
            ResultContext ctx = (ResultContext) rb.rsp.getValues().get("response");
            ctx.docs = results.docList;
        }
    }

    @Override
    public String getDescription() {
        return "This is a test for CS 5604";
    }

    @Override
    public String getSource() {
        return "This is get source";
    }
}
