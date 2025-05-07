package wue.eorc.umas.loader;

import wue.eorc.umas.Main;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.exception.UMASException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

public class SceneLoader {
    private final Class<?> clazz;
    private final String sceneBasePath;

    public static HashMap<String, Pane> availableScenes = new HashMap<>();

    public SceneLoader(Class<?> mainClass, String path) throws UMASException, IOException {
        this.clazz = mainClass;
        this.sceneBasePath = path;

        initSubScenePreLoading();
    }

    public Pane loadSceneFromFXML(Path path) throws UMASException {
        try {
            return FXMLLoader.load(Objects.requireNonNull(path.toUri().toURL()));
        } catch (IOException ioe) {
            throw new UMASException(
                    ErrorType.INTERNAL,
                    "Could not find the scene \"" + path + "\" under the basepath of \"" + sceneBasePath + "\""
            );
        }
    }

    public Pane loadSceneFromFXML(String path) throws UMASException {
        URL url = clazz.getResource(sceneBasePath + path);
        try {
            return FXMLLoader.load(Objects.requireNonNull(url));
        } catch (IOException ioe) {
            throw new UMASException(
                    ErrorType.INTERNAL,
                    "Could not find the scene \"" + path + "\" under the basepath of \"" + sceneBasePath + "\""
            );
        }
    }

    void initSubScenePreLoading() throws IOException, UMASException {
        URL url = clazz.getResource(sceneBasePath + "panes");

        if(url != null){
            try(Stream<Path> lines = Files.walk(Paths.get(url.toURI()))) {
                lines.forEach(f -> {
                    if(f.getFileName().toString().endsWith(".fxml")) {
                        try {
                            availableScenes.put(f.toFile().getName().replace(".fxml", ""), loadSceneFromFXML(f));
                        } catch (UMASException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }else{
            throw new UMASException(ErrorType.INTERNAL, "Could not preload wue.eorc.umas.scenes. Incorrect path!");
        }
    }

    public static HashMap<String, Pane> getAvailableScenes() {
        return availableScenes;
    }

    public static Pane getDialogSceneReset(String key) throws UMASException {
        Class<?> clazz = Main.class;
        URL url = clazz.getResource( "scenes/dialogs/" + key + ".fxml");
        try {
            return FXMLLoader.load(Objects.requireNonNull(url));
        } catch (IOException e) {
            UMASException.throwWindow(ErrorType.INTERNAL, "Failed to load FXML for scene: " + key);
        }
        return null;
    }

}
