import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Index {

    private static EnglishAnalyzer analyzer = new EnglishAnalyzer();

    private IndexWriter writer;
    private ArrayList<File> queue = new ArrayList<>();

    /**
     * Constructor
     *
     * @param indexDir the name of the folder in which the index should be created
     * @throws java.io.IOException when path isn't found.
     */
    Index(String indexDir) throws IOException {
        Path indDir = Paths.get(indexDir);
        FSDirectory dir = FSDirectory.open(indDir);


        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        writer = new IndexWriter(dir, config);
    }

    /**
     * Indexes a file or directory
     *
     * @param fileName the name of a text file or a folder we wish to add to the index
     * @throws java.io.IOException when path isn't found
     */
    public void indexFileOrDirectory(String fileName) {

        addFiles(new File(fileName));

        int originalNumDocs = writer.numRamDocs();
        for (File f : queue) {
            try {
                Document doc = new Document();

                // Creation of a simpledateformatter in order to print the last-modified-date of our files.
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                String date = sdf.format(f.lastModified());

                if (f.getName().endsWith(".html")) {

                    // Creation of a jsoup document to help us with our html parsing.
                    org.jsoup.nodes.Document htmlFile = Jsoup.parse(f, null);
                    String body = htmlFile.body().text();
                    String title = htmlFile.title();
                    String summary = getSummary(htmlFile);


                    doc.add(new TextField("contents", body + " " + title + " " + date, Field.Store.YES));
                    doc.add(new TextField("title", title, Field.Store.YES));
                    doc.add(new StringField("path", f.getPath(), Field.Store.YES));
                    doc.add(new TextField("modified-date", date, Field.Store.YES));
                    doc.add(new StringField("summary", summary, Field.Store.YES));

                }
                else {
                    String content = FileUtils.readFileToString(f, StandardCharsets.UTF_8);

                    doc.add(new TextField("contents", content + " " + date, Field.Store.YES));
                    doc.add(new StringField("path", f.getPath(), Field.Store.YES));
                    doc.add(new TextField("modified-date", date, Field.Store.YES));
                }
                doc.add(new StringField("filename", f.getName(), Field.Store.YES));

                writer.addDocument(doc);
                System.out.println("Added: " + f);
            } catch (Exception e) {
                System.out.println("Could not add: " + f);
            }
        }

        int newNumDocs = writer.numDocs();
        System.out.println("");
        System.out.println("************************");
        System.out.println((newNumDocs - originalNumDocs) + " documents added.");
        System.out.println("************************");

        queue.clear();
    }

    private void addFiles(File file) {

        if (!file.exists()) {
            System.out.println(file + " does not exist.");
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                addFiles(f);
            }
        } else {
            String filename = file.getName().toLowerCase();
            // We add only html or text files to our queue.
            if (filename.endsWith(".html") || filename.endsWith(".txt")) {
                queue.add(file);
            } else {
                System.out.println("Skipped " + filename);
            }
        }
    }

    /**
     * Close the index
     * @throws java.io.IOException when exception closing
     */
    public void closeIndex() throws IOException {
        writer.close();
    }

    /*
    * A method defined to get the value from the summary tag.
    */

    public String getSummary(org.jsoup.nodes.Document doc) {
        Element summaryEl = doc.getElementsByTag("summary").first();
        return summaryEl != null ? StringUtil.normaliseWhitespace(summaryEl.text()).trim() : "";
    }

}
