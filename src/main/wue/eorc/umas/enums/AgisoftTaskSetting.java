package wue.eorc.umas.enums;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import wue.eorc.umas.enums.agisoft.AlignImages;

import java.util.HashMap;

public enum AgisoftTaskSetting {

    ALIGN_IMAGES(new HashMap<>() {{
        put("Accuracy", new ComboBox<>(AlignImages.ACCURACY.getChoices()));
    }});


    private final HashMap<String, Node> parameters;

    AgisoftTaskSetting(HashMap<String, Node> parameters) {
        this.parameters = parameters;
    }

    public HashMap<String, Node> getParameters(){
        return parameters;
    }

}
