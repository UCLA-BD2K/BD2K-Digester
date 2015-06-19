import crawlers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

import static java.lang.System.exit;

/**
 * Created by Alan on 6/15/2015.
 */
public class Digester {
    private final static String DEFAULT_FILETYPE_FILTERS = ".*(\\.(css|gif|js|jpg|png|mp3|mp3|zip|gz))";

    public static void main(String[] args) throws Exception {
        runFromXML("siteData.xml");
    }

    public static void runFromXML(String filename) {
        File fXmlFile = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            if (dBuilder != null) {
                doc = dBuilder.parse(fXmlFile);
            }
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            exit(-1);
        }

        doc.getDocumentElement().normalize();
        NodeList crawlNodes = doc.getElementsByTagName("CrawlID");
        for (int temp = 0; temp < crawlNodes.getLength(); temp++) {
            Node node = crawlNodes.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element crawlNode = (Element) node;
                String crawlID = crawlNode.getAttribute("ID");
                String RootURL = crawlNode.getElementsByTagName("RootURL").item(0).getTextContent();

                NodeList seedNodes = crawlNode.getElementsByTagName("SeedURLs");
                int numSeeds = seedNodes.getLength();
                String seedList[] = new String[numSeeds];
                for (int i = 0; i < numSeeds; i++) {
                    seedList[i] = seedNodes.item(i).getTextContent();
                }

                NodeList excludeNodes = crawlNode.getElementsByTagName("ExcludeURLs");
                int numExcludes = excludeNodes.getLength();;
                String excludeList[] = new String[numExcludes];
                for (int i = 0; i < numExcludes; i++) {
                    excludeList[i] = excludeNodes.item(i).getTextContent();
                }

                String specialTextPattern = crawlNode.getElementsByTagName("SpecialText").item(0).getTextContent();

                DigestCrawler crawler = new DigestCrawler(crawlID, RootURL, seedList,
                        DEFAULT_FILETYPE_FILTERS, excludeList, specialTextPattern);
                crawler.digest();
            }
        }
    }
}
