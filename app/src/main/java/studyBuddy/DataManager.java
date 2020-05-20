package studyBuddy;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public class DataManager {
    private static final String PET_FILE_NAME = "Pet.json";
    private static final String SESSION_FILE_NAME = "Sessions.json";
    private static final String FILES_DIR = "/data/data/com.example.studdybuddy/files";

    public static <D> void save(D data) {
        String filename;

        if (data instanceof Pet) {
            filename = PET_FILE_NAME;
        } else {
            filename = SESSION_FILE_NAME;
        }

        String fileContents = new Gson().toJson(data);
        File file = new File(FILES_DIR, filename);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(fileContents);
            writer.close();
            Log.i("Save", "Written successfully");
        } catch (IOException e) {
            Log.i("Save", "Write error");
            // e.printStackTrace();
        }
    }

    public static <D> D load(Class type) {
        String filePath = FILES_DIR + "/";
        Gson gson = new Gson();
        D result;

        if (type == Pet.class) {
            filePath += PET_FILE_NAME;
        } else {
            filePath += SESSION_FILE_NAME;
        }

        try {
            result = gson.fromJson(new BufferedReader(new FileReader(filePath)), (Type) type);
            Log.i("Load", "Successful");
        } catch (FileNotFoundException e) {
            Log.i("Load", "File not found");
            return  null;
        }

        return result;
    }
}
