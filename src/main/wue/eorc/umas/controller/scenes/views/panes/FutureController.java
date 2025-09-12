package wue.eorc.umas.controller.scenes.views.panes;

import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.exception.UMASException;

import java.util.concurrent.CompletableFuture;

public interface FutureController {

    CompletableFuture<Void> init(Pane pane, DisplayController display) throws UMASException;

}
