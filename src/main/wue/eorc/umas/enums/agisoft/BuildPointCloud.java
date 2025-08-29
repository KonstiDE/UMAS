package wue.eorc.umas.enums.agisoft;

import java.util.List;

public enum BuildPointCloud implements AgisoftParameter {

    ACCURACY("quality", List.of("Ultra High", "High", "Medium", "Low", "Lowest"), 2),
    DEPTH_FILTERING("quality", List.of("Disabled", "Mild", "Moderate", "Aggressive"), 1),
    REUSE_DEPTH_MAPS("reusedepthmaps", List.of("True", "False"), 1),
    CALCULATE_POINT_COLORS("calculatepointcolors", List.of("True", "False"), 1),
    CALCULATE_POINT_CONFIDENCE("calculatepointconfidence", List.of("Source", "Estimated", "Sequential"), 1);

    private final String id;
    private final List<String> choices;
    private final int defaultIndex;

    BuildPointCloud(String id, List<String> choices, int defaultIndex){
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
