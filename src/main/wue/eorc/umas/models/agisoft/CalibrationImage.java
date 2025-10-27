package wue.eorc.umas.models.agisoft;

public class CalibrationImage {

    private String label;
    private String panel;
    private String path;

    public CalibrationImage(String label, String panel, String path) {
        this.label = label;
        this.panel = panel;
        this.path = path;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPanel() {
        return panel;
    }

    public void setPanel(String panel) {
        this.panel = panel;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
