package wue.eorc.umas.controller.listeners;

import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.enums.agisoft.AgisoftTask;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;

public interface CallbackListener {

    void callbackAgisoft(Pane source, WorkflowType workflowType, AgisoftTask task, String result) throws UMASException;

    void progress(DisplayController display, float f);

}
