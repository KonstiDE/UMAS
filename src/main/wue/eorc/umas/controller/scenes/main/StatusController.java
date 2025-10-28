package wue.eorc.umas.controller.scenes.main;

import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import wue.eorc.umas.controller.customs.UMASDialog;
import wue.eorc.umas.controller.listeners.CallbackListener;
import wue.eorc.umas.controller.listeners.QueueListener;
import wue.eorc.umas.controller.scenes.views.dialogs.agisoft.AgisoftErrorController;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.enums.agisoft.AgisoftTask;
import wue.eorc.umas.exception.UMASException;
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

    public static final Queue<Runnable> queue = new LinkedList<>();

    public HBox hBox;

    public ProgressBar progressBar;
    public ListView<String> listView;

    public static Process currentProcess;
    public static boolean isRunning = false;

    public DisplayController display;

    public StatusController(HBox hBox, DisplayController display) throws UMASException {
        this.hBox = hBox;
        this.progressBar = ItemSearcher.getItemById("status.bar", hBox, ProgressBar.class);
        this.listView = ItemSearcher.getGenericControlById("status.queue", hBox, ListView.class, String.class);
        this.display = display;
    }

    public void updateStatus(float progress){
        this.progressBar.setProgress(progress / 100);
    }

    @Override
    public void enqueueAgisoft(WorkflowType workflowType, AgisoftTask agisoftTask) {
        Platform.runLater(() -> listView.getItems().add(workflowType.name() + " - " + agisoftTask.name()));
    }

    @Override
    public void startedAgisoft(WorkflowType workflowType, AgisoftTask agisoftTask) {
        Platform.runLater(() -> {
            try{
                listView.getItems().removeFirst();
            }catch(NoSuchElementException ignored){}
            // currentlyProcessing.textProperty().set(workflowType.name() + " - " + agisoftTask.name());
        });
    }

    @Override
    public void finishAgisoft(WorkflowType workflowType, AgisoftTask agisoftTask) {
        Platform.runLater(() -> {
            if(!listView.getItems().isEmpty()){
                //currentlyProcessing.textProperty().set(processingListView.getItems().get(0));
                //processingListView.getItems().remove(0);
            }else{
                //currentlyProcessing.textProperty().set("");
            }
        });
    }

    private void enqueueAgisoft(WorkflowType workflowType, AgisoftTask task, Pane pane, ProcessBuilder pb, boolean nextIfFailed, CallbackListener callbackListener){
        queue.add(() -> CompletableFuture.supplyAsync(() -> {
            //TODO add some shit to UI here, to the listview lol

            try{
                pb.redirectErrorStream(true);
                Process p = pb.start();
                currentProcess = p;

                Pair<AgisoftTask, String> success = watchForSignalAgisoft("vn:", p.getInputStream(), callbackListener, workflowType, task, pane);

                int exitCode = p.waitFor();

                if (exitCode != 0) return new Pair<>(AgisoftTask.UNDEFINED, "False");

                return success;
            }catch (IOException | InterruptedException e){
                return new Pair<>(AgisoftTask.UNDEFINED, "False");
            }
        }) .thenAcceptAsync(result -> {
            boolean success = Boolean.parseBoolean(result.getValue());

            try {
                callbackListener.callbackAgisoft(pane, workflowType, task, result.getValue());
            } catch (UMASException e) {
                throw new RuntimeException(e);
            }

            //TODO remove from listview here!

            if(success) {
                isRunning = true;
                processNext();
            }else{
                if(nextIfFailed) {
                    isRunning = true;
                    processNext();
                }else{
                    isRunning = false;
                }
            }

        }));

        if (!isRunning) {
            isRunning = true;
            processNext();
        }
    }

    private Pair<AgisoftTask, String> watchForSignalAgisoft(String signalKey, InputStream inputStream, CallbackListener listener, WorkflowType workflowType, AgisoftTask task, Pane pane) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if(listener != null && (line.startsWith("vp: ") || line.startsWith("xvp: "))) {
                    String finalLine = line;
                    Platform.runLater(() -> {
                        try{
                            listener.progress(Float.parseFloat(finalLine.substring(4)));
                        } catch (NumberFormatException ignored) {  }
                    });
                }
                if(line.startsWith(signalKey)){
                    String[] split = line.split(":");

                    AgisoftTask currentTask = AgisoftTask.valueOf(split[1]);

                    if(currentTask == task){
                        if(listener != null)
                            Platform.runLater(() -> listener.progress(0));

                        return new Pair<>(currentTask, split[2]);
                    }else{
                        if(listener != null && pane != null)
                            listener.callbackAgisoft(pane, workflowType, currentTask, split[2]);
                    }
                }
                if(line.startsWith("ve:")){
                    final String[] split = line.split(":");

                    Platform.runLater(() -> {
                        AgisoftTask currentTask = AgisoftTask.valueOf(split[1]);

                        DialogPane dialogPane = (DialogPane) display.getSceneLoader().getScene("agisoft_error_dialog");
                        Dialog<String> dialog = new UMASDialog(dialogPane, "Error", true, true);

                        String[] splitForError = split[2].split("~");

                        AgisoftErrorController errorController = new AgisoftErrorController(
                                splitForError[0], splitForError[1], splitForError[2]);

                        try {
                            errorController.init(display, dialog);
                        } catch (UMASException e) {
                            throw new RuntimeException(e);
                        }

                        dialog.show();
                    });

                }
            }
        } catch (UMASException e) {
            throw new RuntimeException(e);
        }
        return new Pair<>(AgisoftTask.UNDEFINED, Boolean.FALSE.toString());
    }

    private synchronized void processNext() {
        Runnable nextTask = StatusController.queue.poll();
        if (nextTask != null) {
            nextTask.run();
        } else {
            isRunning = false;
        }
    }

    public static void killAll(){
        for(int i = 0; i < StatusController.queue.size(); i++){
            StatusController.queue.remove();
        }

        currentProcess.destroy();
    }

}
