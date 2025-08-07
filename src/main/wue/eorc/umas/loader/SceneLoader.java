package wue.eorc.umas.loader;

import wue.eorc.umas.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.exception.UMASException;

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
    private final ClassLoader classLoader;

    public static HashMap<String, Pane> availableScenes = new HashMap<>();

    public SceneLoader(ClassLoader classLoader) throws UMASException, IOException, URISyntaxException {
        this.classLoader = classLoader;

        initScenePreLoading();
    }

    public Pane getScene(String path) throws UMASException {
        try {
            return availableScenes.get("main");
        } catch (Exception ioe) {
            throw new UMASException(
                    ErrorType.INTERNAL,
                    "Could not find the scene \"" + path + "\""
            );
        }
    }

    void initScenePreLoading() throws IOException, UMASException, URISyntaxException {
        URL url = Objects.requireNonNull(this.classLoader.getResource("scenes")).toURI().toURL();

        if(!url.toString().isEmpty()){
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
            throw new UMASException(ErrorType.INTERNAL, "Could not preload scenes! Incorrect path!");
        }
    }

    public Pane loadSceneFromFXML(Path path) throws UMASException {
        try {
            return FXMLLoader.load(path.toUri().toURL());
        } catch (IOException ioe) {
            throw new UMASException(
                    ErrorType.INTERNAL,
                    "Could not find the scene \"" + path + "\""
            );
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
