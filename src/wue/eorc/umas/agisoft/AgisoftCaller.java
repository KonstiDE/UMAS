package wue.eorc.umas.agisoft;

import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.loader.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AgisoftCaller {

    public String metashapePath;
    public String executableScriptsFolder;

    public static Queue<CompletableFuture<Boolean>> queue = new ConcurrentLinkedQueue<>();

    private static final String snippetsPath =
            Paths.get("src", "wue", "eorc", "umas", "agisoft", "snippets").toFile().getAbsolutePath();

    public static boolean createProject(String path, String psxName) throws IOException, InterruptedException {
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "create_project.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-path", path, "-psxname", psxName);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        boolean success = false;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.startsWith("vn: ")){
                    success = Boolean.parseBoolean(line.substring(4));
                }
            }
        }
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

            String versionNumber = null;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.startsWith("vn: ")){
                        versionNumber = line.substring(4);
                    }
                }
            }
            int exitCode = p.waitFor();

            return exitCode == 0 ? versionNumber : null;
        }catch (IOException e){
            return null;
        }
    }



}
