package studyBuddy;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DataManager {
    private static final String PET_PATH = "Pet.json";
    private static final String SESSION_PATH = "Sessions.json";
    private static final String FILES_DIR = "/data/data/com.example.studdybuddy/files";

    public static void save(Pet pet) {
        String filename = "Pet.json";
        String fileContents = new Gson().toJson(pet);
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

    public static Pet load() {
        //load functionality should be a main method

        String petPath = FILES_DIR + "/" + PET_PATH;
        String sessionsPath = "Sessions.json";
        //BufferedReader bufferedReader = new BufferedReader(new FileReader(petPath));

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
