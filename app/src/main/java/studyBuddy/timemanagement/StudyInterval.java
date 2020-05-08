package studyBuddy.timemanagement;

import java.util.Date;

/**
 * Records a single active / inactive interval.
 */
public class StudyInterval {
    public final long start;
    public final long end;
    public final boolean isActive;

    /**
     * Creates a StudyInterval between two points in time.
     * @param start -- Milliseconds between epoch and start time.
     * @param end -- Milliseconds between epoch and end time.
     * @param isActive -- whether or not the user was/is active during this interval.
     */
    public StudyInterval(long start, long end, boolean isActive) {
        this.start = start;
        this.end = end;
        this.isActive = isActive;
    }
}
