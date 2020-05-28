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

/**
 * The DataManager class allows for the saving and loading of data used
 * within the study buddy app
 */

public class DataManager {
    private static final String FILES_DIR = "/data/data/com.example.studdybuddy/files";

    /**
     * Saves the given object to FILES_DIR in JSON format. The name of the stored file depends
     * on the given object's type: If the object is a pet, then it is saved as
     * PET_FILE_NAME, otherwise it is saved as SESSION_FILE_NAME.
     *
     * @param data the data to be saved
     */
    public static <D> void save(D data) {
        String filename = data.getClass().getSimpleName();

        String fileContents = new Gson().toJson(data);
        File file = new File(FILES_DIR, filename);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(fileContents);
            writer.close();
            Log.i("Save", "Written successfully");
        } catch (IOException e) {
            Log.i("Save", "Write error");
        }
    }

    /**
     * returns data of the given type stored on the device.
     *
     * @param type the type of the data to retrieve
     * @return returns any stored data of the given type, null if
     *          no data of the given type is found in storage
     */
    public static <D> D load(Class type) {
        String filePath = FILES_DIR + "/" + type.getSimpleName();
        Gson gson = new Gson();
        D result;
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
