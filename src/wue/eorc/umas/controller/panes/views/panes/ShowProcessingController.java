package wue.eorc.umas.controller.panes.views.panes;

import com.agisoft.metashape.Document;
import javafx.scene.control.Button;
import wue.eorc.umas.agisoft.ConsoleProgress;
import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.enums.ProcessingChain;
import wue.eorc.umas.exception.UMASException;
import javafx.scene.layout.Pane;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.ItemSearcher;

import java.nio.file.Paths;

public class ShowProcessingController implements ViewController {

    private final Flight flight;

    public ShowProcessingController(Flight flight) {
        this.flight = flight;
    }

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        Button create = ItemSearcher.getItemById("showprocess.createproject", pane, Button.class);

        create.setOnAction(e -> {
            Document document = new Document();
            document.save(Paths.get(flight.getFlightDirectory(), "1_Agisoft", "agisoft.psx").toFile().getAbsolutePath(), new ConsoleProgress());
        });

    }

    private boolean processingExists(){
        if(flight.getProcessingChain() == ProcessingChain.AGISOFT){
            return Paths.get(flight.getFlightDirectory(), "1_Agisoft", "agisoft.psx").toFile().exists();
        }
        return false;
    }

}
