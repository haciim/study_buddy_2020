package studyBuddy;

import android.os.Handler;

import java.util.concurrent.atomic.AtomicLong;

public class TimerRunner implements Runnable {

    private SessionTimerCallback callback;      // callback function called per-second.
    private Handler parent;                     // used to queue per-second.
    private AtomicLong secondsPassed;

    static final long SECOND_MILLIS = 1000;
    /*
        TODO: session should be aware of MS timing
              and runner should be as well. On each call, calculate the time needed to maintain
              second intervals.
              add'l: it would probably be best to rework the session time tracking into here
              just to have it all in one place. double check with everyone else abt that
              possible soln:
              it's not necessary for the session to have acces to the number of seconds
              we can add a "setTime" method in here to get it started and then we can use
              `secondsPassed` internally so that other classes have access to that
              when setting the start time we can estimate the number of seconds left to pass
              add'l: we should probably clear all callbacks when we do that
              calculate the time offset to the next estimated second
              and then cancel all callbacks and reschedule a new one when "setTime" is called
              note that there's no pausing so we don't need to worry about it
              if there was though we could build up an offset and use that to modify calculations
              increasing the offset whenever the timer is "paused" instead of "slept"
     */

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
