package wue.eorc.umas.agisoft;

import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.loader.Settings;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AgisoftCaller {

    public String metashapePath;
    public String executableScriptsFolder;

    public static Queue<CompletableFuture<Boolean>> queue = new ConcurrentLinkedQueue<>();

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

    public static boolean addPhotos(String psxFile, List<String> folders) {
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "add_photos.py");

        try{
            ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath(),
                    "-psxFile", psxFile, "-photo_folder", folders.size() > 1 ? String.join(",", folders) : folders.get(0));

            pb.redirectErrorStream(true);
            Process p = pb.start();

            boolean success = Boolean.parseBoolean(watchForSignal("vn: ", p.getInputStream()));

            int exitCode = p.waitFor();

            return exitCode == 0 && success;
        }catch (IOException | InterruptedException e){
            return false;
        }
    }

    public static boolean addPhotosCheck(String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "add_photos_check.py");

        try{
            ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                    filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

            pb.redirectErrorStream(true);
            Process p = pb.start();

            boolean success = Boolean.parseBoolean(watchForSignal("vn: ", p.getInputStream()));

            int exitCode = p.waitFor();

            return exitCode == 0 && success;
        }catch (IOException | InterruptedException e){
            return false;
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


}
