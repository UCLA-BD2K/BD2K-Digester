package crawlers;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Outputs all plaintext in a site to a single time-stamped text file.
 *
 * Created by Alan on 6/15/2015.
 */
public abstract class ConcatCrawler extends WebCrawler {
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private BufferedWriter writer;

    public abstract String getCrawlID();
    public abstract String getRootURL();
    public abstract String getOutputPath();
    public abstract Pattern getFiletypeFilters();
    public abstract String[] getURLExclusion();
    public abstract Pattern getSpecialTextPattern();

    @Override
    public void onStart() {
        super.onStart();
        try {
            // Create intermediate directories if necessary
            new File(getOutputPath()).mkdirs();
            // Create new timestamped file
            writer = new BufferedWriter(new FileWriter(
                    new File(getOutputPath() + "/" + getCrawlID() + "_" + dateFormat.format(new Date()) + ".txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeforeExit() {
        super.onBeforeExit();
        try {
            writer.close();
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
        if (!getFiletypeFilters().matcher(href).matches() && href.startsWith(getRootURL())) {
            for (String exclude : getURLExclusion()) {
                if (href.equals(exclude)) {
                    System.out.println("URL: " + href + " (EXCLUDED)");
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        System.out.println("URL: " + page.getWebURL().getURL());

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

            // Write to file
            try {
                writer.write("PAGE: " + htmlParseData.getTitle() + "\n");
                writer.write("URL: " + page.getWebURL().getURL() + "\n");
                writer.newLine();

                writer.write(htmlParseData.getText().replaceAll("[\\\r\\\n]+", "") + "\n"); // Strip excess newlines
                // Find special text in HTML
                Matcher specialMatcher = getSpecialTextPattern().matcher(htmlParseData.getHtml());
                while (specialMatcher.find()) {
                    writer.write(specialMatcher.group(1));
                }
                writer.newLine();

                writer.flush();
            } catch(IOException e) {
                e.printStackTrace();
            }

            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
        }
    }
}
