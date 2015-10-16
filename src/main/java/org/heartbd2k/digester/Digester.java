package org.heartbd2k.digester;

import org.apache.commons.cli.*;
import org.heartbd2k.digester.crawlers.DigestCrawler;
import org.heartbd2k.digester.crawlers.PageDiff;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * usage: Digester -f <arg> [-h] [-o <arg>] [-s <arg>]
 * Get a digest of changes to websites.
 * <p/>
 * -f,--filename <arg>   (REQUIRED) Site .xml file to run
 * -h,--help
 * -o,--output <arg>     Output path
 * -s,--siteID <arg>     Specific site ID to run (must be in file)
 * <p/>
 * Created by Alan on 6/15/2015.
 */
public class Digester {
    private final static String PROGRAM_NAME = "Digester";
    private final static String DEFAULT_FILETYPE_FILTERS = ".*(\\.(css|gif|js|jpg|png|mp3|mp3|zip|gz|ico))";
    private final static String DEFAULT_OUTPUT_PATH = System.getProperty("user.dir");
    private final static String DIFF_SUBPATH = "/digest/diffs/";
    private final static String CRAWLS_SUBPATH = "/digest/crawls/";
    private final static String EMAIL_PROP_PATH = "email.prop";

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

        Option.Builder optReportBuilder = Option.builder("r");
        optReportBuilder.longOpt("report");
        optReportBuilder.desc("Send a report to the following semi-color separated emails from " + EMAIL_PROP_PATH);
        optReportBuilder.hasArg();
        Option optReport = optReportBuilder.build();
        options.addOption(optReport);

        HelpFormatter formatter = new HelpFormatter();
        String header = "Get a digest of changes to websites." + "\n\n";
        String footer = "\n";

        // Parse command line arguments
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp(PROGRAM_NAME, header, options, footer, true);
            return;
        }

        // Process arguments
        if (cmd.hasOption(optHelp.getOpt())) {
            formatter.printHelp(PROGRAM_NAME, header, options, footer, true);
            return;
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

        List<String> recipients = new ArrayList<>();
        if (cmd.hasOption(optReport.getOpt())) {
            String[] emails = cmd.getOptionValue(optReport.getOpt()).split(";");
            recipients = Arrays.asList(emails);
        }

        // Execute
        try {
            executeXML(filename, site, outputPath, recipients);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes and runs digest on an XML file
     *
     * @param filename   XML filename
     * @param site       Specific site ID to run
     * @param outputPath Output path
     * @throws IOException
     */
    public static void executeXML(String filename, String site, String outputPath,
                                  List<String> recipients) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

        // Create intermediate directories if necessary
        String diffOutputPath = outputPath + DIFF_SUBPATH;
        new File(diffOutputPath).mkdirs();
        // Create new timestamped file
        String diffReport = diffOutputPath + "/DIFF" + "_" + dateFormat.format(new Date()) + ".txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(diffReport)));

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

        // Setup statistics
        int totalPagesChanged = 0;
        Map<String, Integer> siteChangeMap = new HashMap<>();

        // Iterate through crawl nodes
        assert doc != null;
        doc.getDocumentElement().normalize();
        NodeList crawlNodes = doc.getElementsByTagName("CrawlID");
        for (int siteIndex = 0; siteIndex < crawlNodes.getLength(); siteIndex++) {
            Node node = crawlNodes.item(siteIndex);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element crawlNode = (Element) node;
                String crawlID = crawlNode.getAttribute("ID");
                String rootURL = crawlNode.getElementsByTagName("RootURL").item(0).getTextContent();

                // Get nonrecursive flag
                boolean nonrecursive = crawlNode.getAttribute("nonrecursive").equals("true");

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

                // Get special text patterns
                String specialTextPattern = "";
                NodeList specialNodes = crawlNode.getElementsByTagName("SpecialText");
                if (specialNodes != null && specialNodes.getLength() > 0) {
                    specialTextPattern = specialNodes.item(0).getTextContent();
                }

                // Create and run crawler
                String crawlOutputPath = outputPath + CRAWLS_SUBPATH + crawlID;
                DigestCrawler crawler = new DigestCrawler(crawlID, rootURL, seedList, crawlOutputPath,
                        DEFAULT_FILETYPE_FILTERS, excludeList, specialTextPattern, nonrecursive);
                List<PageDiff> pageDiffs = crawler.getDigest();

                if (pageDiffs != null && !pageDiffs.isEmpty()) {
                    // Output results to diff file only if there are changes
                    writer.write(crawler.getCrawlID() + "\n");
                    writer.write(crawler.getRootURL() + "\n");

                    // Create horizontal line
                    for (int i = 0; i < 80; i++) {
                        writer.write("=");
                    }
                    writer.newLine();

                    String currURL = null;
                    int pagesChanged = 0;
                    for (PageDiff pageDiff : pageDiffs) {
                        System.out.println(pageDiff);
                        try {
                            // Write URL for each page
                            if (currURL == null || !pageDiff.URL.equals(currURL)) {
                                currURL = pageDiff.URL;
                                writer.write(pageDiff.URL + "\n");
                                for (int i = 0; i < 80; i++) {
                                    writer.write("-");
                                }
                                writer.newLine();

                                pagesChanged++;
                            }

                            writer.write("From" + "\n" + pageDiff.delta.getRevised().getLines() + "\n");
                            writer.write("To " + "[Position: " + pageDiff.delta.getRevised().getPosition() +
                                    " Size: " + pageDiff.delta.getRevised().size() + " ]" + "\n" +
                                    pageDiff.delta.getOriginal().getLines() + "\n\n\n");
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    totalPagesChanged += pagesChanged;
                    siteChangeMap.put(crawler.getCrawlID(), pagesChanged);
                }
                writer.newLine();

                // Delete all but latest crawl
                cleanDirectory(crawlOutputPath);
            }
        }

        writer.close();

        // Send report email if necessary
        if (recipients != null && !recipients.isEmpty()) {
            // Generate email body
            StringBuilder body = new StringBuilder();
            body.append("Total pages changed: ").append(totalPagesChanged).append("\n");
            body.append("Sites changed:\n");
            for (Map.Entry pair : siteChangeMap.entrySet()) {
                body.append(pair.getKey()).append(" (").append(pair.getValue()).append(")\n");
            }

            Email.send(EMAIL_PROP_PATH, recipients, "BD2K Crawl Report " + dateFormat.format(new Date()),
                    body.toString(), diffReport);
            System.out.println("Emails sent to " + recipients);
        }
    }

    /**
     * Deletes all files but the last file from the directory.
     *
     * @param dirPath Directory to clean.
     */
    private static void cleanDirectory(String dirPath) {
        File dir = new File(dirPath);

        File[] files = dir.listFiles();
        if (files == null || files.length <= 1) {
            return;
        }

        // Delete all but last modified
        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile.delete();
                lastModifiedFile = files[i];
            }
        }
    }
}
