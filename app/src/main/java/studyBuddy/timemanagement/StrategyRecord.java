package studyBuddy.timemanagement;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * Stores all information necessary to represent a session at some point in time
 */
public class StrategyRecord {
    public final SessionType type;
    public final String name;
    public final Date start;
    public final Date end;
    public final double percentProductive;

    public StrategyRecord(Strategy strategy, String name,
                          long start, long duration,
                          double percentProductive) {
        this.type = strategy.getSessionType();
        this.name = name;
        this.start = new Date(start);
        this.end = new Date(start + duration);
        this.percentProductive = percentProductive;
    }

    @NonNull
    public String toString() {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder.create();
        return gson.toJson(this);
    }

    public static StrategyRecord fromString(String input) {
        Gson gson = new Gson();
        return gson.fromJson(input, StrategyRecord.class);
    }
}
