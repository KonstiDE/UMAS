package wue.eorc.umas.controller.scenes.main;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import wue.eorc.umas.controller.customs.UMASDialog;
import wue.eorc.umas.controller.listeners.CallbackListener;
import wue.eorc.umas.controller.listeners.QueueListener;
import wue.eorc.umas.controller.scenes.views.dialogs.agisoft.AgisoftErrorController;
import wue.eorc.umas.enums.TaskType;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.enums.agisoft.AgisoftTask;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.status.QueueItem;
import wue.eorc.umas.utils.ui.ItemSearcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class StatusController implements QueueListener {

    // When I built this, it was Wednesday, my dudes.
    public static final Queue<Runnable> queue = new LinkedList<>();

    public Label statusLabel;
    public ProgressBar progressBar;
    public HBox visQueue;

    public static Process currentProcess;
    public static boolean isRunning = false;

    public DisplayController display;

    public StatusController(HBox hBox, DisplayController display) throws UMASException {
        this.progressBar = ItemSearcher.getItemById("status.bar", hBox, ProgressBar.class);
        this.statusLabel = ItemSearcher.getItemById("status.label", hBox, Label.class);
        this.visQueue = ItemSearcher.getItemById("status.queue", hBox, HBox.class);
        this.display = display;
    }

    public void updateStatus(float progress){
        this.statusLabel.setText(Math.round(progress) + "%");
        this.progressBar.setProgress(progress / 100);
    }

    @Override
    public void enqueueAgisoft(WorkflowType workflowType, AgisoftTask agisoftTask) {
        QueueItem queueItem = new QueueItem(TaskType.AGISOFT, workflowType.name() + " - " + agisoftTask.name());

        Platform.runLater(() -> visQueue.getChildren().add(0, queueItem.getNode()));
    }

    @Override
    public void startedAgisoft(WorkflowType workflowType, AgisoftTask agisoftTask) {
        Platform.runLater(() -> {
            try{
                visQueue.getChildren().removeFirst();
            }catch(NoSuchElementException ignored){}
            // currentlyProcessing.textProperty().set(workflowType.name() + " - " + agisoftTask.name());
        });
    }

    @Override
    public void finishAgisoft(WorkflowType workflowType, AgisoftTask agisoftTask) {
        Platform.runLater(() -> {
            if(!visQueue.getChildren().isEmpty()){
                //currentlyProcessing.textProperty().set(processingListView.getItems().get(0));
                visQueue.getChildren().removeFirst();
            }else{
                //currentlyProcessing.textProperty().set("");
            }
        });
    }

}
