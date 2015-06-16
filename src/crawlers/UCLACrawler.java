package crawlers;

import java.util.regex.Pattern;

/**
 * Created by Alan on 6/16/2015.
 */
public class UCLACrawler extends ConcatCrawler {
    private final static String CRAWL_ID = "UCLA";
    private final static String ROOT_URL = "http://www.heartbd2k.org/";
    private final static String OUTPUT_PATH = "data/" + CRAWL_ID;
    private final static Pattern FILETYPE_FILTERS = Pattern.compile(".*(\\.(css|gif|js|jpg"
            + "|png|mp3|mp3|zip|gz))$");
    private final static String[] URL_EXCLUDES = {"http://www.heartbd2k.org/cdn-cgi/l/email-protection"};
    private final static Pattern SPECIAL_TEXT_PATTERN = Pattern.compile("<span class='description'>(.*)<\\/span>");

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
