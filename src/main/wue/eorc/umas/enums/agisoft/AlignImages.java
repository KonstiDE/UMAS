package wue.eorc.umas.enums.agisoft;

import javafx.collections.ObservableList;
import wue.eorc.umas.utils.AgiSoftList;

public enum AlignImages {

    ACCURACY(AgiSoftList.of("Highest", "High", "Medium", "Low", "Lowest")),
    GENERIC_PRESELECTION(AgiSoftList.of("Yes", "No"));

    private ObservableList<String> choices;

    AlignImages(ObservableList<String> choices){
        this.choices = choices;
    }

    public ObservableList<String> getChoices(){
        return choices;
    }

}
