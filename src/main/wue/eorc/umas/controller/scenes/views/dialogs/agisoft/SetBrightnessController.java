package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import com.google.gson.Gson;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.controller.scenes.views.panes.components.ProcessActionsPreparer;
import wue.eorc.umas.enums.agisoft.SetBrightness;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.AgisoftParamInitiator;
import wue.eorc.umas.utils.GsonTypeTokens;
import wue.eorc.umas.utils.ItemSearcher;

import java.net.URISyntaxException;
import java.util.HashMap;

public class SetBrightnessController implements StaticDialogController {

    private TextField brightness;
    private TextField contrast;
    private Button estiamte;

    private ProcessActionsPreparer preparer;

    public SetBrightnessController(ProcessActionsPreparer preparer){
        this.preparer = preparer;
    }

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        String prefix = "agisoft.setbrightness.";

        brightness = ItemSearcher.getItemById(prefix + "brightness", pane, TextField.class);
        AgisoftParamInitiator.initTextField(brightness, SetBrightness.BRIGHTNESS);

        contrast = ItemSearcher.getItemById(prefix + "contrast", pane, TextField.class);
        AgisoftParamInitiator.initTextField(contrast, SetBrightness.CONTRAST);

        estiamte = ItemSearcher.getItemById(prefix + "estimate", pane, Button.class);
        estiamte.setOnAction(actionEvent -> {
            try {
                preparer.callBrightnessEstimate();
            } catch (UMASException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        if (buttonType == ButtonType.OK) {
            Gson gson = new Gson();

            HashMap<String, String> parameterMap = new HashMap<>(){{
                put("brightness", brightness.getText());
                put("contrast", contrast.getText());
            }};

            return gson.toJson(parameterMap, GsonTypeTokens.hashmapToken);
        } else if (buttonType == ButtonType.CANCEL) {
            return null;
        }

        return null;
    }
}
