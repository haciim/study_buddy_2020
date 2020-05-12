package studyBuddy;

import android.os.Handler;
import android.util.Log;

import java.util.concurrent.atomic.AtomicLong;

public class TimerRunner implements Runnable {

    private SessionTimerCallback callback;      // callback function called per-second.
    private SessionCompleteCallback finishCall; // callback once session is done
    private Handler parent;                     // used to queue per-second.
    private long startTime;                     // used to synchronize timer
    private long duration;                      // length in MS

    static final long SECOND_MILLIS = 1000;

    public TimerRunner(Handler parent) {
        this.parent = parent;
        startTime = 0;
    }

    /**
     * Sets the callback for this runnable.
     * @param callback - the new callback for the runnable. Replaces the old callback if already set.
     */
    public synchronized void setCallback(SessionTimerCallback callback) {
        this.callback = callback;
    }

    public synchronized void setFinishedCallback(SessionCompleteCallback callback) {
        this.finishCall = callback;
    }

    /**
     * Sets the start time for this runnable.
     * @param startTime - ms since epoch, used to synchronize timing calls.
     */
    public synchronized void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public synchronized void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Runnable function -- called once per second
     */
    public synchronized void run() {
            if (startTime == -1) {
                // first run, not set
                this.startTime = System.currentTimeMillis();
            }
            // increment internal timer
            // pass to callback method
            if (callback != null) {
                callback.callbackFunc((System.currentTimeMillis() - startTime), duration);
            }

        long currentTime = System.currentTimeMillis() - startTime;
        // also works if we somehow skip a second, or if we fall short a second :)
        long estimatedTime = (long)(1000 + Math.floor((currentTime) / 1000.0) * 1000);

        Log.d("TimerRunner", "est: " + estimatedTime);
        Log.d("TimerRunner", "cur: " + currentTime);

        if (currentTime < duration && duration > 0) {
            parent.postDelayed(this, Math.max(estimatedTime - currentTime, 0));
        } else if (finishCall != null) {
            finishCall.callbackFunc((currentTime - startTime) / 1000);
            // call finished callback
        }
    }
}

// callback behavior
//  - display some "session finished" text
