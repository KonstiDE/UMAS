package wue.eorc.umas.enums.agisoft;

import java.util.List;

public enum SetBrightness implements AgisoftParameter {

    BRIGHTNESS(null, 100),
    CONTRAST(null, 100);

    private final List<String> choices;
    private final int defaultIndex;

    SetBrightness(List<String> choices, int defaultIndex){
        this.choices = choices;
        this.defaultIndex = defaultIndex;
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
