package edu.vt.ideal.helpers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Author: dedocibula
 * Created on: 23.2.2016.
 */
public class HBaseInsertSmallCollection {
    private static final String PROPERTY_FILENAME = "/ideal.properties";

    public static void main(String[] args) throws Exception {
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

        // Instantiating Configuration class
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", props.getProperty("cloudera-host", "localhost"));
        config.set("hbase.zookeeper.property.clientPort", props.getProperty("zookeeper-port", "2181"));

        // Checking connection
        HBaseAdmin admin = new HBaseAdmin(config);
        if (!admin.isMasterRunning()) {
            System.out.println("Could not establish connection to Cloudera VM");
            return;
        }
        System.out.println("Successfully connected to HBase Master");

        // Creating small collection table in HBase
        String collectionName = props.getProperty("collection-name", "tweets");
        String columnFamily = props.getProperty("column-family", "raw");
        TableName tableName = TableName.valueOf(collectionName);
        HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
        tableDescriptor.addFamily(new HColumnDescriptor(columnFamily));
        if (!admin.tableExists(tableName)) {
            admin.createTable(tableDescriptor);
            System.out.println("Table [ " + collectionName + " ] was created");
        }

        // Instantiating HTable class
        HTable hTable = new HTable(config, tableName);

        System.out.println("Inserting lines from: " + smallCollection);

        // Inserting all lines from TSV file to HBase
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null)
                insertRow(line, hTable, columnFamily);
        }

        System.out.println("Lines inserted. Verify HBase.");

        // Closing HTable
        hTable.close();
    }

    private static void insertRow(String line, HTable hTable, String columnFamily) throws InterruptedIOException, RetriesExhaustedWithDetailsException {
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

        // Instantiating Put class
        // accepts a row name.
        Put p = new Put(b(parts[0].trim()));

        // Adding values using add() method
        // Accepts column family name, qualifier/row name ,value
        p.add(b(columnFamily), b("text"), b(parts[1].trim()));

        // Saving the put Instance to the HTable.
        hTable.put(p);
    }

    private static byte[] b(String str) {
        return Bytes.toBytes(str);
    }
}
