package wue.eorc.umas.controller.customs;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.Settings;
import wue.eorc.umas.utils.ItemSearcher;

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

    private final EventHandler<MouseEvent> onPressed = e -> {
        x = e.getSceneX();
        y = e.getSceneY();
    };

    private final EventHandler<MouseEvent> onDrag = e -> {
        this.setX(e.getScreenX() - x);
        this.setY(e.getScreenY() - y);
    };

    public UMASDialog(DialogPane dialogPane) {
        this.setDialogPane(dialogPane);
        this.initStyle(StageStyle.UNDECORATED);

        try {
            ImageView imageView = ItemSearcher.getItemById("umasdialog.close", (Parent) dialogPane.getHeader(), ImageView.class);

            imageView.hoverProperty().addListener((observableValue, aBoolean, t1) -> {
                if (t1) {
                    imageView.setImage(closeActive);
                } else {
                    if (Settings.useDarkLayout()){
                        imageView.setImage(closeInactiveDark);
                    }else{
                        imageView.setImage(closeInactiveLight);
                    }
                }
            });

            imageView.setOnMouseClicked((e) -> {
                this.hide();
                this.close();
            });

            Pane mainDrag = ItemSearcher.getItemById("umasdialog.drag", (Parent) dialogPane.getHeader(), Pane.class);
            Label title = ItemSearcher.getItemById("umasdialog.title", (Parent) dialogPane.getHeader(), Label.class);

            mainDrag.setOnMousePressed(onPressed);
            mainDrag.setOnMouseDragged(onDrag);

            title.setOnMousePressed(onPressed);
            title.setOnMouseDragged(onDrag);

        } catch (UMASException _ignored) {
            UMASException.throwWindow(ErrorType.INTERNAL, "Could not find closing button.");
        }
    }

}
