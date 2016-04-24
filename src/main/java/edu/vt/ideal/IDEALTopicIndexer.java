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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
    private static final String PROBABILITIES_FIELD = "probabilities";

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

    String searchTopicLabel(Collection<Term> terms) throws IOException {
        if (searcher == null)
            return null;

        BooleanQuery query = new BooleanQuery();
        for (Term term : terms)
            query.add(new TermQuery(new Term(WORDS_FIELD, term.text())), BooleanClause.Occur.SHOULD);
        if (this.verboseMode)
            logger.info(query);

        TopDocs topDocs = searcher.search(query, 1);
        if (topDocs.totalHits > 0) {
            String label = searcher.doc(topDocs.scoreDocs[0].doc).getField(LABEL_FIELD).stringValue();
            if (this.verboseMode)
                logger.info(String.format("Match found for some of the terms. Topic label [ %s ]", label));
            return label;
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
                String probabilities;

                // extracting fields from HBase
                if ((topicLabel = extractValue(result, LABEL_FIELD)) == null || (collection = extractValue(result, COLLECTION_FIELD)) == null ||
                        (words = extractValue(result, WORDS_FIELD)) == null || (probabilities = extractValue(result, PROBABILITIES_FIELD)) == null)
                    continue;

                Document doc = new Document();
                doc.add(new StringField(LABEL_FIELD, topicLabel, Field.Store.YES));
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

        IDEALTopicIndexer indexer = new IDEALTopicIndexer(true);
        indexer.searchTopicLabel(new HashSet<Term>() {{add(new Term("text", "condition"));}});
    }
}
