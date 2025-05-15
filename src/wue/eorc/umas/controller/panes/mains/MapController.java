package wue.eorc.umas.controller.panes.mains;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import wue.eorc.umas.utils.Colors;

import java.util.List;

public class MapController {

    public StackPane localRoot;

    public MapController(StackPane localRoot) {
        this.localRoot = localRoot;
    }

    public void showFlightArea(List<String[]> coordinates) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

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

        NumberAxis xAxis = new NumberAxis(minX, maxX, 0.001);
        NumberAxis yAxis = new NumberAxis(minY, maxY, 0.001);

        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.getData().add(series);

        localRoot.getChildren().clear();
        localRoot.getChildren().add(scatterChart);

        scatterChart.layoutBoundsProperty().addListener((observableValue, bounds, t1) -> Platform.runLater(() -> {
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
        }));

        scatterChart.setVisible(false);
    }

}
