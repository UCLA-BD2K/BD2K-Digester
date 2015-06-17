package crawlers;

import java.util.regex.Pattern;

/**
 * Singleton-class to hold DynamicCrawler parameters.
 *
 * Created by Alan on 6/17/2015.
 */
public class CrawlParams {
    private static CrawlParams instance = null;

    public String CRAWL_ID;
    public String OUTPUT_PATH;
    public String ROOT_URL;
    public String[] SEED_URLS;
    public Pattern FILETYPE_FILTERS;
    public String[] URL_EXCLUDES;
    public Pattern SPECIAL_TEXT_PATTERN;

    protected CrawlParams() {

    }

    public static CrawlParams getInstance() {
        if (instance == null) {
            instance = new CrawlParams();
        }

        return instance;
    }

    public void setFiletypeFilters(String pattern) {
        FILETYPE_FILTERS = Pattern.compile(pattern);
    }

    public void setSpecialTextPattern(String pattern) {
        SPECIAL_TEXT_PATTERN = Pattern.compile(pattern);
    }
}
