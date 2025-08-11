package wue.eorc.umas.controller.scenes.main;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import wue.eorc.umas.utils.Colors;

import java.util.List;

public class MapController {

    public StackPane localRoot;

    public MapController(StackPane localRoot) {
        this.localRoot = localRoot;
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

}
