package edu.vt.ideal;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * Author: dedocibula
 * Created on: 28.2.2016.
 */
public class NRTLuceneDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        RAMDirectory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(index, config);

        // tracks changes to delegated used by ControlledRealTimeReopenThread to ensure they are visible
        TrackingIndexWriter trackingIndexWriter = new TrackingIndexWriter(indexWriter);
        // allows sharing IndexSearcher across multiple threads
        final ReferenceManager<IndexSearcher> searcherManager = new SearcherManager(indexWriter, true, null);

        // periodically refreshes IndexReader in the background
        ControlledRealTimeReopenThread<IndexSearcher> nrtReopenThread = new ControlledRealTimeReopenThread<>(
                trackingIndexWriter, searcherManager, /* max stale sec */ 1.0, /* min stale sec */ 0.1);
        nrtReopenThread.setName("NRT Reopen Thread");
        nrtReopenThread.setPriority(Math.min(Thread.currentThread().getPriority() + 2, Thread.MAX_PRIORITY));
        nrtReopenThread.setDaemon(true);
        nrtReopenThread.start();

        // writer thread
        Thread writerThread = new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 200; ++i) {
                        Document doc = new Document();
                        doc.add(new LongField("time", System.currentTimeMillis(), Field.Store.YES));
                        doc.add(new StringField("name", "ping", Field.Store.YES));
                        indexWriter.addDocument(doc);
                        // must be invoked for acquire to stop blocking (see below)
                        searcherManager.maybeRefresh();
                        Thread.sleep(100);
                    }
                    // commits all changes (adds, updates, removals) to the index
                    indexWriter.commit();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        writerThread.start();

        while (writerThread.isAlive()) {
            // wait
            Thread.sleep(5000);

            // block until some results are ready (until maybeRefresh is invoked)
            // should find something even though commit was not invoked
            IndexSearcher searcher = searcherManager.acquire();
            Query q = new TermQuery(new Term("name", "ping"));
            TopDocs docs = searcher.search(q, 10);
            System.out.println("Found " + docs.totalHits + " docs for name=ping");


            // release searcher previously acquired
            searcherManager.release(searcher);
        }
    }
}
