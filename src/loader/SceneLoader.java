package loader;

import enums.ErrorType;
import exception.UMASException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class SceneLoader {
    private final Class<?> clazz;

    private final String scenebasePath;

    public SceneLoader(Class<?> mainClass, String sceneBasePath){
        this.clazz = mainClass;
        this.scenebasePath = sceneBasePath;
    }

    public Pane loadSceneFromFXML(String fileName) throws UMASException {
        URL url = clazz.getResource(scenebasePath + fileName);
        try{
            return FXMLLoader.load(Objects.requireNonNull(url));
        } catch(IOException ioe){
            throw new UMASException(
                    ErrorType.INTERNAL,
                    "Could not find the scene \"" + fileName + "\" under the basepath of \"" + scenebasePath + "\""
            );
        }
    }

}
