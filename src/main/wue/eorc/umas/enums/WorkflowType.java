package wue.eorc.umas.enums;

import wue.eorc.umas.enums.agisoft.AgisoftTask;

import java.util.*;

public enum WorkflowType {
    RGB("RGB", ImageType.RGB),
    MULTISPECTRAL("MS", ImageType.MULTISPECTRAL),
    RGB_PLUS_MULTISPECTRAL("RGB+MS", ImageType.RGB, ImageType.MULTISPECTRAL),
    IR("IR", ImageType.IR),
    RGB_PLUS_IR("RGB+IR", ImageType.RGB, ImageType.IR),
    HYPERSPECTRAL("HYP", ImageType.HYPERSPECTRAL),
    LIDAR("LIDAR", ImageType.LIDAR),
    INVALID(null);

    private final String name;
    private final List<ImageType> imageTypes;

    WorkflowType(String name, ImageType... imageTypes) {
        this.name = name;
        this.imageTypes = Arrays.stream(imageTypes).toList();
    }

    public String getName() {
        return name;
    }

    public List<ImageType> getImageTypes(){
        return imageTypes;
    }

    public static List<WorkflowType> getWorkflowTypesFromImageTypes(Set<ImageType> imageTypes){
        List<WorkflowType> workflowTypes = new ArrayList<>();

        if(imageTypes.isEmpty()){
            return List.of(INVALID);
        }else if(imageTypes.size() == 1){
            return switch (imageTypes.stream().toList().get(0)) {
                case RGB -> List.of(RGB);
                case MULTISPECTRAL -> List.of(MULTISPECTRAL);
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
            return List.of(AgisoftTask.ADD_PHOTOS, AgisoftTask.SET_BRIGHTNESS, AgisoftTask.ALIGN_IMAGES,
                    AgisoftTask.OPTIMIZE_CAMERAS, AgisoftTask.BUILD_POINT_CLOUD, AgisoftTask.BUILD_DEM,
                    AgisoftTask.BUILD_ORTHOMOSAIC, AgisoftTask.EXPORT_DEM, AgisoftTask.EXPORT_ORTHOMOSAIC,
                    AgisoftTask.GENERATE_REPORT);
        }
        return null;
    }

}
