package wue.eorc.umas.enums.agisoft;

import java.util.List;

public enum BuildOrthomosaic implements AgisoftParameter {

    COORDINATE_SYSTEM("coordinatesystem", List.of("EPSG::4326"), 0),
    SURFACE("surface", List.of("DEM"), 0),
    BLENDING_MODE("blendingmode", List.of("Mosaic (default)", "Average", "Diabled"), 0),


    REFINE_SEAMLINES("refineseamlines", List.of("True", "False"), 1),
    ENABLE_HOLE_FILLING("enableholefilling", List.of("True", "False"), 0),
    ENABLE_GHOSTING_FILTER("enableghostingfilter", List.of("True", "False"), 1),
    ENABLE_BACK_FACE_CULLING("enablebackfaceculling", List.of("True", "False"), 1);

    private final String id;
    private final List<String> choices;
    private final int defaultIndex;

    BuildOrthomosaic(String id, List<String> choices, int defaultIndex){
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
