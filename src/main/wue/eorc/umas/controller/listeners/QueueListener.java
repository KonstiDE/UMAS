package wue.eorc.umas.controller.listeners;

import wue.eorc.umas.enums.agisoft.AgisoftTask;
import wue.eorc.umas.enums.WorkflowType;

public interface QueueListener {

    void enqueueAgisoft(WorkflowType workflowType, AgisoftTask agisoftTask);
    
    void startedAgisoft(WorkflowType workflowType, AgisoftTask agisoftTask);

    void finishAgisoft(WorkflowType workflowType, AgisoftTask agisoftTask);

}
