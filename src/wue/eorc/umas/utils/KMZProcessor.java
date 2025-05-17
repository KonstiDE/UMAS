package wue.eorc.umas.utils;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.FlightParameters;
import wue.eorc.umas.models.Waypoint;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class KMZProcessor {

    public static FlightParameters processKmz(File kmzFile) {
        try (FileInputStream fis = new FileInputStream(kmzFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            FlightParameters flightParameters = null;
            List<String[]> waypoints = null;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().toLowerCase().endsWith(".kml")) {
                    flightParameters = parseKML(zis);
                }
                if(entry.getName().toLowerCase().endsWith(".wpml")) {
                    waypoints = parseWayPoints(zis);
                }
            }
            if(flightParameters != null && waypoints != null){
                flightParameters.setWaypoints(waypoints);
            }

            return flightParameters;

        } catch (Exception ignored) {
            UMASException.throwWindow(ErrorType.USER, "Could not parse flight parameters. Please make sure that" +
                    " this is a valid .kmz file from the DJI Controller. If you do not find, exports or have the " +
                    "original .kmz file, you can always fill the parameters manually later on.");
        }

        return null;

    }

    private static FlightParameters parseKML(InputStream kmlStream) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(kmlStream);
        doc.normalizeDocument();

        String height = getWpmlTagValue(doc.getElementsByTagName("wpml:height"));
        String speed = getWpmlTagValue(doc.getElementsByTagName("wpml:autoFlightSpeed"));
        String frontOverlap = getWpmlTagValue(doc.getElementsByTagName("wpml:height"));
        String sideOverlap = getWpmlTagValue(doc.getElementsByTagName("wpml:height"));
        String coordinates = getWpmlTagValue(doc.getElementsByTagName("coordinates"));

        List<String> coords = Arrays.stream(coordinates.split("[,\r\n]+"))
                .filter(e -> !e.equals("0") && !e.trim().isEmpty()).map(String::trim).
                toList();

        List<String[]> arrays = new ArrayList<>();
        for (int i = 0; i < coords.size() - 1; i += 2) {
            arrays.add(new String[]{coords.get(i), coords.get(i + 1)});
        }

        return new FlightParameters(Integer.parseInt(height), Integer.parseInt(frontOverlap), Integer.parseInt(sideOverlap), Double.parseDouble(speed), arrays);
    }

    private static List<String[]> parseWayPoints(InputStream wpmlStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(wpmlStream);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        // Define the wpml namespace
        xpath.setNamespaceContext(new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                if ("wpml".equals(prefix)) return "http://www.dji.com/wpmz/1.0.6";
                else return XMLConstants.NULL_NS_URI;
            }

            public String getPrefix(String uri) { return null; }
            public Iterator<String> getPrefixes(String uri) { return null; }
        });

        // Query all wpml:point elements
        NodeList points = (NodeList) xpath.evaluate("//wpml:point", doc, XPathConstants.NODESET);

        List<Waypoint> waypoints = new ArrayList<>();

        for (int i = 0; i < points.getLength(); i++) {
            Node point = points.item(i);
            Element elem = (Element) point;

            double lat = Double.parseDouble(elem.getElementsByTagNameNS("http://www.dji.com/wpmz/1.0.6", "latitude").item(0).getTextContent());
            double lon = Double.parseDouble(elem.getElementsByTagNameNS("http://www.dji.com/wpmz/1.0.6", "longitude").item(0).getTextContent());
            double alt = Double.parseDouble(elem.getElementsByTagNameNS("http://www.dji.com/wpmz/1.0.6", "altitude").item(0).getTextContent());

            waypoints.add(new Waypoint(lat, lon, alt));
        }

        return waypoints.stream().map(waypoint -> new String[]{
                String.valueOf(waypoint.getLongitude()),
                String.valueOf(waypoint.getLatitude()),
                String.valueOf(waypoint.getAltitude())
        }).toList();

    }

    private static String getWpmlTagValue(NodeList nodeList) {
        return nodeList.item(0).getFirstChild().getNodeValue();
    }

}
