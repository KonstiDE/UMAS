package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.ItemSearcher;

public class AgisoftErrorController implements StaticDialogController {

    private final String title;
    private final String content;
    private final String whatnow;

    public AgisoftErrorController(String title, String content, String whatnow){
        this.title = title;
        this.content = content;
        this.whatnow = whatnow;
    }

    @Override
    public void init(DisplayController display, Dialog<String> dialog) throws UMASException {
        String prefix = "agisoft.error.";
        DialogPane pane = dialog.getDialogPane();

        Label uiTitle = ItemSearcher.getItemById(prefix + "title", pane, Label.class);
        Label uiContent = ItemSearcher.getItemById(prefix + "content", pane, Label.class);
        Label uiWhatnow = ItemSearcher.getItemById(prefix + "whatnow", pane, Label.class);

        uiTitle.setText(title);
        uiContent.setText(content);
        uiWhatnow.setText(whatnow);

    }

    @Override
    public void setupResultConverter(Dialog<String> dialog) {}
}
