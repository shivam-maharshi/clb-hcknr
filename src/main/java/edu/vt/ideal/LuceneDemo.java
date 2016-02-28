package edu.vt.ideal;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * Author: dedocibula
 * Created on: 28.2.2016.
 */
public class LuceneDemo {
    public static void main(String[] args) throws IOException, ParseException {
        // filters tokenizer with standard, lowercase and stopwords filters
        StandardAnalyzer analyzer = new StandardAnalyzer();
        // in-memory index (directory)
        Directory index = new RAMDirectory();

        // configuration for index writer (create, append, create_append)
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter writer = new IndexWriter(index, config);
        addDocument(writer, "Lucene in Action", "193398817");
        addDocument(writer, "Lucene for Dummies", "55320055Z");
        addDocument(writer, "Managing Gigabytes", "55063554A");
        addDocument(writer, "The Art of Computer Science", "9900333X");
        // commits and closes writer
        writer.close();

        String queryString = args.length > 0 ? args[0] : "lucene";
        // constructs query title="<value>" for queryString
        Query query = new QueryParser("title", analyzer).parse(queryString);

        int hitsPerPage = 10;
        // accesses point-in-time view of the index
        IndexReader reader = DirectoryReader.open(index);
        // implements search over single reader
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs result = searcher.search(query, hitsPerPage);
        ScoreDoc[] hits = result.scoreDocs;

        System.out.println(String.format("Found %s hits.", hits.length));
        for (ScoreDoc hit : hits) {
            Document document = searcher.doc(hit.doc);
            System.out.println(String.format("Book { title: %s, isbn: %s }", document.get("title"), document.get("isbn")));
        }
    }

    private static void addDocument(IndexWriter writer, String title, String isbn) throws IOException {
        Document document = new Document();
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new StringField("isbn", isbn, Field.Store.YES));
        writer.addDocument(document);
    }
}
