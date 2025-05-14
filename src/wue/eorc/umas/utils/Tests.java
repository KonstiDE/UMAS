package wue.eorc.umas.utils;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Tests {

    public static void main(String[] args) {
        File kmzFile = new File("/home/caipi/Desktop/thermalfireaoi1buffered10m-field.kmz");
        try (FileInputStream fis = new FileInputStream(kmzFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().toLowerCase().endsWith(".kml")) {
                    parseKML(zis);  // <-- parse directly from stream
                    break;          // Optional: exit after first KML
                }
            }

        } catch (Exception ignored) {}
    }

    private static void parseKML(InputStream kmlStream) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(kmlStream);
        doc.getDocumentElement().normalize();

        NodeList coordinates = doc.getElementsByTagName("coordinates");

        for (int i = 0; i < coordinates.getLength(); i++) {
            String text = coordinates.item(i).getTextContent().trim();
            String[] lines = text.split("\\s+");
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    System.out.printf("Lat: %s, Lon: %s, Alt: %s%n", parts[1], parts[0], parts[2]);
                }
            }
        }
    }

}
