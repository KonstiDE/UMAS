package wue.eorc.umas.enums.agisoft;

public enum AgisoftDialog {

    SET_BRIGHTNESS("agisoft_set_brightness"),
    ALIGN_IMAGES("agisoft_align_photos"),
    OPTIMIZE_CAMERAS("agisoft_optimize_cameras"),
    BUILD_POINT_CLOUD("agisoft_build_point_cloud"),
    BUILD_DEM("agisoft_build_dem"),
    BUILD_ORTHOMOSAIC("agisoft_build_orthomosaic"),
    EXPORT_DEM("agisoft_export_dem"),
    EXPORT_ORTHOMOSAIC("agisoft_export_orthomosaic"),
    GENERATE_REPORT("agisoft_generate_report"),

    BATCH_EDIT("agisoft_batch_edit");

    private final String dialogId;

    AgisoftDialog(String dialogId){
        this.dialogId = dialogId;
    }

    public String getDialogId() {
        return dialogId;
    }
}
