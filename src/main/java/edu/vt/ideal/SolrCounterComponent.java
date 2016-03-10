package edu.vt.ideal;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.DocListAndSet;

import java.io.IOException;
import java.util.*;

/**
 * Author: dedocibula
 * Created on: 10.3.2016.
 */
public class SolrCounterComponent extends SearchComponent {
    private static Logger logger = Logger.getLogger(SolrCounterComponent.class);

    private Set<String> counter;

    @Override
    public void init(NamedList args) {
        super.init(args);

        counter = new HashSet<>();
        counter.add("samsung");
        counter.add("apple");
        counter.add("asus");
        counter.add("belkin");
        counter.add("ati");
    }

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        // do nothing
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        logger.info(String.format("[ %s ] - Popularity Boost Component invoked", new Date()));

        DocListAndSet results = rb.getResults();

        DocList docList = results.docList;

        if (docList.size() > 1) {

            DocIterator iterator = docList.iterator();
            Map<String, Integer> response = new HashMap<>();

            while (iterator.hasNext()) {
                int docId = iterator.nextDoc();

                Document d = rb.req.getSearcher().doc(docId);

                for (IndexableField multiField : d.getFields()) {
                    for (String string : multiField.stringValue().split(" ")) {
                        String word = string.toLowerCase();
                        if (counter.contains(word)) {
                            if (!response.containsKey(word))
                                response.put(word, 0);
                            response.put(word, response.get(word) + 1);
                        }
                    }
                }
            }

            rb.rsp.add("counter", response);
        }
    }

    @Override
    public String getDescription() {
        return "This is a test for CS 5604";
    }

    @Override
    public String getSource() {
        return "This is a get source";
    }
}
