import crawlers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Alan on 6/15/2015.
 */
public class Digester {
    public static void main(String[] args) throws Exception {
        // TODO Read sites from file
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
                    NodeList list1 = doc.getElementsByTagName("Seeds");
                    int list1Len = list1.getLength();
                    NodeList list2 = doc.getElementsByTagName("Exclude");
                    int list2Len = list2.getLength();

                    StringWriter sw = new StringWriter();
                    Transformer serializer = TransformerFactory.newInstance().newTransformer();
                    String seedList[] = new String[list1Len];

                    StringWriter tw = new StringWriter();
                    Transformer tserializer = TransformerFactory.newInstance().newTransformer();
                    String excludeList[] = new String[list1Len];


                    for(int i = 0; i < list1Len; i++)
                    {
                        serializer.transform(new DOMSource(list1.item(0)), new StreamResult(sw));
                        String res = sw.toString();
                        seedList[i] = res;
                    }

                    for(int j = 0; j < list2Len; j++)
                    {
                        tserializer.transform(new DOMSource(list2.item(0)), new StreamResult(tw));
                        String res = tw.toString();
                        excludeList[j] = res;
                    }

                    String ID = eElement.getAttribute("ID");
                    String RootURL = eElement.getElementsByTagName("RootURL").item(0).getTextContent();

                    DigestCrawler crawler = new DigestCrawler(ID, RootURL , seedList,
                            ".*(\\.(css|gif|js|jpg|png|mp3|mp3|zip|gz))", excludeList, "");
                    crawler.digest();

                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        /*DigestCrawler crawler = new DigestCrawler("UCSC", "https://genomics.soe.ucsc.edu/bd2k", new String[0],
                ".*(\\.(css|gif|js|jpg|png|mp3|mp3|zip|gz))", new String[0], "");
        crawler.digest();
        */
    }
}
