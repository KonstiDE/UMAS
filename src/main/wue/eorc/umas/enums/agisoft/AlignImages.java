package wue.eorc.umas.enums.agisoft;

import java.util.List;

public enum AlignImages implements AgisoftParameter {

    ACCURACY("accuracy", List.of("Highest", "High", "Medium", "Low", "Lowest"), 2),
    GENERIC_PRESELECTION("genericpreselection", List.of("True", "False"), 0),
    REFERENCE_PRESELECTION("referencepreselection", List.of("True", "False"), 0),
    REFERENCE_PRESELECTION_COMBO("referencepreselectioncombo", List.of("Source", "Estimated", "Sequential"), 0),
    KEY_POINT_LIMIT("keypointlimit", List.of("1"), 1),
    KEY_POINT_LIMIT_PER_MPX("keypointlimitpermpx", List.of("4000"), 0),
    TIE_POINT_LIMIT("tiepointlimit", List.of("1000"), 0),
    APPLY_MASKS_TO("", List.of("None"), 0),
    EXCLUDE_STAT_TIE_POINTS("excludestationarytiepoints", List.of("True", "False"), 0),
    GUIDED_MATCHING("guidedimagematching", List.of("True", "False"), 0),
    ADAPTIVE_MODEL_FITTING("adaptivecameramodelfitting", List.of("True", "False"), 0);

    private final String id;
    private final List<String> choices;
    private final int defaultIndex;

    AlignImages(String id, List<String> choices, int defaultIndex){
        this.id = id;
        this.choices = choices;
        this.defaultIndex = defaultIndex;
    }

    @Override
    public String getId() {
        return id;
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
