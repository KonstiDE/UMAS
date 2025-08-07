package wue.eorc.umas.controller.listeners;

import wue.eorc.umas.enums.AgisoftTask;

public interface AgisoftQueueListener {

    void enqueue(AgisoftTask agisoftTask);
    
    void started(AgisoftTask agisoftTask);

    void finish();

}
