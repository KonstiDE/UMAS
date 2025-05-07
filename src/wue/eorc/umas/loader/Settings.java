package wue.eorc.umas.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.utils.DirectoryUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    private static final Gson gson = new Gson();

    public static File settingsPath;

    public static HashMap<Setting, String> settings = new HashMap<>(Map.ofEntries(
            Map.entry(Setting.UITHEME, "Light"),
            Map.entry(Setting.FULLSCREENATSTARTUP, "false"),
            Map.entry(Setting.AGISOFTEXECPATH, ""),
            Map.entry(Setting.TERRAEXECPATH, "")
    ));

    public static void createSettingsFile() throws IOException {
        File cacheFolder = DirectoryUtils.initCache();

        if(cacheFolder.exists()) {
            File settingsFile = Paths.get(cacheFolder.getAbsolutePath(), "settings").toFile();
            if (!settingsFile.exists()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile));
                writer.write(gson.toJson(settings));
                writer.flush();
                writer.close();
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(settingsFile));
                String json = reader.readLine();
                reader.close();

                if (json != null && !json.isEmpty()) {
                    settings = gson.fromJson(json, new TypeToken<HashMap<Setting, String>>() {
                    }.getType());
                }
            }
            settingsPath = settingsFile;

            saveSettings();
        }
    }

    public static void saveSettings() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(settingsPath));
        writer.write(gson.toJson(settings));
        writer.flush();
        writer.close();
    }

    public static String getSetting(Setting setting){
        return settings.get(setting);
    }

    public static void modifySettings(Setting key, String value){
        settings.put(key, value);
    }

}
