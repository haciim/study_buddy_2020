package studyBuddy.timemanagement;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PomodoroStrategy implements Strategy {

    private static final long HALF_HOUR_MILLIS = 1800000;
    private static final long TWENTY_FIVE_MILLIS = 1500000;

    private final long duration;
    private final List<StudyInterval> intervals;

    public SessionType getSessionType() {
        return SessionType.POMODORO;
    }

    public List<StudyInterval> getTimeTable() {
        return Collections.unmodifiableList(intervals);
    }

    public long getDuration() {
        return duration;
    }

    static boolean isDurationValid(long duration) {
        return (duration % HALF_HOUR_MILLIS == 0 && duration > 0);
    }

    PomodoroStrategy(long duration) {
        this.duration = duration;
        this.intervals = new LinkedList<>();
        int intervalCount = (int)(duration / HALF_HOUR_MILLIS);

        for (int i = 0; i < intervalCount; i++) {
            intervals.add(new StudyInterval((i * HALF_HOUR_MILLIS), (i * HALF_HOUR_MILLIS) + TWENTY_FIVE_MILLIS, true));
            intervals.add(new StudyInterval((i * HALF_HOUR_MILLIS) + TWENTY_FIVE_MILLIS, (i + 1) * HALF_HOUR_MILLIS, false));
        }
    }


}
