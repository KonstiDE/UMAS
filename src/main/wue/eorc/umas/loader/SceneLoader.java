package wue.eorc.umas.loader;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.exception.UMASException;

import java.io.IOException;
import java.net.MalformedURLException;
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

    public static HashMap<String, URL> availableScenes = new HashMap<>();

    public SceneLoader(ClassLoader classLoader) throws UMASException, IOException, URISyntaxException {
        this.classLoader = classLoader;

        initScenePreLoading();
    }

    public Pane getScene(String key) {
        try{
            return loadSceneFromFXML(key);
        } catch (IOException | UMASException e){
            return null;
        }
    }

    void initScenePreLoading() throws IOException, UMASException, URISyntaxException {
        URL url = Objects.requireNonNull(this.classLoader.getResource("scenes")).toURI().toURL();

        if(!url.toString().isEmpty()){
            try(Stream<Path> lines = Files.walk(Paths.get(url.toURI()))) {
                lines.forEach(f -> {
                    if(f.getFileName().toString().endsWith(".fxml")) {
                        try {
                            availableScenes.put(f.toFile().getName().replace(".fxml", ""), f.toFile().toURI().toURL());
                        } catch (MalformedURLException e) {
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

    public Pane loadSceneFromFXML(String key) throws UMASException, IOException {

        if (getAvailableScenes().containsKey(key)) {
            return FXMLLoader.load(getAvailableScenes().get(key));
        }else{
            throw new UMASException(
                    ErrorType.INTERNAL,
                    "Could not find the scene \"" + key + "\".fxml within the preloaded urls."
            );
        }
    }

    public static HashMap<String, URL> getAvailableScenes() {
        return availableScenes;
    }

}
