


import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.Scanner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class XMLWriter {

    public static void main(String args[]) throws Exception{

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Links");
            doc.appendChild(rootElement);

            int numValues = 12;
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            String in = bf.readLine();
            Scanner scan = new Scanner(System.in);
            String[] ids = new String[numValues];
            String[] url = new String[numValues];
            String[] outputPath = new String[numValues];
            String[][] seed = new String[numValues][];
            String[][] excludes = new String[numValues][];
            String [] specialTextPattern = new String[numValues];
            System.out.print("Enter the values: ");
            int k, l;
            for(int i = 0; i < numValues; i++)
            {
                System.out.print("Enter the values: ");
                System.out.print("Crawl ID " + String.valueOf(i + 1) + ": ");
                in = bf.readLine();
                ids[i] = in;
                System.out.print("Root URL " + String.valueOf(i + 1) + ": ");
                in = bf.readLine();
                url[i] = in;
                System.out.print("Output Path " + String.valueOf(i + 1) + ": ");
                in = bf.readLine();
                outputPath[i] = in;
                System.out.print("Seed " + String.valueOf(i + 1) + ": ");
                System.out.print("Enter number of seeds ");
                k = scan.nextInt();
                for(int j = 0; j < k; j++)
                {
                    in = bf.readLine();
                    seed[i][j]=in;
                }
                System.out.print("Excludes " + String.valueOf(i + 1) + ": ");
                System.out.print("Enter number of excluded links ");
                l = scan.nextInt();
                for(int j = 0; j < l; j++)
                {
                    in = bf.readLine();
                    excludes[i][j]=in;
                }
                System.out.print("Special Text Pattern " + String.valueOf(i + 1) + ": ");
                in = bf.readLine();
                specialTextPattern[i] = in;

                Element crawlId = doc.createElement("CrawlID");
                crawlId.setAttribute("ID", String.valueOf(ids[i]));
                Element rootURL = doc.createElement("RootURL");
                Element outputP = doc.createElement("OutputPath");
                Element seeds = doc.createElement("Seeds");
                Element exclude = doc.createElement("Exclude");
                Element specialPattern = doc.createElement("SpecialText");
                rootURL.appendChild(doc.createTextNode(url[i]));
                outputP.appendChild(doc.createTextNode(outputPath[i]));
                for(int j = 0; j < k; j++)
                {
                    seeds.appendChild(doc.createTextNode(seed[i][j]));
                }
                for(int j = 0; j < l; j++)
                {
                    exclude.appendChild(doc.createTextNode(excludes[i][j]));
                }
                specialPattern.appendChild(doc.createTextNode(specialTextPattern[i]));

                crawlId.appendChild(rootURL);
                crawlId.appendChild(outputP);
                crawlId.appendChild(seeds);
                crawlId.appendChild(exclude);
                crawlId.appendChild(specialPattern);
                rootElement.appendChild(crawlId);

            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");

            transformer.transform(new DOMSource(doc),new StreamResult("data/storedData.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            //transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}
