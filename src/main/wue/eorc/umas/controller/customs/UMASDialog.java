package wue.eorc.umas.controller.customs;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import wue.eorc.umas.loader.Settings;

import java.util.Objects;

public class UMASDialog extends Dialog<String> {

    private final Image closeActive = new Image(Objects.requireNonNull(
            getClass().getClassLoader().getResource("assets/logic/cross_red.png")).toString());

    private final Image closeInactiveLight = new Image(Objects.requireNonNull(
            getClass().getClassLoader().getResource("assets/logic/cross_black.png")).toString());

    private final Image closeInactiveDark = new Image(Objects.requireNonNull(
            getClass().getClassLoader().getResource("assets/logic/cross_white.png")).toString());

    private double x;
    private double y;

    public UMASDialog(DialogPane dialogPane, String title, boolean shouldDeliver, boolean closeable){
        this.setDialogPane(dialogPane);

        HBox uiHBox = new HBox();
        uiHBox.setPadding(new Insets(10));

        Label uiTitle = new Label(title);
        uiTitle.setFont(Font.font(14));

        Pane uiPane = new Pane();
        HBox.setHgrow(uiPane, Priority.ALWAYS);

        if (closeable){
            ImageView uiImageView = new ImageView(Settings.useDarkLayout() ? closeInactiveDark : closeInactiveLight);
            uiImageView.setFitHeight(12);
            uiImageView.setFitWidth(12);

            uiImageView.hoverProperty().addListener((observableValue, aBoolean, t1) -> {
                if (t1) {
                    uiImageView.setImage(closeActive);
                } else {
                    if (Settings.useDarkLayout()){
                        uiImageView.setImage(closeInactiveDark);
                    }else{
                        uiImageView.setImage(closeInactiveLight);
                    }
                }
            });

            uiImageView.setOnMouseClicked((e) -> {
                if (!shouldDeliver){
                    this.setResult(null);
                }
                this.hide();
                this.close();
            });

            uiHBox.getChildren().addAll(uiTitle, uiPane, uiImageView);

        }else{
            uiHBox.getChildren().addAll(uiTitle, uiPane);
        }

        this.getDialogPane().setHeader(uiHBox);
        this.initStyle(StageStyle.UNDECORATED);

        EventHandler<MouseEvent> onDragged = e -> {
            this.setX(e.getScreenX() - x);
            this.setY(e.getScreenY() - y);
        };

        EventHandler<MouseEvent> onPressed = e -> {
            x = e.getSceneX();
            y = e.getSceneY();
        };

        uiTitle.setOnMousePressed(onPressed);
        uiTitle.setOnMouseDragged(onDragged);

        uiPane.setOnMousePressed(onPressed);
        uiPane.setOnMouseDragged(onDragged);

        if (Settings.useDarkLayout()){
            this.getDialogPane().getScene().getStylesheets().add(Settings.darkModeDialog);
        }

        this.getDialogPane().getScene().getStylesheets().add("styles/window-shadow.css");
    }

}
