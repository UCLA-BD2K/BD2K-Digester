import crawlers.*;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.File;

/**
 * Created by Alan on 6/15/2015.
 */
public class Digester {
    public static void main(String[] args) throws Exception {
        getDigest(new ENIGMACrawler());
        getDigest(new HarvardCrawler());
        getDigest(new LINCSDCICCrawler());
        getDigest(new MobilizeCrawler());
        getDigest(new StanfordCrawler());
        getDigest(new UCLACrawler());
        getDigest(new UCSCCrawler());
        getDigest(new UIUCCrawler());
        getDigest(new UMemphisCrawler());
        getDigest(new UPittCrawler());
        getDigest(new USCCrawler());
        getDigest(new UWiscCrawler());
    }

    /**
     * Runs a given crawler and returns any changes since the last run (if exists).
     *
     * @param crawler
     */
    public static void getDigest(ConcatCrawler crawler) {
        // Get previous site state file
        File prev = getLatestFileFromDir(crawler.getOutputPath());

        // Get latest crawl
        try {
            crawl(crawler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Compare with previous state if exists
        if (prev != null) {
            DiffHelper.printPatch(
                    DiffHelper.compare(prev.getPath(), getLatestFileFromDir(crawler.getOutputPath()).getPath()));
        }
    }

    /**
     * Runs a given crawler.
     *
     * @param crawler
     * @throws Exception
     */
    public static void crawl(ConcatCrawler crawler) throws Exception {
        String crawlStorageFolder = "data/crawl/root";
        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed(crawler.getRootURL());

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(crawler.getClass(), numberOfCrawlers);
    }

    private static File getLatestFileFromDir(String dirPath){
        File dir = new File(dirPath);
        if (dir == null) {
            return null;
        }

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
