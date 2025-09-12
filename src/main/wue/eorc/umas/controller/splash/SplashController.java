package wue.eorc.umas.controller.splash;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.panes.FutureController;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.ProjectCache;
import wue.eorc.umas.loader.Settings;
import wue.eorc.umas.utils.ItemSearcher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

public class SplashController implements FutureController {


    @Override
    public CompletableFuture<Void> init(Pane pane, DisplayController display) throws UMASException {
        Label label = ItemSearcher.getItemById("splash.label", pane, Label.class);
        ProgressBar progressBar = ItemSearcher.getItemById("splash.progress", pane, ProgressBar.class);

        return CompletableFuture.runAsync(() -> {
            try {
                label.setText("Checking system files...");
                ProjectCache.createRecentProjectsFile();
                Settings.createSettingsFile();
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(() -> label.setText("Testing database connection..."));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String uri = "mongodb://localhost:27017";
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(uri))
                    .serverApi(serverApi)
                    .build();
            Platform.runLater(() -> label.setText("Built database connection string..."));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> label.setText("Trying to reach database server..."));

            try (MongoClient mongoClient = MongoClients.create(settings)) {
                MongoDatabase database = mongoClient.getDatabase("flights");
                try {
                    Bson command = new BsonDocument("ping", new BsonInt64(1));
                    Document commandResult = database.runCommand(command);

                    Platform.runLater(() -> label.setText("Successfully pinged database..."));

                    Thread.sleep(2000);

                } catch (MongoException me) {
                    Platform.runLater(() -> label.setText("Could not connect to database..."));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Platform.runLater(() -> label.setText("Verifying Agisoft Metashape version..."));

            try {
                String version = new AgisoftCaller(null, null, display)
                        .checkAgisoftVersion(Settings.getSetting(Setting.AGISOFTEXECPATH));
                Platform.runLater(() -> label.setText("Verified: " + version));

                Thread.sleep(2000);
            } catch (InterruptedException | URISyntaxException e) {
                Platform.runLater(() -> label.setText("Could not verify Agisoft executable..."));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }

        });
    }

}
