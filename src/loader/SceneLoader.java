package loader;

import enums.ErrorType;
import exception.UMASException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SceneLoader {
    private final Class<?> clazz;
    private final String sceneBasePath;

    public static HashMap<String, Pane> availableScenes = new HashMap<>();

    public SceneLoader(Class<?> mainClass, String path) throws UMASException {
        this.clazz = mainClass;
        this.sceneBasePath = path;

        initSubScenePreLoading();
    }

    public Pane loadSceneFromFXML(String fileName) throws UMASException {
        URL url = clazz.getResource(sceneBasePath + fileName);
        try {
            return FXMLLoader.load(Objects.requireNonNull(url));
        } catch (IOException ioe) {
            throw new UMASException(
                    ErrorType.INTERNAL,
                    "Could not find the scene \"" + fileName + "\" under the basepath of \"" + sceneBasePath + "\""
            );
        }
    }

    void initSubScenePreLoading() throws UMASException {
        URL url = clazz.getResource(sceneBasePath + "sub");

        if(url != null){
            for(File file : Objects.requireNonNull(new File(url.getPath()).listFiles())){
                availableScenes.put(
                        file.getName().replace(".fxml", ""),
                        loadSceneFromFXML("sub/" + file.getName())
                );
            }
        }else{
            throw new UMASException(ErrorType.INTERNAL, "Could not preload scenes. Incorrect path!");
        }
    }

}
