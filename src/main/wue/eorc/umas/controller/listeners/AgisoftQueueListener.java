package wue.eorc.umas.controller.listeners;

import wue.eorc.umas.enums.agisoft.AgisoftTask;
import wue.eorc.umas.enums.WorkflowType;

public interface AgisoftQueueListener {

    void enqueue(WorkflowType workflowType, AgisoftTask agisoftTask);
    
    void started(WorkflowType workflowType, AgisoftTask agisoftTask);

    void finish(WorkflowType workflowType, AgisoftTask agisoftTask);

}
