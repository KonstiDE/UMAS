package wue.eorc.umas.controller.scenes.views.panes;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.listeners.AgisoftCallbackListener;
import wue.eorc.umas.controller.listeners.AgisoftQueueListener;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.panes.components.ProcessActionsPreparer;
import wue.eorc.umas.enums.AgisoftTask;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.ProcessingChain;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.Colors;
import wue.eorc.umas.utils.DirectoryUtils;
import wue.eorc.umas.utils.ItemSearcher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

public class ShowProcessingController implements ViewController, AgisoftQueueListener, AgisoftCallbackListener {

    private final AgisoftCaller agisoftCaller;
    private final Flight flight;

    public ShowProcessingController(Flight flight) throws URISyntaxException {
        this.flight = flight;
        this.agisoftCaller = new AgisoftCaller(this, this);
    }

    public Pane processingPaneRoot;

    public Label currentlyProcessing;
    public ListView<String> processingListView;

    public DisplayController displayController;

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        displayController = display;
        processingPaneRoot = pane;

        currentlyProcessing = ItemSearcher.getItemById("showprocess.queue.current", pane, Label.class);
        processingListView = ItemSearcher.getGenericControlById("showprocess.queue.listview", pane, ListView.class, String.class);

        Button refresh = ItemSearcher.getItemById("showprocess.refresh", pane, Button.class);
        refresh.setOnAction(_ignored -> refresh(pane, display));

        TabPane tabPane = ItemSearcher.getItemById("showprocess.tabpane", pane, TabPane.class);
        checkProcessingChain(pane, tabPane);

        Button createAgisoft = ItemSearcher.getItemById("showprocess.createagisoft", pane, Button.class);
        createAgisoft.setOnAction(_ignored -> {
            try {
                boolean success = agisoftCaller.createProject(DirectoryUtils.figureAgisoftFilePath(this.flight));

                if(!success) {
                    UMASException.throwWindow(ErrorType.INTERNAL, "Could not create Agisoft project! Please restart the application!");
                }else{
                    refresh(pane, display);
                }

            } catch (IOException | InterruptedException e) {
                UMASException.throwWindow(ErrorType.INTERNAL, "Could not create Agisoft project! Did you set the Agisoft path in the Settings?");
            }
        });


        TabPane processingAgisoft = ItemSearcher.getItemById("showprocess.imagetypepaneagisoft", (AnchorPane) tabPane.getTabs().get(0).getContent(), TabPane.class);
        processingAgisoft.getTabs().clear();
        if(projectExistsAgisoft(this.flight)) {
            for(WorkflowType workflowType : WorkflowType.getWorkflowTypesFromImageTypes(this.flight.getImageTypes().keySet())){
                Tab tab = new Tab();
                tab.setText(workflowType.getName());

                ProcessActionsPreparer preparer = new ProcessActionsPreparer(
                        flight, workflowType, display, this, agisoftCaller);

                preparer.setupWorkflowActions();

                AnchorPane anchorPane = new AnchorPane();
                anchorPane.getChildren().add(preparer.getWorkflowPane());

                tab.setClosable(false);
                tab.setContent(anchorPane);

                processingAgisoft.getTabs().add(tab);
            }
        }

    }

    public void refresh(Pane pane, DisplayController display) {
        try {
            init(pane, display);
        } catch (UMASException ex) {
            UMASException.throwWindow(ErrorType.INTERNAL, "Could not refresh the processing display. Please restart the application!");
        }
    }

    private void checkProcessingChain(Pane pane, TabPane tabPane) throws UMASException {
        Button createAgisoft = ItemSearcher.getItemById("showprocess.createagisoft", pane, Button.class);
        Button createTerra =  ItemSearcher.getItemById("showprocess.createterra", pane, Button.class);
        Circle statusAgisoft = ItemSearcher.getItemById("showprocess.statusagisoft", pane, Circle.class);
        Circle statusTerra = ItemSearcher.getItemById("showprocess.statusterra", pane, Circle.class);

        Tab tabAgisoft = tabPane.getTabs().get(0);
        Tab tabTerra = tabPane.getTabs().get(1);

        if (projectExistsAgisoft(this.flight)){
            createAgisoft.setDisable(true);
            statusAgisoft.setFill(Paint.valueOf("GREEN"));
            tabAgisoft.setDisable(false);
        }else{
            createAgisoft.setDisable(false);
            statusAgisoft.setFill(Paint.valueOf("RED"));
            tabAgisoft.setDisable(true);
        }

        if (projectExistsTerra(this.flight)){
            createTerra.setDisable(true);
            statusTerra.setFill(Paint.valueOf("GREEN"));
            tabTerra.setDisable(false);
        }else{
            createTerra.setDisable(false);
            statusTerra.setFill(Paint.valueOf("RED"));
            tabTerra.setDisable(true);
        }

    }

    private boolean projectExistsAgisoft(Flight flight){
        String fileName = flight.getProjectFileNameAgisoft();

        if(flight.getProcessingChain() == ProcessingChain.AGISOFT){
            return Paths.get(flight.getFlightDirectory(), "1_Agisoft", fileName).toFile().exists();
        }else if(flight.getProcessingChain() == ProcessingChain.BOTH){
            return Paths.get(flight.getFlightDirectory(), "2_Agisoft", fileName).toFile().exists();
        }
        return false;
    }

    private boolean projectExistsTerra(Flight flight) {
        String fileName = flight.getProjectFileNameTerra();

        if(flight.getProcessingChain() == ProcessingChain.TERRA || flight.getProcessingChain() == ProcessingChain.BOTH){
            return Paths.get(flight.getFlightDirectory(), "1_TerraFiles", fileName).toFile().exists();
        }
        return false;
    }

    @Override
    public void enqueue(AgisoftTask agisoftTask) {
        Platform.runLater(() -> {
            processingListView.getItems().add(agisoftTask.name());
        });
    }

    @Override
    public void started(AgisoftTask agisoftTask) {
        Platform.runLater(() -> {
            try{
                processingListView.getItems().removeFirst();
            }catch(NoSuchElementException ignored){}
            currentlyProcessing.textProperty().set(agisoftTask.name());
        });
    }

    @Override
    public void finish() {
        Platform.runLater(() -> {
            if(!processingListView.getItems().isEmpty()){
                currentlyProcessing.textProperty().set(processingListView.getItems().get(0));
                //processingListView.getItems().remove(0);
            }else{
                currentlyProcessing.textProperty().set("");
            }
        });
    }

    @Override
    public void callback(Pane source, AgisoftTask task, boolean result) throws UMASException {
        switch (task){
            case ADD_PHOTOS_CHECK, ALIGN_IMAGES_CHECK, OPTIMIZE_CAMERAS_CHECK, BUILD_DEM_CHECK, BUILD_ORTHOMOSAIC_CHECK,
                 EXPORT_DEM_CHECK, EXPORT_ORTHOMOSAIC_CHECK, BUILD_POINT_CLOUD_CHECK, SET_BRIGHTNESS_CHECK,
                 GENERATE_REPORT_CHECK -> {
                StackPane workFlowStep = ItemSearcher.getItemById("processing." + task.name().replace("_CHECK", ""), source, StackPane.class);

                Rectangle rectangle = ItemSearcher.getItemById("processing.rectangle", workFlowStep, Rectangle.class);
                ProgressIndicator indicator = ItemSearcher.getItemById("processing.indicator", workFlowStep, ProgressIndicator.class);

                indicator.setVisible(false);
                if(result){
                    rectangle.setFill(Colors.PROC_GREEN);
                    //this.showProcessingController.refresh(this.showProcessingController.getProcessingPaneRoot(), display);
                }else{
                    rectangle.setFill(Colors.PROC_RED);
                }
            }
        }
    }

    @Override
    public void progress(float f) {
        this.displayController.getRootController().getStatusController().updateStatus(f);
    }

}
