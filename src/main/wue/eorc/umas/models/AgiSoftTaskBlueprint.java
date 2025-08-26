package wue.eorc.umas.models;

import javafx.scene.Node;
import javafx.scene.control.Separator;

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

    public String getId(){
        return description.toLowerCase().replaceAll("[\\s|\\u00A0]+", "");
    }

    /**
     * @return instance of a new section for dynamic dialog building
     * @implNote defaultIndex -1 corresponds to a header, -2 to a sub-header
     */
    public static AgiSoftTaskBlueprint createHeader(String title){
        return new AgiSoftTaskBlueprint(title, new Separator(), -1);
    }

    /**
     * @return instance of a new section for dynamic dialog building
     * @implNote defaultIndex -1 corresponds to a header, -2 to a sub-header
     */
    public static AgiSoftTaskBlueprint createSubHeader(String title){
        return new AgiSoftTaskBlueprint(title, new Separator(), -2);
    }

}
