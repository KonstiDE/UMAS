package wue.eorc.umas.enums.agisoft;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import java.util.List;

public enum AlignImages implements AgisoftParameter {

    ACCURACY(List.of("Highest", "High", "Medium", "Low", "Lowest"), 2),
    GENERIC_PRESELECTION(List.of("True", "False"), 0),
    REFERENCE_PRESELECTION(List.of("True", "False"), 0),
    REFERENCE_PRESELECTION_COMBO(List.of("Source", "Estimated", "Sequential"), 0),
    KEY_POINT_LIMIT_PER_MPX(null, 4000),
    TIE_POINT_LIMIT(null, 1000),
    APPLY_MASKS_TO(null, 99),
    EXCLUDE_STAT_TIE_POINTS(List.of("True", "False"), 0),
    GUIDED_MATCHING(List.of("True", "False"), 0),
    ADAPTIVE_MODEL_FITTING(List.of("True", "False"), 0);

    private final List<String> choices;
    private final int defaultIndex;

    AlignImages(List<String> choices, int defaultIndex){
        this.choices = choices;
        this.defaultIndex = defaultIndex;
    }

    @Override
    public List<String> getChoices() {
        return choices;
    }

    @Override
    public int getDefaultIndex() {
        return defaultIndex;
    }

}
