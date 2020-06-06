package studyBuddy.time_management;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import studyBuddy.session_activity.Session;

/**
 * Represents a session at some point in time.
 */
public class SessionRecord {
    public final SessionType type;
    public final String name;
    public final Date start;
    public final Date end;
    public final double percentProductive;

    /**
     * Constructs a new SessionRecord from a session.
     * @param session - The session we are storing.
     */
    public SessionRecord(Session session) {
        this.type = SessionType.NONE;
        this.name = session.getName();
        this.start = session.getStartTime();
        this.end = session.getEndTime();
        this.percentProductive = session.getPercentProductive();
    }

    /**
     * Return a string representation of this SessionRecord as a JSON object.
     * @return - JSON representation.
     */
    @NonNull
    public String toString() {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder.create();
        return gson.toJson(this);
    }

    /**
     * Creates a new SessionRecord from a stored string record.
     * @param input - A JSON representation of the SessionRecord.
     * @return - The SessionRecord as a Java object.
     */
    public static SessionRecord fromString(String input) {
        Gson gson = new Gson();
        return gson.fromJson(input, SessionRecord.class);
    }

    public int getDurationMins() {
        return this.end.getMinutes() - this.start.getMinutes();
    }
}
