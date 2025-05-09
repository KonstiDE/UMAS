package wue.eorc.umas.agisoft;

import javafx.scene.layout.StackPane;
import wue.eorc.umas.controller.listeners.AgisoftCallbackListener;
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

    private static boolean isRunning = false;

    private static final String snippetsPath =
            Paths.get("src", "wue", "eorc", "umas", "agisoft", "snippets").toFile().getAbsolutePath();

    public static boolean createProject(String psxFilePath) throws IOException, InterruptedException {
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "create_project.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFilePath);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        boolean success = Boolean.parseBoolean(watchForSignal("vn: ", p.getInputStream()));

        int exitCode = p.waitFor();

        return exitCode == 0 && success;
    }

    public static String checkAgisoftVersion(String path) throws InterruptedException {
        Path pythonPath = Paths.get(path);
        Path filePath = Paths.get(snippetsPath, "version_number.py");

        try{
            ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath());
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String versionNumber = watchForSignal("vn: ", p.getInputStream());

            int exitCode = p.waitFor();

            return exitCode == 0 ? versionNumber : null;
        }catch (IOException e){
            return null;
        }
    }

    public static void addPhotos(StackPane stackPane, String psxFile, List<String> folders, AgisoftCallbackListener listener){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "add_photos.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath(),
                "-psxFile", psxFile, "-photo_folder", folders.size() > 1 ? String.join(",", folders) : folders.get(0));

        enqueue(AgisoftTask.ADD_PHOTOS, stackPane, pb, false, listener);
    }

    public static void addPhotosCheck(StackPane stackPane, String psxFile, AgisoftCallbackListener listener){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "add_photos_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.ADD_PHOTOS_CHECK, stackPane, pb, true, listener);
    }

    private static void enqueue(AgisoftTask task, StackPane stackPane, ProcessBuilder pb, boolean nextIfFailed, AgisoftCallbackListener listener){
        queue.add(() -> {
            CompletableFuture.supplyAsync(() -> {
                try{
                   pb.redirectErrorStream(true);
                   Process p = pb.start();

                   boolean success = Boolean.parseBoolean(watchForSignal("vn: ", p.getInputStream()));

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
                    if(nextIfFailed) processNext();
                }
                try {
                    listener.callback(stackPane, task, result);
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
    public static String watchForSignal(String signalKey, InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if(line.startsWith(signalKey)){
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
