import crawlers.*;

/**
 * Created by Alan on 6/15/2015.
 */
public class Digester {
    public static void main(String[] args) throws Exception {
        // TODO Read sites from file
        DigestCrawler memphis = new DigestCrawler("UCSC", "https://genomics.soe.ucsc.edu/bd2k", new String[0],
                ".*(\\.(css|gif|js|jpg|png|mp3|mp3|zip|gz))", new String[0], "");
        memphis.digest();
    }
}
