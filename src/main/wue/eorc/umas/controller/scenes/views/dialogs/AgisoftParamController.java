package wue.eorc.umas.controller.scenes.views.dialogs;

import com.google.gson.Gson;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.AgiSoftTaskBlueprint;
import wue.eorc.umas.utils.ItemSearcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgisoftParamController implements DynamicDialogController {

    private final Gson gson = new Gson();

    private final HashMap<String, Node> nodeSaver = new HashMap<>();

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog, List<AgiSoftTaskBlueprint> data) throws UMASException {
        GridPane gridPane = ItemSearcher.getItemById("dynamicdialog.grid", pane, GridPane.class);

        int index = 0;

        for (AgiSoftTaskBlueprint blueprint : data) {
            // Styling
            if (blueprint.getDefault() >= 0) {
                gridPane.addRow(index, new Label(blueprint.getDescription()), blueprint.getNode());
                nodeSaver.put(blueprint.getId(), blueprint.getNode());

                // Actual Node to process
                if (blueprint.getNode() instanceof ComboBox<?>) {
                    ((ComboBox<?>) blueprint.getNode()).getSelectionModel().select(blueprint.getDefault());
                } else if (blueprint.getNode() instanceof CheckBox) {
                    ((CheckBox) blueprint.getNode()).setSelected(blueprint.getDefault() == 0);
                }
            } else {
                // Title or subtitle to process
                Label label = new Label(blueprint.getDescription());

                gridPane.addRow(index, label);
                index++;
                gridPane.addRow(index, blueprint.getNode(), new Separator()); // Adds to seperator twice

                if (blueprint.getDefault() == -1) {
                    label.setFont(Font.font(14));
                } else if (blueprint.getDefault() == -2) {
                    label.setFont(Font.font(12));
                } else {
                    UMASException.throwWindow(ErrorType.INTERNAL, "Could not create a title or subtitle with an default-index of " + blueprint.getDefault());
                }
            }
            index++;

        }

    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        if(buttonType == ButtonType.CANCEL){
            return null;
        }else if(buttonType == ButtonType.OK){
            HashMap<String, String> choices = new HashMap<>();

            for (Map.Entry<String, Node> entry : nodeSaver.entrySet()){
                if(entry.getValue() instanceof ComboBox<?>){
                    choices.put(entry.getKey(), ((ComboBox<?>) entry.getValue()).getSelectionModel().getSelectedItem().toString());

                } else if(entry.getValue() instanceof CheckBox){
                    choices.put(entry.getKey(), ((CheckBox) entry.getValue()).isSelected() ? "True" : "False");

                } else if(entry.getValue() instanceof TextField){
                    choices.put(entry.getKey(), ((TextField) entry.getValue()).getText());
                }
            }

            return gson.toJson(choices);
        }

        return null;
    }

}
