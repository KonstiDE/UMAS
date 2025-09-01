package wue.eorc.umas.enums.agisoft;

import java.util.List;

public enum BuildDem implements AgisoftParameter {

    COORDINATE_SYSTEM("coordinatesystem", List.of("EPSG::4326"), 0),
    SOURCE_DATA("sourcedata", List.of("Tie points", "Depth maps", "Dense Cloud"), 2),
    QUALITY("quality", List.of("Ultra High", "High", "Medium", "Low", "Lowest"), 2),
    INTERPOLATION("interpolation", List.of("Disabled", "Enabled (default)", "Extrapolated"), 1);

    private final String id;
    private final List<String> choices;
    private final int defaultIndex;

    BuildDem(String id, List<String> choices, int defaultIndex){
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
