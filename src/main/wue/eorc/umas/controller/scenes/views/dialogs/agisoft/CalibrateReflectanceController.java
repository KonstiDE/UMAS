package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import javafx.scene.control.*;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.agisoft.BandCalibration;
import wue.eorc.umas.models.agisoft.CalibrationImage;
import wue.eorc.umas.utils.ui.ItemSearcher;

public class CalibrateReflectanceController implements StaticDialogController {

    private TableView<CalibrationImage> calibrationImageTableView;
    private TableView<BandCalibration> bandCalibrationTableView;

    private Button locatePanels;
    private Button selectPanel;

    private CheckBox useReflectancePanels;
    private CheckBox useSunSensor;


    @Override
    public void init(DisplayController display, Dialog<String> dialog) throws UMASException {
        String prefix = "agisoft.buildpointcloud.";
        DialogPane pane = dialog.getDialogPane();

        locatePanels = ItemSearcher.getItemById(prefix + "locatepanels", pane, Button.class);
        locatePanels.setOnAction(event -> {

        });
    }

    @Override
    public void setupResultConverter(Dialog<String> dialog) {

    }
}
