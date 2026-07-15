package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import javafx.scene.control.*;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.agisoft.BandCalibration;
import wue.eorc.umas.models.agisoft.CalibrationImage;
import wue.eorc.umas.utils.ui.ItemSearcher;

public class CalibrateThermalController implements StaticDialogController  {

    @Override
    public void init(DisplayController display, Dialog<String> dialog) throws UMASException {
        String prefix = "agisoft.calibratethermal.";

        return;
    }

    @Override
    public void setupResultConverter(Dialog<String> dialog) {
        return;
    }

}
