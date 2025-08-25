package wue.eorc.umas.controller.scenes.views.dialogs;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.AgiSoftTaskBlueprint;
import wue.eorc.umas.utils.ItemSearcher;

import java.util.List;
import java.util.Map;

public class AgisoftParamController implements DynamicDialogController {

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog, List<AgiSoftTaskBlueprint> data) throws UMASException {
        GridPane gridPane = ItemSearcher.getItemById("dynamicdialog.grid", pane, GridPane.class);

        for(AgiSoftTaskBlueprint blueprint : data){
            gridPane.addRow(0, new Label(blueprint.getDescription()), blueprint.getNode());

            if(blueprint.getNode() instanceof ComboBox<?>){
                ((ComboBox<?>) blueprint.getNode()).getSelectionModel().select(blueprint.getDefault());
            }

        }

    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        return "";
    }
}
