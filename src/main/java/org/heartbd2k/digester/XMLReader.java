package org.heartbd2k.digester; /**
 * Created by Sneha on 6/17/2015.
 */
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class XMLReader {

    public static void main(String argv[])
    {
        try {
            File fXmlFile = new File("C:\\Users\\Sneha\\Documents\\GitHub\\BD2K-crawler\\data\\storedData.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("CrawlID");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++)
            {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;

                    System.out.println("Crawl ID : " + eElement.getAttribute("ID"));
                    System.out.println("Root URL : " + eElement.getElementsByTagName("RootURL").item(0).getTextContent());
                    System.out.println("Output Path : " + eElement.getElementsByTagName("OutputPath").item(0).getTextContent());
                    System.out.println("Seeds: ");
                    NodeList Seeds = eElement.getElementsByTagName("SeedURLs");
                    System.out.println("SeedURLs: ");
                    for(int i = 0; i < Seeds.getLength(); i++)
                    {
                        Element tempElem = (Element) nNode;
                        System.out.println("Seed URL: " + tempElem.getElementsByTagName("SeedURLs").item(i).getTextContent());
                    }
                    System.out.println("URL Excludes: " + eElement.getElementsByTagName("Exclude").item(0).getTextContent());
                    System.out.println("Special Text : " + eElement.getElementsByTagName("SpecialText").item(0).getTextContent());

                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}