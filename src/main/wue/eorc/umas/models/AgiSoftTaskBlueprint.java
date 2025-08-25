package wue.eorc.umas.models;

import javafx.scene.Node;

public class AgiSoftTaskBlueprint {

    private final String description;
    private final Node node;
    private final int defaultIndex;

    public AgiSoftTaskBlueprint(String description, Node node, int defaultIndex) {
        this.description = description;
        this.node = node;
        this.defaultIndex = defaultIndex;
    }

    public String getDescription() {
        return description;
    }

    public Node getNode() {
        return node;
    }

    public int getDefault() {
        return defaultIndex;
    }

}
