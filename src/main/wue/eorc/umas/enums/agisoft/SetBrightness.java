package wue.eorc.umas.enums.agisoft;

import wue.eorc.umas.utils.agisoft.AgiSoftList;

import java.util.List;

public enum SetBrightness implements AgisoftParameter {

    BRIGHTNESS("brightness", AgiSoftList.of("100"), 0),
    CONTRAST("contrast", AgiSoftList.of("100"), 0);

    private final String id;
    private final List<String> choices;
    private final int defaultIndex;

    SetBrightness(String id, List<String> choices, int defaultIndex){
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
