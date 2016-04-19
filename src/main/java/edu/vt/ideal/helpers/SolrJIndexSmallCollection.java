package edu.vt.ideal.helpers;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Author: dedocibula
 * Created on: 28.2.2016.
 */
public class SolrJIndexSmallCollection {
    private static final String PROPERTY_FILENAME = "/ideal.properties";
    private static final String PATTERN = "http://%s:%s/solr/%s";

    public static void main(String[] args) throws IOException, SolrServerException {
        // Loading TSV file based on the commandline arguments
        if (args.length < 1) {
            System.out.println("Please specify parameters: <small_collection_file>");
            return;
        }

        // Get small collection name
        String smallCollection = args[0];
        if (smallCollection == null || smallCollection.isEmpty()) {
            System.out.println("Small collection name cannot be null or empty");
            return;
        }

        // Check if file exists
        Path path = Paths.get(smallCollection);
        if (Files.notExists(path) || !Files.isRegularFile(path) || !Files.isReadable(path)) {
            System.out.println("No readable file found: " + smallCollection);
            return;
        }

        // Loading properties from ideal.properties
        Properties props = new Properties();
        try (InputStream stream = HBaseInsertSmallCollection.class.getResourceAsStream(PROPERTY_FILENAME)) {
            props.load(stream);
        }

        // Instantiating Solr client
        String url = String.format(PATTERN, props.getProperty("cloudera-host", "localhost"),
                props.getProperty("solr-port", "8983"),
                props.getProperty("collection-name", "tweets"));
        SolrServer client = new HttpSolrServer(url);
        try {
            // Checking if Solr is available
            client.ping();
        } catch (SolrServerException e) {
            System.out.println("Could not establish connection to Cloudera VM");
            return;
        }
        System.out.println("Successfully connected to Solr");

        System.out.println("Inserting lines from: " + smallCollection);

        // Inserting all lines from TSV file to Solr
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                insertRow(line, client);
                if (i % 100 == 0)
                    client.commit();
                i++;
            }
        }
        client.commit();

        System.out.println("Lines inserted. Verify Solr.");

        // Closing Solr client
        client.shutdown();
    }

    private static void insertRow(String line, SolrServer client) {
        if (line == null) {
            System.out.println("Null line");
            return;
        }

        // Splitting line by tabulator
        String[] parts = line.split("\t");
        if (parts.length < 2) {
            System.out.println("Malformed line: " + line);
            return;
        }

        // Instantiating Solr Input document class
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", parts[0].trim());
        document.addField("text", parts[1].trim());

        // Saving input document to Solr
        try {
            client.add(document);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }
}
