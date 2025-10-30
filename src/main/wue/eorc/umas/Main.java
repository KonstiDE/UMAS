package wue.eorc.umas;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.RootController;
import wue.eorc.umas.controller.customs.UMASDialog;
import wue.eorc.umas.controller.scenes.main.StatusController;
import wue.eorc.umas.controller.scenes.views.dialogs.ClosingController;
import wue.eorc.umas.controller.splash.SplashController;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.SceneLoader;
import wue.eorc.umas.loader.Settings;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    static {
        System.load(Objects.requireNonNull(Main.class.getClassLoader().getResource("compiled/opencv_java4120.dll")).getFile());
    }

    @Override
    public void start(Stage primaryStage) throws IOException, UMASException, URISyntaxException {
        primaryStage.initStyle(StageStyle.UNDECORATED);

        SceneLoader loader = new SceneLoader(this.getClass().getClassLoader());

        AnchorPane splashRoot = (AnchorPane) loader.getScene("splash");
        SplashController splashController = new SplashController();
        CompletableFuture<Void> splash = splashController.init(splashRoot, null);

        splash.thenRun(() -> Platform.runLater(() -> {
            try {
                VBox root = (VBox) loader.getScene("main");
                RootController rootController = new RootController(root, loader);

                Scene scene = new Scene(root, 1440, 900);

                // Check settings
                if(Settings.useDarkLayout()){
                    scene.getStylesheets().add(Settings.darkMode);
                }

                Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();

                if(Boolean.parseBoolean(Settings.getSetting(Setting.FULL_SCREEN_AT_START_UP))){
                    primaryStage.setMaximized(true);
                }else{
                    primaryStage.setWidth(1440);
                    primaryStage.setHeight(900);

                    primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
                    primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
                }

                primaryStage.hide();
                primaryStage.close();

                Stage stage = new Stage();
                stage.initStyle(StageStyle.DECORATED);
                stage.setScene(scene);
                stage.setTitle("UAS Management Application System");

                stage.getIcons().add(new Image(
                        Objects.requireNonNull(getClass().
                                getClassLoader().getResourceAsStream("assets/icons/logo_jm.PNG"))
                ));

                stage.setOnCloseRequest(windowEvent -> {
                    if (StatusController.isRunning){
                        DialogPane dialogPane = (DialogPane) loader.getScene("decision_for_closing");
                        ClosingController closingController = new ClosingController();

                        UMASDialog closingDialog = new UMASDialog(dialogPane, "Over and out!", true, true);

                        Optional<String> close = closingDialog.showAndWait();
                        closingDialog.hide();
                        closingDialog.close();

                        if (close.isPresent()){
                            AgisoftCaller.killAll();

                            primaryStage.hide();
                            primaryStage.close();
                        }

                    }
                });

                stage.show();
            } catch (UMASException e) {
                e.printStackTrace();
            }
        }));

        primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().
                        getClassLoader().getResourceAsStream("assets/icons/logo_jm.PNG"))
        ));

        Scene scene = new Scene(splashRoot, 600, 400);

        primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().
                        getClassLoader().getResourceAsStream("assets/icons/logo_jm.PNG"))
        ));

        primaryStage.setScene(scene);
        primaryStage.show();

        //new QRCodeScanner();

    }
}