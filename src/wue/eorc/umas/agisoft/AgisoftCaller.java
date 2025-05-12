package wue.eorc.umas.agisoft;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import wue.eorc.umas.controller.listeners.AgisoftCallbackListener;
import wue.eorc.umas.controller.listeners.AgisoftQueueListener;
import wue.eorc.umas.enums.AgisoftTask;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.Settings;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class AgisoftCaller {

    public static Queue<Runnable> queue = new LinkedList<>();
    public static boolean isRunning = false;

    private final String snippetsPath =
            Paths.get("src", "wue", "eorc", "umas", "agisoft", "snippets").toFile().getAbsolutePath();

    public AgisoftQueueListener agisoftQueueListener;
    public AgisoftCallbackListener agisoftCallbackListener;

    public AgisoftCaller(AgisoftQueueListener agisoftQueueListener, AgisoftCallbackListener agisoftCallbackListener) {
        this.agisoftQueueListener = agisoftQueueListener;
        this.agisoftCallbackListener = agisoftCallbackListener;
    }

    public boolean createProject(String psxFilePath) throws IOException, InterruptedException {
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "create_project.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFilePath);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        boolean success = Boolean.parseBoolean(watchForSignal("vn: ", p.getInputStream(), null));

        int exitCode = p.waitFor();

        return exitCode == 0 && success;
    }

    public String checkAgisoftVersion(String path) throws InterruptedException {
        Path pythonPath = Paths.get(path);
        Path filePath = Paths.get(snippetsPath, "version_number.py");

        try{
            ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath());
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String versionNumber = watchForSignal("vn: ", p.getInputStream(), null);

            int exitCode = p.waitFor();

            return exitCode == 0 ? versionNumber : null;
        }catch (IOException e){
            return null;
        }
    }

    public void addPhotosCheck(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "add_photos_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.ADD_PHOTOS_CHECK, stackPane, pb, true);
    }

    public void addPhotos(StackPane stackPane, String psxFile, List<String> folders){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "add_photos.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath(),
                "-psxFile", psxFile, "-photo_folder", folders.size() > 1 ? String.join(",", folders) : folders.get(0));

        enqueue(AgisoftTask.ADD_PHOTOS, stackPane, pb, false);
    }

    public void setBrightnessCheck(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "set_brightness_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.SET_BRIGHTNESS_CHECK, stackPane, pb, true);
    }

    public void setBrightness(StackPane stackPane, String psxFile, int brightness, int contrast){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "set_brightness.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-brightness", String.valueOf(brightness), "-contrast", String.valueOf(contrast));

        enqueue(AgisoftTask.SET_BRIGHTNESS, stackPane, pb, false);
    }

    public void alignPhotosCheck(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "align_photos_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.ALIGN_IMAGES_CHECK, stackPane, pb, true);
    }

    public void alignPhotos(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "align_photos.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.ALIGN_IMAGES, stackPane, pb, false);
    }

    private void enqueue(AgisoftTask task, StackPane stackPane, ProcessBuilder pb, boolean nextIfFailed){
        queue.add(() -> {
            CompletableFuture.supplyAsync(() -> {
                try{
                   pb.redirectErrorStream(true);
                   Process p = pb.start();

                   boolean success = Boolean.parseBoolean(watchForSignal("vn: ", p.getInputStream(), agisoftCallbackListener));

                   int exitCode = p.waitFor();

                   return exitCode == 0 && success;
                }catch (IOException | InterruptedException e){
                   return false;
                }
            }) .thenAcceptAsync(result -> {
                if(result) {
                    isRunning = true;
                    processNext();
                }else{
                    if(nextIfFailed) {
                        isRunning = true;
                        processNext();
                    }else{
                        isRunning = false;
                    }
                }
                try {
                    agisoftCallbackListener.callback(stackPane, task, result);
                } catch (UMASException e) {
                    throw new RuntimeException(e);
                }

            });
        });

        if (!isRunning) {
            isRunning = true;
            processNext();
        }
    }

    // !!!Signal key must be 4 chars long!!!
    public static String watchForSignal(String signalKey, InputStream inputStream, AgisoftCallbackListener listener) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if(listener != null && line.contains("vp: ")) {
                    String finalLine = line;
                    Platform.runLater(() -> {
                        try{
                            listener.progress(Float.parseFloat(finalLine.substring(4)));
                        } catch (NumberFormatException ignored) {  }
                    });
                }
                if(line.startsWith(signalKey)){
                    if(listener != null) Platform.runLater(() -> listener.progress(0));
                    return line.substring(4);
                }
            }
        }
        return null;
    }

    private static synchronized void processNext() {
        Runnable nextTask = queue.poll();
        if (nextTask != null) {
            nextTask.run();
        } else {
            isRunning = false;
        }
    }

}
