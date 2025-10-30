package wue.eorc.umas.utils.exports;

import java.io.*;
import java.util.*;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import com.pdfjet.*;

public class Exporter {

    private final PDF pdf;

    public Exporter(PDF pdf) {
        this.pdf = pdf;
    }

    public static void toFile(TableView<?> tv, String filename) throws Exception {
        try(FileOutputStream fos = new FileOutputStream(filename)) {
            PDF pdf = new PDF(fos);
            new Exporter(pdf).export(tv);
        }
    }

    public void export(TableView<?> tv) throws Exception {
        Page page = new Page(pdf, A4.LANDSCAPE);

        Font header = new Font(pdf, CoreFont.HELVETICA_BOLD);
        header.setSize(7.0f);

        Font content = new Font(pdf, CoreFont.HELVETICA);
        content.setSize(7.0f);

        Table table = new Table();
        List<List<Cell>> tableData = getData(tv, header, content);
        table.setData(tableData, Table.DATA_HAS_1_HEADER_ROWS);
        table.setCellBordersWidth(0.2f);
        table.setPosition(70.0f, 30.0f);
        table.autoAdjustColumnWidths();
        table.rightAlignNumbers();
        int numOfPages = table.getNumberOfPages(page);
        while (true) {
            Point point = table.drawOn(page);
            if (!table.hasMoreData()) {
                table.resetRenderedPagesCount();
                break;
            }
            page = new Page(pdf, Letter.LANDSCAPE);
        }
        pdf.flush();
    }

    private List<List<Cell>> getData(TableView<?> tv, Font header, Font content) {
        List<?> items = tv.getItems();
        List<List<Cell>> data = new ArrayList<>();
        List<Cell> row = new ArrayList<>();

        for (TableColumn<?, ?> col : tv.getColumns()) {
            row.add(new Cell(header, col.getText()));
        }
        data.add(row);

        for (int i = 0, n = items.size(); i < n; i++) {
            row = new ArrayList<>();
            for (TableColumn<?, ?> col : tv.getColumns()) {
                try {
                    Object value = col.getCellObservableValue(i).getValue();
                    String text = value == null ? "" : value.toString();
                    row.add(new Cell(content, text));
                }catch (Exception ignored){}
            }
            data.add(row);
        }

        return data;
    }
}