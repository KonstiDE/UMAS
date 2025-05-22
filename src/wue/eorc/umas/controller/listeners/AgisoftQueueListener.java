package wue.eorc.umas.controller.listeners;

import wue.eorc.umas.enums.AgisoftTask;
import wue.eorc.umas.models.Flight;

public interface AgisoftQueueListener {

    void enqueue(AgisoftTask agisoftTask);
    
    void started(AgisoftTask agisoftTask);

    void finish();

}
