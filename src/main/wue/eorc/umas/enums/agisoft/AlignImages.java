package wue.eorc.umas.enums.agisoft;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import wue.eorc.umas.models.AgiSoftTaskBlueprint;
import wue.eorc.umas.utils.AgiSoftList;

import java.util.Arrays;
import java.util.List;

public enum AlignImages {

    ACCURACY("Accuracy", new ComboBox<>(AgiSoftList.of("Highest", "High", "Medium", "Low", "Lowest")), 2),
    GENERIC_PRESELECTION("Generic Preselection" , new ComboBox<>(AgiSoftList.of("Yes", "No")), 0);

    private final String description;
    private final Node node;
    private final int defaultIndex;

    AlignImages(String description, Node node, int defaultIndex) {
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

    public int getDefaultValue() {
        return defaultIndex;
    }

    public static List<AgiSoftTaskBlueprint> getSettings() {
        return Arrays.stream(values()).map(alignImages ->
                new AgiSoftTaskBlueprint(alignImages.description, alignImages.node, alignImages.defaultIndex)).toList();
    }
}
