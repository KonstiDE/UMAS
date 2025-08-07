package wue.eorc.umas.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import wue.eorc.umas.models.Project;
import wue.eorc.umas.utils.DirectoryUtils;

import java.io.*;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

public class ProjectCache {

    private static final Gson gson = new Gson();

    public static File cachePath;

    public static HashMap<String, Timestamp> cache = new HashMap<>();

    public static Project currentlyOpenedProject;

    public static void createRecentProjectsFile() throws IOException {
        File cacheFolder = DirectoryUtils.initCache();

        if(cacheFolder.exists()){
            File cacheFile = Paths.get(cacheFolder.getAbsolutePath(), "recent_projects").toFile();
            if(!cacheFile.exists()){
                BufferedWriter writer = new BufferedWriter(new FileWriter(cacheFile));
                writer.write("");
                writer.flush();
                writer.close();
            }else{
                BufferedReader reader = new BufferedReader(new FileReader(cacheFile));
                String json = reader.readLine();
                reader.close();

                if(json != null && !json.isEmpty()){
                    cache = gson.fromJson(json, new TypeToken<HashMap<String, Timestamp>>(){}.getType());
                }
            }
            cachePath = cacheFile;

            Iterator<Map.Entry<String, Timestamp>> it = cache.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Timestamp> item = it.next();
                if(!new File(item.getKey()).exists()){
                    it.remove();
                }
            }
            saveCache();

        }

    }


    public static void addToCache(File projectFile) throws IOException {
        if(cache.size() >= 10){
            String oldestKey = "";
            Timestamp current = Timestamp.from(Instant.now());
            for(String key : cache.keySet()){
                if(cache.get(key).before(current)){
                    oldestKey = key;
                    current = cache.get(key);
                }
            }
            if(!oldestKey.isEmpty()){
                cache.remove(oldestKey);
            }
        }

        cache.put(projectFile.getAbsolutePath(), new Timestamp(System.currentTimeMillis()));

        saveCache();
    }

    private static void saveCache() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(cachePath));
        writer.write(gson.toJson(cache));
        writer.flush();
        writer.close();
    }

}
