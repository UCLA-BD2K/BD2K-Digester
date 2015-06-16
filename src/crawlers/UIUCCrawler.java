package crawlers;

import java.util.regex.Pattern;

/**
 * Created by Sneha on 6/16/2015.
 */
public class UIUCCrawler extends ConcatCrawler {
    private final static String CRAWL_ID = "UIUC";
    private final static String ROOT_URL = "http://knoweng.org/";
    private final static String OUTPUT_PATH = "data/" + CRAWL_ID;
    private final static Pattern FILETYPE_FILTERS = Pattern.compile(".*(\\.(css|gif|js|jpg|png|mp3|mp3|zip|gz))$");
    private final static String[] URL_EXCLUDES = {""};
    private final static Pattern SPECIAL_TEXT_PATTERN = Pattern.compile("");

    @Override
    public String getCrawlID() {
        return CRAWL_ID;
    }

    @Override
    public String getRootURL() {
        return ROOT_URL;
    }

    @Override
    public String getOutputPath() {
        return OUTPUT_PATH;
    }

    @Override
    public Pattern getFiletypeFilters() {
        return FILETYPE_FILTERS;
    }

    @Override
    public String[] getURLExclusion() {
        return URL_EXCLUDES;
    }

    @Override
    public Pattern getSpecialTextPattern() {
        return SPECIAL_TEXT_PATTERN;
    }
}
