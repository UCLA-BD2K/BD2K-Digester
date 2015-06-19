import crawlers.*;
import org.apache.commons.cli.*;
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

/**
 * usage: Digester -f <arg> [-h] [-o <arg>] [-s <arg>]
 * Get a digest of changes to websites.
 *
 * -f,--filename <arg>   (REQUIRED) Site .xml file to run
 * -h,--help
 * -o,--output <arg>     Output path
 * -s,--siteID <arg>     Specific site ID to run (must be in file)
 *
 * Created by Alan on 6/15/2015.
 */
public class Digester {
    private final static String PROGRAM_NAME = "Digester";
    private final static String DEFAULT_FILETYPE_FILTERS = ".*(\\.(css|gif|js|jpg|png|mp3|mp3|zip|gz|ico))";
    private final static String DEFAULT_OUTPUT_PATH = System.getProperty("user.dir");
    private final static String DIFF_SUBPATH = "/digest/diffs/";
    private final static String CRAWLS_SUBPATH = "/digest/crawls/";

    public static void main(String[] args) {
        // Create options
        Options options = new Options();

        Option.Builder optHelpBuilder = Option.builder("h");
        optHelpBuilder.longOpt("help");
        optHelpBuilder.desc("");
        Option optHelp = optHelpBuilder.build();
        options.addOption(optHelp);

        Option.Builder optFileBuilder = Option.builder("f");
        optFileBuilder.longOpt("filename");
        optFileBuilder.desc("(REQUIRED) Site .xml file to run");
        optFileBuilder.hasArg();
        optFileBuilder.required();
        Option optFile = optFileBuilder.build();
        options.addOption(optFile);

        Option.Builder optSiteBuilder = Option.builder("s");
        optSiteBuilder.longOpt("siteID");
        optSiteBuilder.desc("Specific site ID to run (must be in file)");
        optSiteBuilder.hasArg();
        Option optSite = optSiteBuilder.build();
        options.addOption(optSite);

        Option.Builder optOutputPathBuilder = Option.builder("o");
        optOutputPathBuilder.longOpt("output");
        optOutputPathBuilder.desc("Output path");
        optOutputPathBuilder.hasArg();
        Option optOutputPath = optOutputPathBuilder.build();
        options.addOption(optOutputPath);

        HelpFormatter formatter = new HelpFormatter();
        String header = "Get a digest of changes to websites."  + "\n\n";
        String footer = "\n";

        // Parse command line arguments
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp(PROGRAM_NAME, header, options, footer, true);
            return ;
        }

        // Process arguments
        if (cmd.hasOption(optHelp.getOpt())) {
            formatter.printHelp(PROGRAM_NAME, header, options, footer, true);
            return ;
        }

        String filename = null;
        if (cmd.hasOption(optFile.getOpt())) {
            filename = cmd.getOptionValue(optFile.getOpt());
        }

        String site = null;
        if (cmd.hasOption(optSite.getOpt())) {
            site = cmd.getOptionValue(optSite.getOpt());
        }

        String outputPath = DEFAULT_OUTPUT_PATH;
        if (cmd.hasOption(optOutputPath.getOpt())) {
            outputPath = cmd.getOptionValue(optOutputPath.getOpt());
            System.out.println(outputPath);
        }

        // Execute
        try {
            executeXML(filename, site, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes and runs digest on an XML file
     *
     * @param filename      XML filename
     * @param site          Specific site ID to run
     * @param outputPath    Output path
     * @throws IOException
     */
    public static void executeXML(String filename, String site, String outputPath) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

        // Create intermediate directories if necessary
        String diffOutputPath = outputPath + DIFF_SUBPATH;
        new File(diffOutputPath).mkdirs();
        // Create new timestamped file
        BufferedWriter writer = new BufferedWriter(new FileWriter(
                new File(diffOutputPath + "/DIFF" + "_" + dateFormat.format(new Date()) + ".txt")));

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
        }

        // Iterate through crawl nodes
        assert doc != null;
        doc.getDocumentElement().normalize();
        NodeList crawlNodes = doc.getElementsByTagName("CrawlID");
        for (int i = 0; i < crawlNodes.getLength(); i++) {
            Node node = crawlNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element crawlNode = (Element) node;
                String crawlID = crawlNode.getAttribute("ID");
                String RootURL = crawlNode.getElementsByTagName("RootURL").item(0).getTextContent();

                // Skip if not run only
                if (site != null && !site.equals(crawlID)) {
                    continue;
                }

                // Process seed URLs
                Element seeds = (Element) crawlNode.getElementsByTagName("Seeds").item(0);
                NodeList seedNodes = seeds.getElementsByTagName("SeedURL");
                int numSeeds = seedNodes.getLength();
                String seedList[] = new String[numSeeds];
                for (int j = 0; j < numSeeds; j++) {
                    seedList[j] = seedNodes.item(j).getTextContent();
                    System.out.println(seedList[j]);
                }

                // Process URL excludes
                Element excludes = (Element) crawlNode.getElementsByTagName("Excludes").item(0);
                NodeList excludeNodes = excludes.getElementsByTagName("ExcludeURL");
                int numExcludes = excludeNodes.getLength();
                String excludeList[] = new String[numExcludes];
                for (int j = 0; j < numExcludes; j++) {
                    excludeList[j] = excludeNodes.item(j).getTextContent();
                }

                String specialTextPattern = crawlNode.getElementsByTagName("SpecialText").item(0).getTextContent();

                // Create and run crawler
                String crawlOutputPath = outputPath + CRAWLS_SUBPATH + crawlID;
                DigestCrawler crawler = new DigestCrawler(crawlID, RootURL, seedList, crawlOutputPath,
                        DEFAULT_FILETYPE_FILTERS, excludeList, specialTextPattern);
                List<PageDiff> pageDiffs = crawler.getDigest();

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
