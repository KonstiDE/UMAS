package wue.eorc.umas.agisoft;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AgisoftCaller {

    public final String metashapePath;
    public final String executableScriptsFolder;

    public static Queue<CompletableFuture<Boolean>> queue = new ConcurrentLinkedQueue<>();

    public AgisoftCaller(String metashape, String executableScripts) {
        this.metashapePath = metashapePath;
        this.executableScriptsFolder = executableScriptsFolder;
    }

    public static boolean createProject(){
        Path rootPath = FileSystems.getDefault().getPath("").toAbsolutePath();
        Path pythonPath = Paths.get("/opt/metashape-pro/metashape");
        Path filePath = Paths.get(rootPath.toString(),"src", "wue", "eorc", "umas", "agisoft", "test.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath());
        pb.redirectErrorStream(true);
        Process p = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Python] " + line);
            }
        }
        int exitCode = p.waitFor();

        System.out.println(exitCode);
    }

}
