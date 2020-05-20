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

    public static <D> D load() {
        //load functionality should be a main method

        String petPath = FILES_DIR + "/";

        if (D instanceof Pet) {

        }

        Gson gson = new Gson();
        Pet result;
        try {
            result = gson.fromJson(new BufferedReader(new FileReader(petPath)), Pet.class);
        } catch (FileNotFoundException e) {
            result = new Pet("1234");
            Log.i("File", "File not found");
        }
        Log.i("Default pet name", result.getName());
        result.setTrustLevel(5);
        result.setName("Study");
        Log.i("changed pet name", result.getName());
        return result;
    }
}
