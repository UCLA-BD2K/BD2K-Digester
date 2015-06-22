package org.heartbd2k.digester.crawlers;

/**
 * DigestCrawler that reads from singleton CrawlerFactory.
 *
 * Created by Alan on 6/17/2015.
 */
public class DynamicCrawler extends DigestCrawler {
    private final CrawlerFactory cf = CrawlerFactory.getInstance();

    @Override
    public String getCrawlID() {
        return cf.getCrawlID();
    }

    @Override
    public String getRootURL() {
        return cf.getRootURL();
    }

    @Override
    public String[] getSeedURLs() {
        return cf.getSeedURLs();
    }

    @Override
    public String getOutputPath() {
        return cf.getOutputPath();
    }

    @Override
    public String getFiletypeFilter() {
        return cf.getFiletypeFilter();
    }

    @Override
    public String[] getURLExcludes() {
        return cf.getURLExcludes();
    }

    @Override
    public String getSpecialTextPattern() {
        return cf.getSpecialTextPattern();
    }
}
