package wue.eorc.umas.enums;

import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.HashMap;

public enum AgisoftTaskSetting {


    ALIGN_IMAGES(new HashMap<>() {{
        put("", new Label());
    }});


    private final HashMap<Node, Node> parameters;

    AgisoftTaskSetting(HashMap<Node, Node> parameters) {
        this.parameters = parameters;
    }

    public HashMap<Node, Node> getParameters(){
        return parameters;
    }

}
