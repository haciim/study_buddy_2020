package studyBuddy.timemanagement;

import java.util.LinkedList;
import java.util.List;

public class StandardStrategy implements Strategy {

    private final long duration;
    private StudyInterval interval;

    public SessionType getSessionType() {
        return SessionType.STANDARD;
    }

    public List<StudyInterval> getTimeTable() {
        // immutable so its ok
        List<StudyInterval> result = new LinkedList<StudyInterval>();
        result.add(this.interval);
        return result;
    }

    static boolean isDurationValid(long duration) {
        return (duration > 0);
    }

    public long getDuration() {
        return duration;
    }

    StandardStrategy(long duration) {
        this.duration = duration;
        this.interval = new StudyInterval(0, duration, true);
    }
}
