package wue.eorc.umas.controller.listeners;

import javafx.scene.layout.Pane;
import wue.eorc.umas.enums.agisoft.AgisoftTask;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;

public interface AgisoftCallbackListener {

    void callback(Pane source, WorkflowType workflowType, AgisoftTask task, String result) throws UMASException;

    void progress(float f);

}
