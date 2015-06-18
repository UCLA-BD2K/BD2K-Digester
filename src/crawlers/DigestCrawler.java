package crawlers;

import difflib.Patch;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;
import utils.DiffHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Outputs all plaintext in a site to a single time-stamped text file, and can also run digests to find differences
 * between several runs.
 *
 * Created by Alan on 6/15/2015.
 */
public class DigestCrawler extends WebCrawler {
    private final static int NUM_CRAWLERS = 4;
    private final static String USER_AGENT_NAME = "UCLA BD2K";
    private final static String CRAWL_STORAGE_FOLDER = "temp/crawl/root";

    private String crawlID;
    private String rootURL;
    private String[] seedURLs;
    private String outputPath;
    private String filetypeFilter;
    private String[] URLExcludes;
    private String specialTextPatterns;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private BufferedWriter writer;

    protected DigestCrawler() {
        // Empty constructor to be used only by subclasses.
    }

    public DigestCrawler(String crawlID, String rootURL, String[] seedURLs,
                         String filetypeFilter, String[] URLExcludes, String specialTextPatterns) {
        this.crawlID = crawlID;
        this.rootURL = rootURL;
        this.seedURLs = seedURLs;
        this.outputPath = "data/crawls/" + crawlID;
        this.filetypeFilter = filetypeFilter;
        this.URLExcludes = URLExcludes;
        this.specialTextPatterns = specialTextPatterns;
    }

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

    protected BufferedWriter getWriter() {
        return writer;
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

        // Filter filetypes
        if (Pattern.compile(getFiletypeFilter()).matcher(href).find()) {
            return false;
        }

        // Filter domain
        if (!href.startsWith(getRootURL())) {
            return false;
        }

        // Filter excluded URLs
        for (String exclude : getURLExcludes()) {
            if (href.startsWith(exclude)) {
                return false;
            }
        }

        System.out.println(href);
        return true;
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

            // Write to file
            try {
                writer.write("PAGE: " + htmlParseData.getTitle());
                writer.newLine();
                writer.write("URL: " + page.getWebURL().getURL());
                writer.newLine();
                writer.newLine();

                writer.write(htmlParseData.getText().replaceAll("[\\\r\\\n]+", "")); // Strip excess newlines
                writer.newLine();
                // Find special text in HTML
                Matcher specialMatcher = Pattern.compile(getSpecialTextPattern()).matcher(htmlParseData.getHtml());
                writer.newLine();
                while (specialMatcher.find()) {
                    writer.write(specialMatcher.group(1));
                    writer.newLine();
                }
                writer.newLine();

                writer.flush();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Runs the given crawler and returns any changes since the last run (if exists).
     */
    public Patch<String> digest() {
        // Get previous site state file
        File prev = getLatestFileFromDir(getOutputPath());

        // Get latest crawl
        try {
            crawl();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Compare with previous state if exists
        if (prev != null) {
            Patch<String> patch = DiffHelper.compare(prev.getPath(), getLatestFileFromDir(getOutputPath()).getPath());
            DiffHelper.printPatch(patch);
            return patch;
        }

        return null;
    }

    /**
     * Runs the crawler by creating a setting the variables in CrawlerFactor, which returns a DynamicCrawler literal
     * that handles the actual running.
     *
     * @throws Exception
     */
    public void crawl() throws Exception {
        // Required for HTTPS sites; see http://stackoverflow.com/a/14884941
        System.setProperty("jsse.enableSNIExtension", "false");

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(CRAWL_STORAGE_FOLDER);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(false); // Ignore robots.txt
        robotstxtConfig.setUserAgentName(USER_AGENT_NAME);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed(getRootURL());
        // Add extra seed URLs
        for (String url : getSeedURLs()) {
            controller.addSeed(url);
        }

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        System.out.println("Starting " + getCrawlID() + " crawl");
        controller.start(CrawlerFactory.getInstance().getCrawler(crawlID, rootURL, seedURLs, outputPath,
                filetypeFilter, URLExcludes, specialTextPatterns), NUM_CRAWLERS);
        System.out.println("Crawl complete");
    }

    public String getCrawlID() {
        return crawlID;
    }

    public String getRootURL() {
        return rootURL;
    }

    public String[] getSeedURLs() {
        return seedURLs;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getFiletypeFilter() {
        return filetypeFilter;
    }

    public String[] getURLExcludes() {
        return URLExcludes;
    }

    public String getSpecialTextPattern() {
        return specialTextPatterns;
    }

    private static File getLatestFileFromDir(String dirPath){
        File dir = new File(dirPath);

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }

        return lastModifiedFile;
    }
}
