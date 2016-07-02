package chmutocsv;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {

    public void exportToXML(HashMap<String, Kraj> data, String filename) {
        Object nazvyKraju[] = data.keySet().toArray();
        int celkovyPocet = 0;
        for (Object iterator : nazvyKraju) {
            celkovyPocet += data.get((String) iterator).getPocetStanic();
        }
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("seznamStanic");
            doc.appendChild(rootElement);
            Attr attr = doc.createAttribute("celkovyPocet");
            attr.setValue(String.valueOf(celkovyPocet));
            rootElement.setAttributeNode(attr);
            for (Object iterator : nazvyKraju) {
                // kraj elements
                Element kraj = doc.createElement("kraj");
                rootElement.appendChild(kraj);
                // set attribute to kraj element
                kraj.setAttribute("nazevKraje", ((String) iterator).trim());
                kraj.setAttribute("webidKraje", String.valueOf(data.get((String) iterator).getWebidKraje()));

                Object nazvyStanic[] = data.get((String) iterator).getSeznam().keySet().toArray();
                for (Object nazev : nazvyStanic) {
                    // stanice elements
                    Element stanice = doc.createElement("stanice");
                    kraj.appendChild(stanice);
                    // set attribute to stanice element
                    stanice.setAttribute("nazevStanice", ((String) nazev).trim());
                    stanice.setAttribute("webidStanice", String.valueOf(data.get((String) iterator).getSeznam().get((String) nazev)));
                }
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filename));

            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public HashMap<String, Kraj> importFromXML(String filename) {
        HashMap<String, Kraj> temp = new HashMap<>();
        try {
            File fXmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            
            NodeList krajList = doc.getElementsByTagName("kraj");
            for (int i = 0; i < krajList.getLength(); i++) {
                Element krajElement = (Element) krajList.item(i);
                Kraj tempKraj = new Kraj(Integer.decode(krajElement.getAttribute("webidKraje")));
                NodeList staniceList = krajElement.getElementsByTagName("stanice");
                for (int j = 0; j < staniceList.getLength(); j++) {
                    Element staniceElement = (Element) staniceList.item(j);
                    tempKraj.vlozitStanici(staniceElement.getAttribute("nazevStanice"), Integer.decode(staniceElement.getAttribute("webidStanice")));
                }
                temp.put(krajElement.getAttribute("nazevKraje"), tempKraj);
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return temp;
    }
}
