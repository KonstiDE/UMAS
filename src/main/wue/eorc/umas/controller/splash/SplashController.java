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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

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
            } catch (IOException ignored) {}

            try{
                Thread.sleep(1000);
            }catch(InterruptedException ignored){}

            Platform.runLater(() -> label.setText("Testing database connection..."));
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}

            String uri = "mongodb://132.187.202.30:27017";
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(uri))
                    .applyToSocketSettings(builder -> {
                        builder.connectTimeout(2000, MILLISECONDS);
                        builder.readTimeout(2000, MILLISECONDS);
                    })
                    .applyToClusterSettings( builder -> builder.serverSelectionTimeout(2000, MILLISECONDS))
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

                    try{
                        Thread.sleep(2000);
                    }catch (InterruptedException ignored){}

                } catch (MongoException me) {
                    Platform.runLater(() -> label.setText("Could not connect to database..."));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {}
                }
            }
            Platform.runLater(() -> label.setText("Verifying Agisoft Metashape version..."));

            try {
                String version = new AgisoftCaller(null, null, display)
                        .checkAgisoftVersion(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));

                if(version != null){
                    Platform.runLater(() -> label.setText("Verified: " + version));
                }else{
                    Platform.runLater(() -> label.setText("Could not verify Agisoft executable..."));
                }


                try{
                    Thread.sleep(2000);
                }catch (InterruptedException ignored){}

            } catch (URISyntaxException | InterruptedException e) {
                Platform.runLater(() -> label.setText("Could not verify Agisoft executable..."));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }

        });
    }

}
