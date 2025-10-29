package wue.eorc.umas.controller.scenes.views.panes;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.listeners.CallbackListener;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.panes.components.ProcessActionsPreparer;
import wue.eorc.umas.enums.agisoft.AgisoftTask;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.ProcessingChain;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.system.Colors;
import wue.eorc.umas.utils.system.DirectoryUtils;
import wue.eorc.umas.utils.ui.ItemSearcher;

import java.io.IOException;
import java.nio.file.Paths;

public class ShowProcessingController implements ViewController, CallbackListener {

    private final AgisoftCaller agisoftCaller;
    private final Flight flight;

    public ShowProcessingController(Flight flight, DisplayController display) {
        this.flight = flight;
        this.agisoftCaller = new AgisoftCaller(this, display);
    }

    public Pane processingPaneRoot;

    public DisplayController displayController;

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        displayController = display;
        processingPaneRoot = pane;

        Button refresh = ItemSearcher.getItemById("showprocess.refresh", pane, Button.class);
        refresh.setOnAction(_ignored -> refresh(pane, display));

        TabPane tabPane = ItemSearcher.getItemById("showprocess.tabpane", pane, TabPane.class);
        checkProcessingChain(pane, tabPane);

        Button createAgisoft = ItemSearcher.getItemById("showprocess.createagisoft", pane, Button.class);
        createAgisoft.setOnAction(_ignored -> {
            try {
                boolean success = agisoftCaller.createProject(DirectoryUtils.figureAgisoftFilePath(this.flight));

                //TODO Somehow this shit is still false... Although I return vn:CREATE_PROJECT:true. ass code!
                //need to check for if(success){ ... }
                refresh(pane, display);

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
    public void callbackAgisoft(Pane source, WorkflowType workflowType, AgisoftTask task, String result) throws UMASException {
        switch (task){
            case ADD_PHOTOS_CHECK, ALIGN_IMAGES_CHECK, OPTIMIZE_CAMERAS_CHECK, BUILD_DEM_CHECK, BUILD_ORTHOMOSAIC_CHECK,
                 EXPORT_DEM_CHECK, EXPORT_ORTHOMOSAIC_CHECK, BUILD_POINT_CLOUD_CHECK, SET_BRIGHTNESS_CHECK,
                 GENERATE_REPORT_CHECK, CALIBRATE_REFLECTANCE_CHECK -> {

                try{
                    StackPane workFlowStep = ItemSearcher.getItemById("processing." + task.name().replace("_CHECK", ""), source, StackPane.class);

                    Rectangle rectangle = ItemSearcher.getItemById("processing.rectangle", workFlowStep, Rectangle.class);
                    ProgressIndicator indicator = ItemSearcher.getItemById("processing.indicator", workFlowStep, ProgressIndicator.class);

                    indicator.setVisible(false);

                    boolean success = Boolean.parseBoolean(result);

                    if(success){
                        rectangle.setFill(Colors.PROC_GREEN);
                        //this.showProcessingController.refresh(this.showProcessingController.getProcessingPaneRoot(), display);
                    }else{
                        rectangle.setFill(Colors.PROC_RED);
                    }
                }catch (Exception ignored){  }

            }
            case SET_BRIGHTNESS_ESTIMATE -> {
                TextField brightness = ItemSearcher.getItemById("agisoft.setbrightness.brightness", source, TextField.class);
                TextField contrast = ItemSearcher.getItemById("agisoft.setbrightness.contrast", source, TextField.class);
                Button estimate = ItemSearcher.getItemById("agisoft.setbrightness.estimate", source, Button.class);
                ProgressIndicator indicator = ItemSearcher.getItemById("agisoft.setbrightness.progressindicator", source, ProgressIndicator.class);

                String[] split = result.split("#");

                brightness.setText(split[0]);
                contrast.setText(split[1]);

                estimate.setDisable(false);
                indicator.setVisible(false);
            }
        }
    }

    @Override
    public void progress(float f) {
        this.displayController.getRootController().getStatusController().updateStatus(f);
    }

    public Flight getFlight() {
        return flight;
    }

}
