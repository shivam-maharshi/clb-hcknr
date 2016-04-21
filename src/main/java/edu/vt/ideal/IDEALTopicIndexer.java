package edu.vt.ideal;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Author: dedocibula
 * Created on: 19.4.2016.
 */
public final class IDEALTopicIndexer {
    private static Logger logger = Logger.getLogger(IDEALTopicIndexer.class);
    private static final String TABLE_NAME = "ideal-CS5604s16-topic-words";
    private static final String COLUMN_FAMILY = "topics";
    private static final String LABEL_FIELD = "label";
    private static final String COLLECTION_FIELD = "collection_id";
    private static final String WORDS_FIELD = "words";
    private static final String PROBABILITIES_FIELD = "probabilities";

    private Configuration configuration;
    private boolean verboseMode;

    private IndexSearcher searcher;

    private IDEALTopicIndexer(InputStream configStream, boolean verboseMode) {
        this.verboseMode = verboseMode;

        if (configStream == null) {
            logger.warn("HBase configuration file not found. Initializing empty topic index");
            return;
        }

        // creating configuration
        configuration = HBaseConfiguration.create();
        configuration.addResource(configStream);

        // checking connection
        try {
            HBaseAdmin admin = new HBaseAdmin(configuration);
            if (!admin.isMasterRunning()) {
                if (this.verboseMode)
                    logger.error("Could not establish connection to HBase");
                return;
            }
            if (this.verboseMode)
                logger.info("Established connection to HBase");

            // checking table existence
            TableName tableName = TableName.valueOf(TABLE_NAME);
            if (!admin.tableExists(tableName)) {
                if (this.verboseMode)
                    logger.error(String.format("Table [ %s ] does not exist", tableName));
                return;
            }

            // creating in memory index
            Directory index = new RAMDirectory();
            IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(Version.LUCENE_4_10_3, new StandardAnalyzer()));
            createIndex(writer);
            writer.close();

            // opening index for searching
            searcher = new IndexSearcher(DirectoryReader.open(index));
        } catch (IOException e) {
            logger.error("Unexpected exception occurred", e);
            e.printStackTrace();
        }
    }

    public String searchTopicLabel(Collection<Term> terms) throws IOException {
        if (searcher == null)
            return null;

        BooleanQuery query = new BooleanQuery();
        for (Term term : terms)
            query.add(new TermQuery(new Term(WORDS_FIELD, term.text())), BooleanClause.Occur.SHOULD);
        if (this.verboseMode)
            logger.info(query);

        TopDocs topDocs = searcher.search(query, 1);
        if (topDocs.totalHits > 1) {
            String label = searcher.doc(topDocs.scoreDocs[0].doc).getField(LABEL_FIELD).stringValue();
            if (this.verboseMode)
                logger.info(String.format("Match found for some of the terms. Topic label [ %s ]", label));
            return label;
        }

        return null;
    }

    private void createIndex(IndexWriter writer) throws IOException {
        HTable hTable = new HTable(this.configuration, TABLE_NAME);

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

        hTable.close();
    }

    private String extractValue(Result result, String column) {
        byte[] value = result.getValue(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(column));
        if (value == null) {
            if (this.verboseMode)
                logger.error(String.format("Skipping HBase row. Couldn't find column [ %s ]", column));
        }
        return value != null ? new String(value, StandardCharsets.UTF_8) : null;
    }

    static IDEALTopicIndexer create(String configFile, boolean verboseMode) {
        if (configFile == null || configFile.isEmpty()) {
            System.out.println("HBase-site.xml path cannot be null or empty");
            return new IDEALTopicIndexer(null, verboseMode);
        }

        Path path = Paths.get(configFile);
        if (Files.notExists(path) || !Files.isRegularFile(path) || !Files.isReadable(path)) {
            System.out.println("No readable config file found: " + configFile);
            return new IDEALTopicIndexer(null, verboseMode);
        }

        try {
            return new IDEALTopicIndexer(new FileInputStream(path.toFile()), true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Please specify parameters: <hbase-site.xml path>");
            return;
        }

        create(args[0], true);
    }
}
