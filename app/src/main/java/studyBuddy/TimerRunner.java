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

    // TODO:
    //  - rig up some broadcast receivers
    //      - display a notification if the user is in the middle of a session
    //      - if they dismiss it, the session ends -- there's no guarantee the app will be rebooted and pick it up
    //        so we need to be able to handle this case independently
    //          - Observer pattern, singleton with observer objects that get pinged
    //      - if they tap it, or resume the app, the session continues.
    //          - PendingIntent -- we have no desire to destroy the app so we can use onSave/onRestore to hold onto that info if we lose it,
    //            and otherwise depend on the session to hold its state
    //      - the notification updates to reflect the session -- it should probably have some reference to the session itself
    //          - broadcast receiver triggers itself once every minute to update the notification text
    //          - once done: read out that the session is done! :)
    //  - ensure that only the activity or the notification attempts to save -- perhaps the session will have to do that on its own
    //    and that would be best

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
            finishCall.callbackFunc();
            // call finished callback
        }
    }
}
