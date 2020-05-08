package studyBuddy;

import android.os.Handler;

import java.util.concurrent.atomic.AtomicLong;

public class TimerRunner implements Runnable {

    private SessionTimerCallback callback;      // callback function called per-second.
    private Handler parent;                     // used to queue per-second.
    private AtomicLong secondsPassed;

    static final long SECOND_MILLIS = 1000;

    public TimerRunner(Handler parent) {
        this.parent = parent;
    }

    /**
     * Sets the callback for this runnable.
     * @param callback - the new callback for the runnable. Replaces the old callback if already set.
     */
    public synchronized void setCallback(SessionTimerCallback callback) {
        this.callback = callback;
    }

    public void run() {
        synchronized (this) {
            if (callback != null) {
                callback.callbackFunc(secondsPassed.get());
            }
        }

        parent.postDelayed(this, SECOND_MILLIS);
    }
}
