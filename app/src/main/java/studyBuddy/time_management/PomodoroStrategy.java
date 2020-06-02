package studyBuddy.time_management;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PomodoroStrategy implements Strategy {

    private static final long HALF_HOUR_MILLIS = 1800000;
    private static final long TWENTY_FIVE_MILLIS = 1500000;
    // this is the only strategy we really need to worry about tbh

    // restores from parcel
    public static final Parcelable.Creator<PomodoroStrategy> CREATOR = new Parcelable.Creator<PomodoroStrategy>() {
        @Override
        public PomodoroStrategy createFromParcel(Parcel source) {
            return new PomodoroStrategy(source.readLong());
        }

        @Override
        public PomodoroStrategy[] newArray(int size) {
            return new PomodoroStrategy[size];
        }
    };

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

    /**
     * Returns true if the length of this duration is valid for the strategy, false otherwise.
     * @param duration - Length of the desired session.
     * @return - True if valid, false otherwise.
     */
    static boolean isDurationValid(long duration) {
        return (duration % HALF_HOUR_MILLIS == 0 && duration > 0);
    }

    public PomodoroStrategy(long duration) {
        this.duration = duration;
        this.intervals = new LinkedList<>();
        int intervalCount = (int)(duration / HALF_HOUR_MILLIS);

        for (int i = 0; i < intervalCount; i++) {
            intervals.add(new StudyInterval((i * HALF_HOUR_MILLIS), (i * HALF_HOUR_MILLIS) + TWENTY_FIVE_MILLIS, true));
            intervals.add(new StudyInterval((i * HALF_HOUR_MILLIS) + TWENTY_FIVE_MILLIS, (i + 1) * HALF_HOUR_MILLIS, false));
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(duration);
    }
}
