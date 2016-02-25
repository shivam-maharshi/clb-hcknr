package edu.vt.ideal;

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
public class InsertSmallCollection {
    private static final String PROPERTY_FILENAME = "/ideal.properties";
    private static final String TABLE_NAME = "tweets_small";
    private static final String COLUMN_FAMILY = "raw";

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
        try (InputStream stream = InsertSmallCollection.class.getResourceAsStream(PROPERTY_FILENAME)) {
            props.load(stream);
        }

        // Instantiating Configuration class
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", props.getProperty("cloudera-host"));
        config.set("hbase.zookeeper.property.clientPort", props.getProperty("zookeeper-port"));

        // Checking connection
        HBaseAdmin admin = new HBaseAdmin(config);
        if (!admin.isMasterRunning()) {
            System.out.println("Could not establish connection to Cloudera VM");
            return;
        }
        System.out.println("Successfully connected to HBase Master");

        // Creating small collection table in HBase
        TableName tableName = TableName.valueOf(TABLE_NAME);
        HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
        tableDescriptor.addFamily(new HColumnDescriptor(COLUMN_FAMILY));
        if (!admin.tableExists(tableName)) {
            admin.createTable(tableDescriptor);
            System.out.println("Table [ " + TABLE_NAME + " ] was created");
        }

        // Instantiating HTable class
        HTable hTable = new HTable(config, "tweets_small");

        // Inserting all lines from TSV file to HBase
        for (String line : Files.readAllLines(path)) {
            insertRow(line, hTable);
        }

        // Closing HTable
        hTable.close();
    }

    private static void insertRow(String line, HTable hTable) throws InterruptedIOException, RetriesExhaustedWithDetailsException {
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
        p.add(b("raw"), b("text"), b(parts[1].trim()));

        // Saving the put Instance to the HTable.
        hTable.put(p);
        System.out.println("Inserted " + line);
    }

    private static byte[] b(String str) {
        return Bytes.toBytes(str);
    }
}
