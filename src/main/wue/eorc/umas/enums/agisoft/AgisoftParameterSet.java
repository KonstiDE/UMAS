package wue.eorc.umas.enums.agisoft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum AgisoftParameterSet {
    ALIGN_IMAGES(
            Arrays.asList(AlignImages.values()),
            new HashMap<>(){{
                put("accuracy", "High");
                put("genericpreselection", "True");
                put("referencepreselection", "Source");
                put("keypointlimitpermpx", "4000");
                put("tiepointlimit", "1000");
                put("excludestationarytiepoints", "True");
                put("guidedimagematching", "True");
                put("adaptivecameramodelfitting", "True");
            }}
    );

    private List<AgisoftParameter> list;
    private HashMap<String, String> defaultChoices;

    AgisoftParameterSet(List<AgisoftParameter> list, HashMap<String, String> defaultChoices){
        this.list = list;
        this.defaultChoices = defaultChoices;
    }

    public List<AgisoftParameter> getList() {
        return list;
    }

    public HashMap<String, String> getDefaultChoices() {
        return defaultChoices;
    }
}
