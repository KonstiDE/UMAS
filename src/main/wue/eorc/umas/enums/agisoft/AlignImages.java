package wue.eorc.umas.enums.agisoft;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import wue.eorc.umas.models.AgiSoftTaskBlueprint;
import wue.eorc.umas.utils.AgiSoftList;

import java.util.Arrays;
import java.util.List;

public enum AlignImages {

    TITLE("Align Images", new Separator(Orientation.HORIZONTAL), -1),

    ACCURACY("Accuracy", new ComboBox<>(AgiSoftList.of("Highest", "High", "Medium", "Low", "Lowest")), 2),
    GENERIC_PRESELECTION("Generic Preselection" , new CheckBox(), 0),
    REFERENCE_PRESELECTION("Reference Preselection", new ComboBox<>(AgiSoftList.of("Source", "Seqential")), 0),

    ADVANCED("", new Separator(Orientation.HORIZONTAL), -2),
    KEY_POINT_LIMIT_PER_MPX("Key point limit per Mpx", new TextField("4000"), 4000),
    TIE_POINT_LIMIT("Tie point limit", new TextField("1000"), 1000),
    EXCLUDE_STATIONARY_TIE_POINTS("Exclude stationary tie points", new CheckBox(), 0),
    GUIDED_IMAGE_MATCHING("Guided image matching", new CheckBox(), 0),
    ADAPTIVE_CAMERA_MODEL_FITTING("Adaptive camera model fitting", new CheckBox(), 0);

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
