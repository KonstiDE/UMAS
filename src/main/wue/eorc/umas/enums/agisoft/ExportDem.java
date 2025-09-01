package wue.eorc.umas.enums.agisoft;

import java.util.List;

public enum ExportDem implements AgisoftParameter {

    COORDINATE_SYSTEM("coordinatesystem", List.of("EPSG::4326"), 0),
    RASTER_TRANSFORM("rastertransform", List.of("None", "Palette"), 0),
    NO_DATA_VALUE("nodatavalue", List.of("-32767"), 0),

    WRITE_KML("writekml", List.of("True", "False"), 1),
    WRITE_WORLD_FILE("writeworldfile", List.of("True", "False"), 1),
    WRITE_TILE_SCHEME("writetilescheme", List.of("True", "False"), 1),

    IMAGE_DESCRIPTION("imagedescription", List.of(""), 0),

    WRITE_TILED_TIFF("writetiledtiff", List.of("True", "False"), 0),
    WRITE_BIG_TIFF_FILE("writebigtifffile", List.of("True", "False"), 0),
    GENERATE_TIFF_OVERVIEWS("generatetiffoverviews", List.of("True", "False"), 0);

    private final String id;
    private final List<String> choices;
    private final int defaultIndex;

    ExportDem(String id, List<String> choices, int defaultIndex){
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
