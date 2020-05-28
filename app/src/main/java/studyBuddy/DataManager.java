package studyBuddy;

import android.content.Context;
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

    /**
     * Saves the given object to FILES_DIR in JSON format. The name of the stored file is
     * "[simple name of the object's class].json"
     *
     * @param context context so the method knows where to save the file
     * @param data the data to be saved
     */
    public static <D> void save(Context context, D data) {
        String filename = data.getClass().getSimpleName() + ".json";

        String fileContents = new Gson().toJson(data);
        File file = new File(context.getFilesDir(), filename);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(fileContents);
            writer.close();
            Log.i("Save", "Written successfully, saved as: " + filename);
        } catch (IOException e) {
            Log.i("Save", "Write error");
        }
    }

    /**
     * returns data of the given type stored on the device.
     *
     * @param context context so the method knows where to save the file
     * @param type the type of the data to retrieve
     * @return returns any stored data of the given type, null if
     *          no data of the given type is found in storage
     */
    public static <D> D load(Context context, Class type) {
        String filePath = context.getFilesDir() + "/" + type.getSimpleName() + ".json";
        Gson gson = new Gson();
        D result;
        Log.i("Load", "Loading from: " + filePath);
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
