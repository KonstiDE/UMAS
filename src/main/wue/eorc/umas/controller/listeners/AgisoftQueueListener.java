package wue.eorc.umas.controller.listeners;

import wue.eorc.umas.enums.AgisoftTask;
import wue.eorc.umas.enums.WorkflowType;

public interface AgisoftQueueListener {

    void enqueue(WorkflowType workflowType, AgisoftTask agisoftTask);
    
    void started(WorkflowType workflowType, AgisoftTask agisoftTask);

    void finish(WorkflowType workflowType, AgisoftTask agisoftTask);

}
