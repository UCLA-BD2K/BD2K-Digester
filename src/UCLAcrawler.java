import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Alan on 6/15/2015.
 */
public final class UCLAcrawler extends WebCrawler {
    public final static String CRAWLER_ID = "UCLA";
    public final static String ROOT_URL = "http://www.heartbd2k.org/";

    private final static String OUTPUT_PATH = "data/" + CRAWLER_ID;

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|gif|js|jpg"
            + "|png|mp3|mp3|zip|gz))$");

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private File file;
    private BufferedWriter output;
    
    @Override
    public void onStart() {
        super.onStart();
        try {
            // Create intermediate directories if necessary
            file = new File(OUTPUT_PATH);
            file.mkdirs();
            // Create new timestamped file
            file = new File(OUTPUT_PATH + "/" + CRAWLER_ID + "_" + dateFormat.format(new Date()) + ".txt");
            output = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeforeExit() {
        super.onBeforeExit();
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && href.startsWith(ROOT_URL);
    }


    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();

            // Write to file
            // TODO Check for captions with regexp: "/<span class='description'>(.+)<\/span>/g"
            try {
                output.write(text);
            } catch(IOException e) {
                e.printStackTrace();
            }

            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
        }
    }
}
