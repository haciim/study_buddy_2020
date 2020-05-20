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
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(fileContents);
            writer.close();
            Log.i("JSON", "Written successfully");
        } catch (IOException e) {
            Log.i("JSON", "Write error");
            // e.printStackTrace();
        }
        //Log.i("View JSON", fileContents);

    }

    public static <D> D load(Class type) {
        //load functionality should be a main method

        String filePath = FILES_DIR + "/";
        D result;

        if (type == Pet.class) {
            filePath += PET_FILE_NAME;
            result = (Pet) result;
        } else {
            filePath += SESSION_FILE_NAME;
        }

        Gson gson = new Gson();
        try {
            result = gson.fromJson(new BufferedReader(new FileReader(filePath)), Pet.class);
        } catch (FileNotFoundException e) {
            if(type == Pet.class) {
                (Pet) result = new Pet("1234");
            }
            Log.i("File", "File not found");
        }
        if(result.getClass() == Pet.class) {
            Log.i("Default pet name", ((Pet) result).getName());
            ((Pet) result).setTrustLevel(5);
            ((Pet) result).setName("Study");
            Log.i("changed pet name", ((Pet) result).getName());
        }
        return result;
    }
}
