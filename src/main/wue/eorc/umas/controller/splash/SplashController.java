package wue.eorc.umas.controller.splash;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.panes.FutureController;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.ProjectCache;
import wue.eorc.umas.loader.Settings;
import wue.eorc.umas.utils.ItemSearcher;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

public class SplashController implements FutureController {


    @Override
    public CompletableFuture<Void> init(Pane pane, DisplayController display) throws UMASException {
        Label label = ItemSearcher.getItemById("splash.label", pane, Label.class);
        label.setText("Checking system files...");

        ProgressBar progressBar = ItemSearcher.getItemById("splash.progress", pane, ProgressBar.class);
        progressBar.setProgress(0.2);

        return CompletableFuture.runAsync(() -> {
            try {
                ProjectCache.createRecentProjectsFile();
                Settings.createSettingsFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            label.setText("Testing database connection...");

            // Replace the placeholder with your Atlas connection string
            String uri = "mongodb://localhost:27017";
            // Construct a ServerApi instance using the ServerApi.builder() method
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(uri))
                    .serverApi(serverApi)
                    .build();
            // Create a new client and connect to the server
            try (MongoClient mongoClient = MongoClients.create(settings)) {
                MongoDatabase database = mongoClient.getDatabase("flights");
                try {
                    // Send a ping to confirm a successful connection
                    Bson command = new BsonDocument("ping", new BsonInt64(1));
                    Document commandResult = database.runCommand(command);
                    System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
                } catch (MongoException me) {
                    System.err.println(me);
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
    }

}
