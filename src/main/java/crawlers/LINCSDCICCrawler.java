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

/**
 * Special DynamicCrawler for LINCS-DCIC. Concats all js/data files in addition to standard links.
 *
 * Created by Alan on 6/16/2015.
 */
public class LINCSDCICCrawler extends DynamicCrawler {
    private Set<String> jsDB;

    protected LINCSDCICCrawler() {
        // Should not be instantiated by public.
        super();
    }

    @Override
    public void onStart() {
        super.onStart();
        jsDB = new HashSet<>();
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
                String jsURL = element.attr("src");
                if (jsURL != null && !jsURL.isEmpty() && jsURL.startsWith("js/data/") && !jsDB.contains(jsURL)) {
                    jsDB.add(jsURL);
                    System.out.println(jsURL);
                    GetRequest request = Unirest.get(getRootURL() + jsURL);
                    try {
                        getWriter().write("JS: " + jsURL + "\n");
                        getWriter().newLine();
                        getWriter().write(request.asString().getBody() + "\n");
                        getWriter().newLine();
                    } catch (IOException | UnirestException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        super.visit(page);
    }
}
