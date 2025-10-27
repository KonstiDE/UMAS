package wue.eorc.umas.loader;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.utils.system.DirectoryUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    public static final String darkMode = "styles/dark-mode.css";
    public static final String darkModeDialog = "styles/dark-mode-dialog.css";

    private static final Gson gson = new Gson();

    public static File settingsPath;

    public static HashMap<Setting, String> settings = new HashMap<>(Map.ofEntries(
            Map.entry(Setting.UI_THEME, "Light"),
            Map.entry(Setting.FULL_SCREEN_AT_START_UP, "false"),
            Map.entry(Setting.AGISOFT_EXEC_PATH, ""),
            Map.entry(Setting.AGISOFT_VERSION, ""),
            Map.entry(Setting.AGISOFT_EXEC_PATH_VALID, "false"),
            Map.entry(Setting.TERRA_EXEC_PATH, ""),
            Map.entry(Setting.TERRA_VERSION, ""),
            Map.entry(Setting.TERRA_EXEC_PATH_VALID, "false"),
            Map.entry(Setting.DATABASE_CONNECTION_STRING, ""),
            Map.entry(Setting.DATABASE_CONNECTION_STRING_VALID, "false")
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
                    try{
                        settings = gson.fromJson(json, new TypeToken<HashMap<Setting, String>>() {}.getType());
                    } catch (JsonSyntaxException e) {
                        // Settings file is malformed due to an update of the software
                        boolean success = settingsFile.delete();
                        if(success){
                            createSettingsFile();
                        }else{
                            throw new RuntimeException("Settings file is malformed and cannot be deleted. " +
                                    "Please delete it manually. Path: " + settingsFile.getAbsolutePath());
                        }
                    }
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

    public static boolean useDarkLayout(){
        return settings.get(Setting.UI_THEME).equals("Dark");
    }

}
