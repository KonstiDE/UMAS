package wue.eorc.umas.enums.agisoft;

import wue.eorc.umas.models.AgiSoftTaskBlueprint;

import java.util.List;

public enum AgisoftTaskSetting {

    ALIGN_IMAGES(AlignImages.getSettings());

    private final List<AgiSoftTaskBlueprint> blueprints;

    AgisoftTaskSetting(List<AgiSoftTaskBlueprint> blueprints){
        this.blueprints = blueprints;
    }

    public List<AgiSoftTaskBlueprint> getBlueprints(){
        return blueprints;
    }

}
