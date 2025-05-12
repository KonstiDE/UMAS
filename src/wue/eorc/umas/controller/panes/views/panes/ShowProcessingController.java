package wue.eorc.umas.controller.panes.views.panes;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.controller.panes.views.panes.components.ProcessActionsPreparer;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.ProcessingChain;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;
import javafx.scene.layout.Pane;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.DirectoryUtils;
import wue.eorc.umas.utils.ItemSearcher;

import java.io.IOException;
import java.nio.file.Paths;

public class ShowProcessingController implements ViewController {

    private final Flight flight;

    public ShowProcessingController(Flight flight) {
        this.flight = flight;
    }

    public Pane processingPaneRoot;

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        processingPaneRoot = pane;

        Button refresh = ItemSearcher.getItemById("showprocess.refresh", pane, Button.class);
        refresh.setOnAction(_ignored -> refresh(pane, display));

        TabPane tabPane = ItemSearcher.getItemById("showprocess.tabpane", pane, TabPane.class);
        checkProcessingChain(pane, tabPane);

        Button createAgisoft = ItemSearcher.getItemById("showprocess.createagisoft", pane, Button.class);
        createAgisoft.setOnAction(_ignored -> {
            try {
                boolean success = AgisoftCaller.createProject(DirectoryUtils.figureAgisoftFilePath(this.flight));

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

                ProcessActionsPreparer preparer = new ProcessActionsPreparer(flight, workflowType, display, this);
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
            return Paths.get(flight.getFlightDirectory(), "1_Agisoft", fileName).toFile().exists();
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

    public Pane getProcessingPaneRoot() {
        return processingPaneRoot;
    }
}
