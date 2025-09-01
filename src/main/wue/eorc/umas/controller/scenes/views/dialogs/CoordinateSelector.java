package wue.eorc.umas.controller.scenes.views.dialogs;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.CoordinateSystem;
import wue.eorc.umas.utils.ItemSearcher;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoordinateSelector implements StaticDialogController {

    private TableView<CoordinateSystem> table;

    private final List<CoordinateSystem> library = new ArrayList<>();

    private CoordinateSystem selectedSystem;

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        table = ItemSearcher.getGenericControlById("table", pane, TableView.class, CoordinateSystem.class);
        initTableViewCellFactories(table);

        try {
            Reader in = new FileReader(Path.of(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("data/coordinate-systems.csv")).toURI()).toString());

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder().get();

            Iterable<CSVRecord> records = csvFormat.parse(in);

            for (CSVRecord record : records) {
                if (record.getRecordNumber() > 1){
                    String name = record.get(0);
                    String code = record.get(1);
                    String dataSource = record.get(4);
                    String notes = record.get(5);

                    CoordinateSystem system = new CoordinateSystem(name, dataSource + "::" + code, notes);
                    table.getItems().add(system);
                    library.add(system);
                }

            }

        } catch (IOException | URISyntaxException e){
            UMASException.throwWindow(ErrorType.INTERNAL, "Something went wrong parsing the CSV");
        }

        TextField search = ItemSearcher.getItemById("searchbar", pane, TextField.class);
        search.textProperty().addListener((opt, oldVal, newVal) -> {
            List<CoordinateSystem> filtered = library.stream().filter((e) -> e.name().contains(newVal) | e.id().contains(newVal)).toList();
            table.getItems().clear();
            table.getItems().addAll(filtered);
        });


        table.setRowFactory( tv -> {
            TableRow<CoordinateSystem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    selectedSystem = row.getItem();
                    dialog.setResult(selectedSystem.toString());
                }
            });
            return row ;
        });

    }

    @SuppressWarnings("unchecked")
    private void initTableViewCellFactories(TableView<CoordinateSystem> tableView) {
        TableColumn<CoordinateSystem, String> nameCol = (TableColumn<CoordinateSystem, String>) tableView.getColumns().get(0);
        nameCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().name()));

        TableColumn<CoordinateSystem, String> idCol = (TableColumn<CoordinateSystem, String>) tableView.getColumns().get(1);
        idCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().id()));
    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        if (buttonType == ButtonType.OK) {
            if (table.getSelectionModel().getSelectedItem() != null){
                selectedSystem = table.getSelectionModel().getSelectedItem();
            }

            if (selectedSystem != null) {
                return selectedSystem.toString();

            }else{
                return null;
            }

        } else if(buttonType == ButtonType.CANCEL) {
            return null;
        }
        return null;
    }

    @Override
    public String toString() {
        return String.join("#", selectedSystem.name(), selectedSystem.id());
    }

    public static CoordinateSystem fromString(String s) {
        String[] split = s.split("#");

        return new CoordinateSystem(split[0], split[1], null);
    }

}
