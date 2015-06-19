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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.lang.System.exit;

/**
 * Created by Alan on 6/15/2015.
 */
public class Digester {
    private final static String DEFAULT_FILETYPE_FILTERS = ".*(\\.(css|gif|js|jpg|png|mp3|mp3|zip|gz|ico))";

    private final static String DIFF_OUTPUT_FOLDER = "data/diffs/";
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private static BufferedWriter writer;

    public static void main(String[] args) {
        try {
            // Create intermediate directories if necessary
            new File(DIFF_OUTPUT_FOLDER).mkdirs();
            // Create new timestamped file
            writer = new BufferedWriter(new FileWriter(
                    new File(DIFF_OUTPUT_FOLDER + "DIFF" + "_" + dateFormat.format(new Date()) + ".txt")));
            runFromXML("siteData.xml", "UCLA");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runFromXML(String filename, String runOnlyID) throws IOException {
        // Open and run sites from XML file
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

        // Iterate through crawl nodes
        doc.getDocumentElement().normalize();
        NodeList crawlNodes = doc.getElementsByTagName("CrawlID");
        for (int temp = 0; temp < crawlNodes.getLength(); temp++) {
            Node node = crawlNodes.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element crawlNode = (Element) node;
                String crawlID = crawlNode.getAttribute("ID");
                String RootURL = crawlNode.getElementsByTagName("RootURL").item(0).getTextContent();

                // Skip if not run only
                if (runOnlyID != null && !runOnlyID.equals(crawlID)) {
                    continue;
                }

                // Process seed URLs
                NodeList seedNodes = crawlNode.getElementsByTagName("SeedURLs");
                int numSeeds = seedNodes.getLength();
                String seedList[] = new String[numSeeds];
                for (int i = 0; i < numSeeds; i++) {
                    seedList[i] = seedNodes.item(i).getTextContent();
                }

                // Process URL excludes
                NodeList excludeNodes = crawlNode.getElementsByTagName("ExcludeURLs");
                int numExcludes = excludeNodes.getLength();
                String excludeList[] = new String[numExcludes];
                for (int i = 0; i < numExcludes; i++) {
                    excludeList[i] = excludeNodes.item(i).getTextContent();
                }

                String specialTextPattern = crawlNode.getElementsByTagName("SpecialText").item(0).getTextContent();

                // Create and run crawler
                DigestCrawler crawler = new DigestCrawler(crawlID, RootURL, seedList,
                        DEFAULT_FILETYPE_FILTERS, excludeList, specialTextPattern);
                List<PageDiff> pageDiffs = crawler.digest();

                // Output results to diff file
                writer.write(crawler.getCrawlID() + "\n");
                writer.write(crawler.getRootURL() + "\n");
                if (pageDiffs == null || pageDiffs.isEmpty()) {
                    writer.write("No changes" + "\n");
                } else {
                    pageDiffs.forEach((PageDiff pageDiff) -> {
                        System.out.println(pageDiff);
                        try {
                            writer.write(pageDiff.URL + "\n");
                            writer.write(pageDiff.delta.toString() + "\n");
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                writer.newLine();
            }
        }

        writer.close();
    }
}
