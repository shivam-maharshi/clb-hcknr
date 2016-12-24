package edu.vt.ideal;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Author: dedocibula
 * Created on: 19.4.2016.
 */
public final class IDEALTopicIndexer {
    private static Logger logger = Logger.getLogger(IDEALTopicIndexer.class);
    private static final String TABLE_NAME = "ideal-cs5604s16-topic-words";
    private static final String COLUMN_FAMILY = "topics";
    private static final String LABEL_FIELD = "label";
    private static final String COLLECTION_FIELD = "collection_id";
    private static final String WORDS_FIELD = "words";

    private static final int MAX_RESULTS = 3;

    private boolean verboseMode;
    private IndexSearcher searcher;

    IDEALTopicIndexer(boolean verboseMode) {
        this.verboseMode = verboseMode;

        // creating configuration (automatically loads the correct one) and checking connection
        try (Connection connection = ConnectionFactory.createConnection(HBaseConfiguration.create())) {
            if (this.verboseMode)
                logger.info("Established connection to HBase");

            // checking table existence
            TableName tableName = TableName.valueOf(TABLE_NAME);
            try (Admin admin = connection.getAdmin()) {
                if (!admin.tableExists(tableName)) {
                    if (this.verboseMode)
                        logger.error(String.format("Table [ %s ] does not exist", tableName));
                    return;
                }
            }

            // creating in memory index
            Directory index = new RAMDirectory();

            // This line uses deprecated API because of version incompatibilities (Cloudera VM and cluster).
            // The following is non deprecated cluster version:
            //      IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(Version.LATEST, new StandardAnalyzer()));
            //noinspection deprecation
            IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(Version.LUCENE_CURRENT, new StandardAnalyzer(Version.LUCENE_CURRENT)));
            createIndex(connection, tableName, writer);
            writer.close();

            // opening index for searching
            searcher = new IndexSearcher(DirectoryReader.open(index));
        } catch (IOException e) {
            logger.error("Unexpected exception occurred", e);
            e.printStackTrace();
        }
    }

    Set<String> searchTopicLabels(Collection<Term> terms) throws IOException {
        // only if we have an index
        if (searcher == null)
            return null;

        // creating synthetic query
        BooleanQuery query = new BooleanQuery();
        for (Term term : terms) {
            // if query contains explicit collection number narrow the search down to only relevant topics
            if (term.field().equals("colnum_s"))
                query.add(new TermQuery(new Term(COLLECTION_FIELD, term.text())), BooleanClause.Occur.MUST);
            else // else use the text to search in words
                query.add(new TermQuery(new Term(WORDS_FIELD, term.text())), BooleanClause.Occur.SHOULD);
            query.setMinimumNumberShouldMatch(1);
        }
        if (this.verboseMode)
            logger.info(query);

        // getting results
        TopDocs topDocs = searcher.search(query, MAX_RESULTS);
        if (this.verboseMode)
            logger.info(String.format("Found [ %s ] matches", topDocs.totalHits));
        if (topDocs.totalHits > 0) {
            Set<String> labels = new HashSet<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                IndexableField labelField = searcher.doc(scoreDoc.doc).getField(LABEL_FIELD);
                if (labelField != null && labelField.stringValue() != null)
                    labels.add(labelField.stringValue());
            }
            if (this.verboseMode)
                logger.info(String.format("Match found for some of the terms. Topic labels %s", labels));
            return labels;
        }

        return null;
    }

    private void createIndex(Connection connection, TableName tableName, IndexWriter writer) throws IOException {
        try (Table hTable = connection.getTable(tableName)) {
            List<Document> documents = new ArrayList<>();
            for (Result result : hTable.getScanner(new Scan())) {
                String topicLabel;
                String collection;
                String words;

                // extracting fields from HBase
                byte[] rowKey = result.getRow();
                if (rowKey == null) {
                    if (this.verboseMode)
                        logger.error("Skipping HBase row. Couldn't find row key");
                    continue;
                }
                topicLabel = new String(rowKey, StandardCharsets.UTF_8);

                if ((collection = extractValue(result, COLLECTION_FIELD)) == null || (words = extractValue(result, WORDS_FIELD)) == null)
                    continue;

                Document doc = new Document();
                doc.add(new StringField(LABEL_FIELD, topicLabel, Field.Store.YES));
                doc.add(new StringField(COLLECTION_FIELD, collection, Field.Store.YES));
                doc.add(new TextField(WORDS_FIELD, words, Field.Store.YES));

                documents.add(doc);
            }

            // bulk-inserting into Solr (all or nothing)
            writer.addDocuments(documents);
            if (verboseMode)
                logger.info(String.format("Created in-memory index containing topic metadata with [ %s ] documents", documents.size()));
        }
    }

    private String extractValue(Result result, String column) {
        byte[] value = result.getValue(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(column));
        if (value == null) {
            if (this.verboseMode)
                logger.error(String.format("Skipping HBase row. Couldn't find column [ %s ]", column));
        }
        return value != null ? new String(value, StandardCharsets.UTF_8) : null;
    }

    public static void main(String[] args) throws Exception {
        logger.addAppender(new ConsoleAppender(new SimpleLayout()));

        // test run: java -classpath=<path to this JAR> edu.vt.ideal.IDEALTopicIndexer
        new IDEALTopicIndexer(true);
    }
}
