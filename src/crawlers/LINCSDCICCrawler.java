package crawlers;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Special ConcatCrawler for LINCS-DCIC. Concats all js/data files in addition to standard links.
 *
 * Created by Alan on 6/16/2015.
 */
public class LINCSDCICCrawler extends ConcatCrawler {
    private final static String CRAWL_ID = "LINCS-DCIC";
    private final static String ROOT_URL = "http://lincs-dcic.org/";
    private final static String OUTPUT_PATH = "data/" + CRAWL_ID;
    private final static Pattern FILETYPE_FILTERS = Pattern.compile(".*(\\.(css|gif|jpg|png|mp3|mp3|zip|gz))$");
    private final static String[] URL_EXCLUDES = {};
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

    private Set<String> jsDB;

    @Override
    public void onStart() {
        super.onStart();
        jsDB = new HashSet<String>();
    }

    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {

            // Check for js/data files
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            Document doc = Jsoup.parseBodyFragment(htmlParseData.getHtml());
            Elements scriptElements = doc.getElementsByTag("script");
            for (Element element :scriptElements ){
                // Write js/data to file and add to DB if not discovered
                String js = element.attr("src");
                if (js != null && js != "" && js.startsWith("js/data/") && !jsDB.contains(js)) {
                    jsDB.add(js);
                    System.out.println(js);
                    GetRequest request = Unirest.get(getRootURL() + js);
                    try {
                        getWriter().write("JS: " + js + "\n");
                        getWriter().newLine();
                        getWriter().write(request.asString().getBody().toString());
                        getWriter().newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UnirestException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        super.visit(page);
    }
}
