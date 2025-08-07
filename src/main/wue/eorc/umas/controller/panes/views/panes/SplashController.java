package wue.eorc.umas.controller.panes.views.panes;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.ItemSearcher;

public class SplashController implements ViewController{

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        AnchorPane innerParent = ItemSearcher.getItemById("splash:innerparent", pane, AnchorPane.class);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //AnchorPane outer = (AnchorPane) innerParent.getParent();
                //innerParent.setPrefHeight(outer.getScene().getHeight() - 40);
            }
        });

        //ImageView imageView = ItemSearcher.getItemById("splash:imageview", pane, ImageView.class);

        //if(!Settings.getSetting(Setting.UITHEME).equals("Dark")){
            //imageView.setImage(new Image("wue/eorc/umas/assets/splash/EOR_JMU_2-2048x179_black.png"));
        //}else{
            //imageView.setImage(new Image("wue/eorc/umas/assets/splash/EOR_JMU_2-2048x179.png"));
        //}

    }

}
