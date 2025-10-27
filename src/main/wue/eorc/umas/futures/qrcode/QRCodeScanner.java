package wue.eorc.umas.futures.qrcode;

import boofcv.abst.distort.FDistort;
import boofcv.abst.fiducial.QrCodeDetector;
import boofcv.alg.distort.DistortImageOps;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.alg.interpolate.InterpolationType;
import boofcv.factory.fiducial.ConfigQrCode;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.gui.edge.VisualizeEdgeFeatures;
import boofcv.gui.feature.VisualizeFeatures;
import boofcv.gui.feature.VisualizeOpticalFlow;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.*;
import georegression.geometry.UtilLine2D_F64;
import georegression.struct.line.LineParametric2D_F64;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Vector2D_F64;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

public class QRCodeScanner {

    public List<BufferedImage> bufferedImages;

    public QRCodeScanner() {
        BufferedImage input = UtilImageIO.loadImageNotNull(UtilIO.pathExample("C:/Users/fkt40ea/Desktop/DJI_20241208052736_0006_D.JPG"));
        GrayU8 gray = ConvertBufferedImage.convertFrom(input, (GrayU8) null);

        var config = new ConfigQrCode();
        QrCodeDetector<GrayU8> detector = FactoryFiducial.qrcode(config, GrayU8.class);

        detector.process(gray);

        ArrayList<QrCode> detections = new ArrayList<>(detector.getDetections());

        Graphics2D g2 = input.createGraphics();
        int strokeWidth = Math.max(4, input.getWidth() / 200); // in large images the line can be too thin
        g2.setColor(Color.GREEN);
        g2.setStroke(new BasicStroke(strokeWidth));

        QrCode qr = detections.getFirst();
        Point2D_F64 p1 = qr.bounds.get(0);
        Point2D_F64 p2 = qr.bounds.get(1);
        Point2D_F64 p3 = qr.bounds.get(2);
        Point2D_F64 p4 = qr.bounds.get(3);

        double centerY = (p1.y + p2.y + p3.y + p4.y) / 4;

        //VisualizeShapes.drawPolygon(qr.bounds, true, 1, g2);
        //VisualizeFeatures.drawPoint(g2, centerX, centerY, 20, Color.RED, false);

        //ShowImages.showWindow(input, "Example QR Codes", true);

        Vector2D_F64 p1p4 = new Vector2D_F64(p4.x - p1.x, p4.y - p1.y);
        Vector2D_F64 p1p2 = new Vector2D_F64(p2.x - p1.x, p2.y - p1.y);

        Point2D_F64 downwards = new Point2D_F64(p4.x + p1p4.x, p4.y + p1p4.y);
        Point2D_F64 towardsMid = new Point2D_F64(downwards.x + p1p2.x / 2, downwards.y + p1p2.y / 2);

        //VisualizeShapes.draw(p1, p4, g2);
        //VisualizeFeatures.drawPoint(g2, (int) towardsMid.x, (int) towardsMid.y, 10, Color.RED, false);
        //ShowImages.showWindow(input, "Original");

        Mat openCVImage = bufferedImageToMat(input);

        Mat mask = Mat.zeros(openCVImage.rows() + 2, openCVImage.cols() + 2, CvType.CV_8UC1);

        Rect rect = new Rect();
        int connectivity = 4;
        int flags = connectivity | Imgproc.FLOODFILL_MASK_ONLY | (255 << 8);

        Scalar loDiff = new Scalar(10, 10, 10);
        Scalar upDiff = new Scalar(10, 10, 10);

        int filledPixels = Imgproc.floodFill(
                openCVImage, mask, new Point(towardsMid.x, towardsMid.y), new Scalar(0, 255, 0), rect, loDiff, upDiff, flags
        );

        System.out.println(filledPixels);

        Mat region = new Mat(mask, new Rect(1, 1, openCVImage.cols(), openCVImage.rows()));
        Core.multiply(region, new Scalar(255), region);

        HighGui.imshow("Connected Region", mask);
        HighGui.resizeWindow("Connected Region", 640, 480);
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();

    }

    public static Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

}
