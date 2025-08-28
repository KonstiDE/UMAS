package wue.eorc.umas.enums.agisoft;

import javafx.scene.Node;

import java.util.HashMap;
import java.util.List;

public interface AgisoftParameter {

    String getId();
    List<String> getChoices();
    int getDefaultIndex();

}
