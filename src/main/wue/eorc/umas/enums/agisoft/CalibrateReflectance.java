package wue.eorc.umas.enums.agisoft;

import java.util.List;

public enum CalibrateReflectance implements AgisoftParameter {

    ;

    private final String id;
    private final List<String> choices;
    private final int defaultIndex;

    CalibrateReflectance(String id, List<String> choices, int defaultIndex){
        this.id = id;
        this.choices = choices;
        this.defaultIndex = defaultIndex;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<String> getChoices() {
        return choices;
    }

    @Override
    public int getDefaultIndex() {
        return defaultIndex;
    }

}
