package wue.eorc.umas.controller.panes.mains;

import javafx.scene.control.DialogPane;
import wue.eorc.umas.controller.RootController;
import wue.eorc.umas.controller.panes.views.dialogs.SettingsController;
import wue.eorc.umas.controller.panes.views.panes.CreateProjectController;
import wue.eorc.umas.controller.panes.views.panes.ShowFlightsController;
import wue.eorc.umas.controller.panes.views.panes.ShowProjectController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.SplitPanePosition;
import wue.eorc.umas.exception.UMASException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import wue.eorc.umas.loader.ProjectCache;
import wue.eorc.umas.loader.SceneLoader;
import wue.eorc.umas.models.Project;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
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
                .filter(m -> m.getId() != null && m.getId().equals(id))
                .findFirst();

        if(menuItem.isPresent()){
            return menuItem.get();
        }else{
            throw new UMASException(ErrorType.INTERNAL, "Could not find menu-item with the id of \"" + id + "\"");
        }
    }

    private void init(RootController rootController) throws UMASException {
        ObservableList<Menu> menus = this.menuBar.getMenus();

        ObservableList<MenuItem> mainMenu = menus.get(0).getItems();

        getMenuItem(mainMenu, "newmission").setOnAction(_ignored -> {

                rootController.getDisplayController().switchSceneTo(
                        SplitPanePosition.LEFT,
                        SceneLoader.getAvailableScenes().get("new_mission"),
                        new CreateProjectController()
                );
        });

        getMenuItem(mainMenu, "openmission").setOnAction(_ignored -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Mission Directory");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("UMASPROJECT files (*.umasproject)", "*.umasproject"));
            File projectFile = fileChooser.showOpenDialog(rootController.getDisplayController().rootControl.getScene().getWindow());

            if(projectFile != null){
                try {
                    Project project = Project.read(projectFile.getAbsolutePath());

                    rootController.getDisplayController().switchSceneTo(
                            SplitPanePosition.LEFT,
                            SceneLoader.getAvailableScenes().get("show_mission"),
                            new ShowProjectController(project)
                    );

                    rootController.getDisplayController().switchSceneTo(
                            SplitPanePosition.CENTER,
                            SceneLoader.getAvailableScenes().get("show_flights"),
                            new ShowFlightsController()
                    );

                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Menu recentMenu = (Menu) menus.get(0).getItems().get(2);
        for(Map.Entry<String, Timestamp> entry : ProjectCache.cache.entrySet()){
            File projectFile = new File(entry.getKey());

            recentMenu.getItems().add(new MenuItem(projectFile.getName()));
            recentMenu.getItems().get(recentMenu.getItems().size() - 1).setOnAction(_ignored -> {
                try {
                    Project project = Project.read(projectFile.getAbsolutePath());

                    rootController.getDisplayController().switchSceneTo(
                            SplitPanePosition.LEFT,
                            SceneLoader.getAvailableScenes().get("show_mission"),
                            new ShowProjectController(project)
                    );

                    rootController.getDisplayController().switchSceneTo(
                            SplitPanePosition.CENTER,
                            SceneLoader.getAvailableScenes().get("show_flights"),
                            new ShowFlightsController()
                    );

                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        getMenuItem(mainMenu, "settings").setOnAction(_ignored -> {
            try {
                rootController.getDisplayController().openSettingsDialog(
                        (DialogPane) SceneLoader.getDialogSceneReset("settings"),
                        new SettingsController()
                );
            } catch (UMASException e) {
                throw new RuntimeException(e);
            }
        });


        mainMenu.get(mainMenu.size() - 1).setOnAction(ignored -> System.exit(0));


    }

}
