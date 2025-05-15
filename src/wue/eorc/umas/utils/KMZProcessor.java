package wue.eorc.umas.utils;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.FlightParameters;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class KMZProcessor {

    public static FlightParameters processKmz(File kmzFile) {
        try (FileInputStream fis = new FileInputStream(kmzFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().toLowerCase().endsWith(".kml")) {
                    return parseKML(zis);
                }
            }

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

    private static String getWpmlTagValue(NodeList nodeList) {
        return nodeList.item(0).getFirstChild().getNodeValue();
    }

}
