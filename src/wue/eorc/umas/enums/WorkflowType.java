package wue.eorc.umas.enums;

import java.util.List;
import java.util.Set;

public enum WorkflowType {
    RGB,
    MULTISPECTRAL,
    RGB_PLUS_MULTISPECTRAL,
    IR,
    RGB_PLUS_IR,
    HYPERSPECTRAL,
    LIDAR,
    INVALID;

    public static WorkflowType getWorkflowFromImageTypes(List<ImageType> imageTypes){
        if(imageTypes.isEmpty()){
            return INVALID;
        }else if(imageTypes.size() == 1){
            return switch (imageTypes.get(0)) {
                case RGB -> RGB;
                case MULTISPECTRAL -> MULTISPECTRAL;
                case IR -> IR;
                case HYPERSPECTRAL -> HYPERSPECTRAL;
                case LIDAR -> LIDAR;
            };
        }else{
            if()
        }
        return INVALID;
    }

}
