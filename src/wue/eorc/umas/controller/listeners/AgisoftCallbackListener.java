package wue.eorc.umas.controller.listeners;

import javafx.scene.layout.StackPane;
import wue.eorc.umas.enums.AgisoftTask;
import wue.eorc.umas.exception.UMASException;

public interface AgisoftCallbackListener {

    void callback(StackPane source, AgisoftTask task, boolean result) throws UMASException;

}
