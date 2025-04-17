package controller.panes.mains;

import controller.RootController;
import controller.panes.views.NewMissionViewController;
import enums.ErrorType;
import enums.SplitPanePosition;
import exception.UMASException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import loader.SceneLoader;

import java.util.Optional;

public class MenuController {

    @FXML
    private MenuBar menuBar;

    public  MenuController(RootController rootController, MenuBar menuBar) throws UMASException {
        this.menuBar = menuBar;

        init(rootController);
    }

    private MenuItem getMenuItem(ObservableList<MenuItem> menu, String id) throws UMASException {
        Optional<MenuItem> menuItem = menu.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();

        if(menuItem.isPresent()){
            return menuItem.get();
        }else{
            throw new UMASException(ErrorType.INTERNAL, "Could not find menu-item with the id of \"" + id + "\"");
        }
    }

    private void init(RootController rootController) throws UMASException {
        ObservableList<Menu> menus = this.menuBar.getMenus();

        ObservableList<MenuItem> mainMenu = menus.getFirst().getItems();

        getMenuItem(mainMenu, "newmission").setOnAction(_ -> {

                rootController.getDisplayController().switchSceneTo(
                        SplitPanePosition.LEFT,
                        SceneLoader.getAvailableScenes().get("new_mission"),
                        new NewMissionViewController()
                );
        });

        mainMenu.getLast().setOnAction(ignored -> System.exit(0));


    }

}
