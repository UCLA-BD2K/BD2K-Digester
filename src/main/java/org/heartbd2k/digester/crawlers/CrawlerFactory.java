package org.heartbd2k.digester.crawlers;

/**
 * Singleton-class to hold DynamicCrawler parameters.
 *
 * Created by Alan on 6/17/2015.
 */
public class CrawlerFactory {
    private static CrawlerFactory instance = null;
    private String crawlID;
    private String rootURL;
    private String[] seedURLs;
    private String outputPath;
    private String filetypeFilter;
    private String[] URLExcludes;
    private String specialTextPatterns;
    private boolean nonrecursive;

    protected CrawlerFactory() {
        // Should not be instantiated by public.
    }

    public static CrawlerFactory getInstance() {
        if (instance == null) {
            instance = new CrawlerFactory();
        }

        return instance;
    }

    public Class<? extends DigestCrawler> getCrawler(String crawlID, String rootURL, String[] seedURLs,
                                                     String outputPath, String filetypeFilter, String[] URLExcludes,
                                                     String specialTextPatterns, boolean nonrecusrive) {
        this.crawlID = crawlID;
        this.rootURL = rootURL;
        this.seedURLs = seedURLs;
        this.outputPath = outputPath;
        this.filetypeFilter = filetypeFilter;
        this.URLExcludes = URLExcludes;
        this.specialTextPatterns = specialTextPatterns;
        this.nonrecursive = nonrecusrive;

        return DynamicCrawler.class;
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

    public boolean getNonrecursive() {
        return nonrecursive;
    }
}
