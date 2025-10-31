package wue.eorc.umas.controller.scenes.main;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import wue.eorc.umas.utils.system.Colors;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapController {

    private final Color VIR_DOWN = Color.color(0.267, 0.005, 0.329);
    private final Color VIR_UP = Color.color(0.993, 0.906, 0.144);

    public StackPane localRoot;

    public MapController(StackPane localRoot) throws FileNotFoundException {
        this.localRoot = localRoot;

        // Create 3D objects
        Box box = new Box(1, 1, 1);
        box.setTranslateX(0);
        box.setTranslateY(0);
        box.setTranslateZ(0);

        // Create semi-transparent material for outer sphere
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.color(0.3, 0.5, 1.0, 0.3)); // Semi-transparent blue
        material.setSpecularColor(Color.BLUE);
        box.setMaterial(material);

        List<Box> boxes = readTxtFile(new File("B:\\1_Projects\\FireMapping\\0_Flights\\01102025_Boma_North_MAVICM3MFIXEDM3M\\1_Agisoft\\align_images.txt"));
        boxes.add(box);

        // Group all rotatable objects together
        Group rotationGroup = new Group(boxes.toArray(new Box[0]));

        // Create lights
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(100);
        light.setTranslateY(-100);
        light.setTranslateZ(-200);

        AmbientLight ambient = new AmbientLight(Color.color(0.3, 0.3, 0.3));

        Group root3D = new Group(rotationGroup, light, ambient);

        // Create SubScene for 3D content
        SubScene subScene = new SubScene(root3D, 400, 300, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.gray(0.2));

        // Create and position camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-300);
        camera.setNearClip(0.1);
        camera.setFarClip(2000.0);
        camera.setFieldOfView(45);
        subScene.setCamera(camera);

        // Mouse rotation controls
        final double[] mouseOldX = {0};
        final double[] mouseOldY = {0};
        final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

        rotationGroup.getTransforms().addAll(rotateX, rotateY);

        subScene.setOnMousePressed(event -> {
            mouseOldX[0] = event.getSceneX();
            mouseOldY[0] = event.getSceneY();
        });

        subScene.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mouseOldX[0];
            double deltaY = event.getSceneY() - mouseOldY[0];

            rotateY.setAngle(rotateY.getAngle() - deltaX * 0.5);
            rotateX.setAngle(rotateX.getAngle() + deltaY * 0.5);

            mouseOldX[0] = event.getSceneX();
            mouseOldY[0] = event.getSceneY();
        });

        // Scroll to zoom
        subScene.setOnScroll(event -> {
            double delta = event.getDeltaY();
            camera.setTranslateZ(camera.getTranslateZ() + delta * 0.5);
        });

        subScene.setManaged(false);

        // Add SubScene into StackPane
        this.localRoot.getChildren().add(subScene);

        localRoot.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            subScene.setWidth(newVal.getWidth());
            subScene.setHeight(newVal.getHeight());
        });

    }

    public void showFlightArea(List<String[]> coordinates, List<String[]> waypoints) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        XYChart.Series<Number, Number> seriesA = new XYChart.Series<>();

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (String[] coordinate : coordinates) {
            double x = Double.parseDouble(coordinate[0]);
            double y = Double.parseDouble(coordinate[1]);

            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;

            series.getData().add(new XYChart.Data<>(x, y));
        }

        for(String[] waypoint : waypoints) {
            double x = Double.parseDouble(waypoint[0]);
            double y = Double.parseDouble(waypoint[1]);

            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;

            seriesA.getData().add(new XYChart.Data<>(x, y));
        }

        NumberAxis xAxis = new NumberAxis(minX, maxX, 0.001);
        NumberAxis yAxis = new NumberAxis(minY, maxY, 0.001);

        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.getData().add(series);
        scatterChart.getData().add(seriesA);

        localRoot.getChildren().clear();
        localRoot.getChildren().add(scatterChart);

        ChangeListener<Bounds> changeListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds bounds, Bounds t1) {
                Platform.runLater(() -> drawOverlayLines(scatterChart, series, seriesA, xAxis, yAxis));
                scatterChart.layoutBoundsProperty().removeListener(this);
            }
        };

        scatterChart.layoutBoundsProperty().addListener(changeListener);

        scatterChart.setVisible(false);
    }

    private void drawOverlayLines(ScatterChart<Number, Number> scatterChart,
                                  XYChart.Series<Number, Number> series, XYChart.Series<Number, Number> seriesA,
                                  NumberAxis xAxis, NumberAxis yAxis) {

        Point2D plotAreaOriginInScene = scatterChart.localToScene(0, 0);

        Polygon polygon = new Polygon();
        polygon.setStroke(Colors.PROC_GREEN);
        polygon.setStrokeWidth(2);

        for (XYChart.Data<Number, Number> data : series.getData()) {
            double x = xAxis.getDisplayPosition(data.getXValue());
            double y = yAxis.getDisplayPosition(data.getYValue());

            Point2D pointInScene = scatterChart.localToScene(x, y);
            double localX = pointInScene.getX() - plotAreaOriginInScene.getX();
            double localY = pointInScene.getY() - plotAreaOriginInScene.getY();

            polygon.getPoints().addAll(localX, localY);
        }

        localRoot.getChildren().addAll(polygon);

        Group group = new Group();

        for(int i = 0; i < seriesA.getData().size() - 2; i+=2) {
            XYChart.Data<Number, Number> dataA = seriesA.getData().get(i);
            XYChart.Data<Number, Number> dataB = seriesA.getData().get(i + 1);

            double x = xAxis.getDisplayPosition(dataA.getXValue());
            double y = yAxis.getDisplayPosition(dataA.getYValue());

            Point2D pointInScene = scatterChart.localToScene(x, y);
            double localX = pointInScene.getX() - plotAreaOriginInScene.getX();
            double localY = pointInScene.getY() - plotAreaOriginInScene.getY();

            double x2 = xAxis.getDisplayPosition(dataB.getXValue());
            double y2 = yAxis.getDisplayPosition(dataB.getYValue());

            Point2D pointInScene2 = scatterChart.localToScene(x2, y2);
            double localX2 = pointInScene2.getX() - plotAreaOriginInScene.getX();
            double localY2 = pointInScene2.getY() - plotAreaOriginInScene.getY();

            Line line = new Line(localX, localY, localX2, localY2);
            line.setStroke(Colors.CYAN);

            group.getChildren().add(line);
        }

        localRoot.getChildren().addAll(group);
    }

    private Cylinder createAxis(Color color, double x, double y, double z) {
        Cylinder axis = new Cylinder(2, 120);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(color.brighter());
        axis.setMaterial(material);
        axis.setTranslateX(x);
        axis.setTranslateY(y);
        axis.setTranslateZ(z);
        return axis;
    }

    private List<Box> readTxtFile(File file) throws FileNotFoundException {
        List<Float> xs = new ArrayList<>();
        List<Float> ys = new ArrayList<>();
        List<Float> zs = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));

        for(String line : reader.lines().toList()){
            String[] split = line.split(" ");

            // Switch y and z axis! Why the hell people doing this??
            xs.add(Float.parseFloat(split[0]));
            zs.add(Float.parseFloat(split[1]));
            ys.add(Float.parseFloat(split[2]));
        }

        // Find middle point of x and z axis and shift
        float xmin = Collections.min(xs);
        float xmax = Collections.max(xs);
        float zmin = Collections.min(zs);
        float zmax = Collections.max(zs);

        float relative_mid_x = (Math.abs(xmax) - Math.abs(xmin)) / 2;
        float relative_mid_z = (Math.abs(zmax) - Math.abs(zmin)) / 2;

        shift(xs, xmin, xmax, relative_mid_x);
        shift(zs, zmin, zmax, relative_mid_z);

        float ymin = Collections.min(ys);
        ys.replaceAll(y -> (y - ymin));

        float ymaxNew = Collections.max(ys);

        List<Box> boxes = new ArrayList<>();

        for(int i = 0; i < xs.size(); i++) {
            Box box = new Box(.3, .3, .3);
            box.setTranslateX(xs.get(i));
            box.setTranslateY(ys.get(i) * -1);
            box.setTranslateZ(zs.get(i));

            PhongMaterial material1 = new PhongMaterial();
            double t = ys.get(i) / ymaxNew;

            double r = VIR_DOWN.getRed() + t * (VIR_UP.getRed() - VIR_DOWN.getRed());
            double g = VIR_DOWN.getGreen() + t * (VIR_UP.getGreen() - VIR_DOWN.getGreen());
            double b = VIR_DOWN.getBlue() + t * (VIR_UP.getBlue() - VIR_DOWN.getBlue());

            material1.setDiffuseColor(Color.color(r, g, b));
            material1.setSpecularColor(Color.color(r, g, b));

            box.setMaterial(material1);
            boxes.add(box);
        }

        return boxes;

    }

    private void shift(List<Float> xs, float xmin, float xmax, float relative_mid) {
        if (xmin > 0 && xmax > 0){
            _shift(xmin + relative_mid, xs, ShiftDirection.L);
        } else if(xmin < 0 && xmax < 0){
            _shift(Math.abs(xmin) + Math.abs(relative_mid), xs, ShiftDirection.R);
        } else {
            if(Math.abs(xmin) > Math.abs(xmax)){
                _shift(Math.abs(relative_mid), xs, ShiftDirection.R);
            }else{
                _shift(Math.abs(relative_mid), xs, ShiftDirection.L);
            }
        }
    }

    private void _shift(float shift, List<Float> vs, ShiftDirection direction) {
        for (int i = 0; i < vs.size(); i++) {
            if (direction == ShiftDirection.L) {
                vs.set(i, vs.get(i) - shift);
            } else {
                vs.set(i, vs.get(i) + shift);
            }
        }
    }

    enum ShiftDirection {
        R,
        L
    }

}
