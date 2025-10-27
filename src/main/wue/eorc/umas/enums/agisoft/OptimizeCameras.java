package wue.eorc.umas.enums.agisoft;

import wue.eorc.umas.utils.agisoft.AgiSoftList;

import java.util.List;

public enum OptimizeCameras implements AgisoftParameter {

    FIT_F("fitf", AgiSoftList.of("True", "False"), 0),
    FIT_K1("fitk1", AgiSoftList.of("True", "False"), 0),
    FIT_K2("fitk2", AgiSoftList.of("True", "False"), 0),
    FIT_K3("fitk3", AgiSoftList.of("True", "False"), 0),
    FIT_K4("fitk4", AgiSoftList.of("True", "False"), 1),
    FIT_CX_CY("fitcxcy", AgiSoftList.of("True", "False"), 0),
    FIT_P1("fitp1", AgiSoftList.of("True", "False"), 0),
    FIT_P2("fitp2", AgiSoftList.of("True", "False"), 0),
    FIT_B1("fitb1", AgiSoftList.of("True", "False"), 1),
    FIT_B2("fitb2", AgiSoftList.of("True", "False"), 1),

    ADAPTIVE_FITTING("adaptivecameramodelfitting", AgiSoftList.of("True", "False"), 1),
    ESTIMATING_TIE_COV("estimatetiepointcovariance", AgiSoftList.of("True", "False"), 1),
    FIT_ADDITIONAL("fitadditionalcorrections", AgiSoftList.of("True", "False"), 1);

    private final String id;
    private final List<String> choices;
    private final int defaultIndex;

    OptimizeCameras(String id, List<String> choices, int defaultIndex){
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
