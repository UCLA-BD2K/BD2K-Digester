package crawlers;

import java.util.regex.Pattern;

/**
 * ConcatCrawler that reads from singleton CrawlParams.
 *
 * Created by Alan on 6/17/2015.
 */
public class DynamicCrawler extends ConcatCrawler {
    private final CrawlParams cp = CrawlParams.getInstance();

    @Override
    public String getCrawlID() {
        return cp.CRAWL_ID;
    }

    @Override
    public String getRootURL() {
        return cp.ROOT_URL;
    }

    @Override
    public String[] getSeedURLs() {
        return cp.SEED_URLS;
    }

    @Override
    public String getOutputPath() {
        return cp.OUTPUT_PATH;
    }

    @Override
    public Pattern getFiletypeFilters() {
        return cp.FILETYPE_FILTERS;
    }

    @Override
    public String[] getURLExclusion() {
        return cp.URL_EXCLUDES;
    }

    @Override
    public Pattern getSpecialTextPattern() {
        return cp.SPECIAL_TEXT_PATTERN;
    }
}
