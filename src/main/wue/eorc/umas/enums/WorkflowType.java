package wue.eorc.umas.enums;

import wue.eorc.umas.enums.agisoft.AgisoftTask;

import java.util.*;

import static wue.eorc.umas.enums.agisoft.AgisoftTask.*;

public enum WorkflowType {
    RGB("RGB", List.of(ImageType.RGB), List.of(ADD_PHOTOS, SET_BRIGHTNESS, ALIGN_IMAGES, OPTIMIZE_CAMERAS, BUILD_POINT_CLOUD, BUILD_DEM, BUILD_ORTHOMOSAIC, EXPORT_DEM, EXPORT_ORTHOMOSAIC, GENERATE_REPORT)),
    MULTISPECTRAL("MS", List.of(ImageType.MULTISPECTRAL, ImageType.CALIBRATION), List.of(ADD_PHOTOS, SET_BRIGHTNESS, CALIBRATE_REFLECTANCE, ALIGN_IMAGES, OPTIMIZE_CAMERAS, BUILD_POINT_CLOUD, BUILD_DEM, BUILD_ORTHOMOSAIC, EXPORT_DEM, EXPORT_ORTHOMOSAIC, GENERATE_REPORT)),
    RGB_PLUS_MULTISPECTRAL("RGB+MS", List.of(ImageType.RGB, ImageType.MULTISPECTRAL, ImageType.CALIBRATION), null),
    IR("IR", List.of(ImageType.IR), null),
    RGB_PLUS_IR("RGB+IR", List.of(ImageType.RGB, ImageType.IR), null),
    HYPERSPECTRAL("HYP", List.of(ImageType.HYPERSPECTRAL), null),
    LIDAR("LIDAR", List.of(ImageType.LIDAR), null),
    INVALID(null, null, null);

    private final String name;
    private final List<ImageType> imageTypes;
    private final List<AgisoftTask> agisoftTasks;

    WorkflowType(String name, List<ImageType> imagesTypes, List<AgisoftTask> agisoftTasks) {
        this.name = name;
        this.imageTypes = imagesTypes;
        this.agisoftTasks = agisoftTasks;
    }

    public String getName() {
        return name;
    }

    public List<ImageType> getImageTypes(){
        return imageTypes;
    }

    public List<AgisoftTask> getAgisoftTasks() {
        return agisoftTasks;
    }

    public static List<WorkflowType> getWorkflowTypesFromImageTypes(Set<ImageType> imageTypes){
        List<WorkflowType> workflowTypes = new ArrayList<>();

        if(imageTypes.isEmpty()){
            return List.of(INVALID);
        }else if(imageTypes.size() == 1){
            return switch (imageTypes.stream().toList().get(0)) {
                case RGB -> List.of(RGB);
                case MULTISPECTRAL, CALIBRATION -> List.of(MULTISPECTRAL);
                case IR -> List.of(IR);
                case HYPERSPECTRAL -> List.of(HYPERSPECTRAL);
                case LIDAR -> List.of(LIDAR);
            };
        }else{
            if(imageTypes.contains(ImageType.RGB) && imageTypes.contains(ImageType.MULTISPECTRAL)){
                workflowTypes.add(RGB);
                workflowTypes.add(MULTISPECTRAL);
                workflowTypes.add(RGB_PLUS_MULTISPECTRAL);
            }else if(imageTypes.contains(ImageType.RGB) && imageTypes.contains(ImageType.IR)){
                workflowTypes.add(RGB);
                workflowTypes.add(IR);
                workflowTypes.add(RGB_PLUS_IR);
            }
        }
        return workflowTypes;
    }

    public static List<AgisoftTask> getAgisoftTasksForWorkflowType(WorkflowType workflowType){
        if (workflowType == RGB){
            return List.of(ADD_PHOTOS, AgisoftTask.SET_BRIGHTNESS, AgisoftTask.ALIGN_IMAGES,
                    AgisoftTask.OPTIMIZE_CAMERAS, AgisoftTask.BUILD_POINT_CLOUD, AgisoftTask.BUILD_DEM,
                    AgisoftTask.BUILD_ORTHOMOSAIC, AgisoftTask.EXPORT_DEM, AgisoftTask.EXPORT_ORTHOMOSAIC,
                    AgisoftTask.GENERATE_REPORT);
        }
        return null;
    }

}
