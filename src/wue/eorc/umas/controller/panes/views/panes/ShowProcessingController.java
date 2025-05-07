package wue.eorc.umas.controller.panes.views.panes;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.ProcessingChain;
import wue.eorc.umas.exception.UMASException;
import javafx.scene.layout.Pane;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.ItemSearcher;

import java.nio.file.Paths;

public class ShowProcessingController implements ViewController {

    private final Flight flight;

    public ShowProcessingController(Flight flight) {
        this.flight = flight;
    }

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        Button refresh = ItemSearcher.getItemById("showprocess.refresh", pane, Button.class);
        refresh.setOnAction(_ignored -> {
            try {
                init(pane, display);
            } catch (UMASException ex) {
                UMASException.throwWindow(ErrorType.INTERNAL, "Could not refresh the processing display. Please restart the application!");
            }
        });

        checkProcessingChain(pane, display);


    }

    private void checkProcessingChain(Pane pane, DisplayController display) throws UMASException {
        Button createAgisoft = ItemSearcher.getItemById("showprocess.createagisoft", pane, Button.class);
        Button createTerra =  ItemSearcher.getItemById("showprocess.createterra", pane, Button.class);
        Circle statusAigsoft = ItemSearcher.getItemById("showprocess.statusagisoft", pane, Circle.class);
        Circle statusTerra = ItemSearcher.getItemById("showprocess.statusterra", pane, Circle.class);

        TabPane tabPane = ItemSearcher.getItemById("showprocess.tabpane", pane, TabPane.class);
        Tab tabAgisoft = tabPane.getTabs().get(0);
        Tab tabTerra = tabPane.getTabs().get(1);

        if (projectExistsAgisoft(this.flight)){
            createAgisoft.setDisable(true);
            statusAigsoft.setFill(Paint.valueOf("GREEN"));
            tabAgisoft.setDisable(false);
        }else{
            createAgisoft.setDisable(false);
            createAgisoft.setOnAction(_ignored -> {

            });
            statusAigsoft.setFill(Paint.valueOf("RED"));
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

}
