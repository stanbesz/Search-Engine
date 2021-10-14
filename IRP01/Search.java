import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Search {
    private static EnglishAnalyzer analyzer = new EnglishAnalyzer();

    static Path path;

    public static void search(String indexPath) {
        path = Paths.get(indexPath);

        IndexReader reader = null;

        try {
            reader = DirectoryReader.open(FSDirectory.open(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        IndexSearcher searcher = new IndexSearcher(reader);
        String s;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        do {
            TopScoreDocCollector collector = TopScoreDocCollector.create(10);
            s = "";
            try {
                System.out.println();
                System.out.println("Enter the search query (q=quit):");
                s = br.readLine();
                if (s.equalsIgnoreCase("q")) {
                    break;
                }
                Query q = new QueryParser("contents", analyzer).parse(s);
                searcher.search(q, collector);
                ScoreDoc[] hits = collector.topDocs().scoreDocs;

                // Print results
                System.out.println("Found " + hits.length + " hits.");
                for (int i = 0; i < hits.length; ++i) {
                    int docId = hits[i].doc;
                    Document d = searcher.doc(docId);
                    if (d.get("path").endsWith(".html")) {
                        System.out.println((i + 1) + ". " + d.get("path") + ";" + " score=" + hits[i].score + ";" + " title=" + d.get("title") + ";" + " summary=" + d.get("summary") + ";" +  " last-modified-date: " + d.get("modified-date") + ";");
                    } else {
                        System.out.println((i + 1) + ". " + d.get("path") + ";" + " score=" + hits[i].score + ";" + " last-modified-date: " + d.get("modified-date") + ";");
                    }
                    System.out.println("----------------------------------------");
                }
            } catch (Exception e) {
                System.out.println("Error searching " + s + " : " + e.getMessage());
            }
        } while (!s.equalsIgnoreCase("q"));
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
