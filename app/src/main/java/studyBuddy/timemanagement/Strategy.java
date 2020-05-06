package studyBuddy.timemanagement;

import java.util.List;

public interface Strategy {

    /**
     * Returns the type of a given strategy.
     * @return the relevant SessionType.
     */
    public SessionType getSessionType();

    /**
     * Based on the currently active strategy, as well as its duration, returns a series
     * of active / inactive study intervals which constitute a single session.
     *
     * @return An array of StudyIntervals starting from 0ms.
     */
    public List<StudyInterval> getTimeTable();

    /**
     * Returns true if the session is valid with the given duration, false otherwise.
     * @param duration - the length of the session, in milliseconds.
     * @return - true if the session is valid, false otherwise.
     */
    static boolean isDurationValid(long duration) {
        return false;
    }

    /**
     * Returns the duration of the strategy, in milliseconds.
     */
    public long getDuration();
}
