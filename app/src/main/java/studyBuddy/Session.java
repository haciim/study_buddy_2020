// Author: Georgina Chandler

package studyBuddy;

import android.os.Handler;
import android.os.Looper;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class Session {
    private Date startTime;
    private Date endTime;
    private String name;
    // In minutes
    private long expectedTime;
    private double productiveTime;
    private double totalTime;
    // Expected value between 0 and 1
    private double percentProductive;
    // TODO: fill this in correctly according to implementation of Time Management module
    // private TimeManagement timeManager;

    // handler vs thread: if this is created on the UI thread, the task runs on there
    private boolean sessionOngoing;
    private boolean sessionPaused;
    private SessionTimerCallback callback;
    private SessionCompleteCallback completeCallback;
    private Handler handler;

    private final TimerRunner runner;


    /**
     * Constructs a new studyBuddy.Session
     * Parameters are initialized to somewhat meaningless values
     * Session needs to be started by user
     */
    public Session() {
        startTime = null;
        endTime = null;
        name = null;
        expectedTime = 0;
        productiveTime = 0.0;
        totalTime = 0.0;
        percentProductive = 1.0;
        sessionOngoing = false;
        sessionPaused = false;

        // manage session events
        callback = null;
        completeCallback = null;

        // runs on UI thread (intended for view updates)
        handler = new Handler(Looper.getMainLooper());
        runner = new TimerRunner(handler);
    }

    /**
     * Sets the callback on this session, which will be called once per second while the session
     * is running. At most one callback can be set on a single session at a time.
     * @param callback -- the function which will be called.
     */
    public synchronized void setTimerCallback(SessionTimerCallback callback) {
        this.callback = callback;
        if (runner != null) {
            runner.setCallback(callback);
        }
    }

    public synchronized void setFinishedCallback(SessionCompleteCallback callback) {
        completeCallback = callback;
        if (runner != null) {
            runner.setFinishedCallback(callback);
        }
    }

//    TODO
//    public void setTimeManager(TimeManagement manager);

    /**
     * Starts a session, triggered by user interaction
     * Sets the session name and expected duration as given by user
     * Records the real time session start time
     * @param sessionName the name of the task for this session
     * @param expectedSessionTime the expected duration of this session /
     *                            expected time to complete task
     * @param startTime the time at which this session started, if we are spinning up
     *                  a session which was destroyed
     */
    public void startSession(String sessionName, long expectedSessionTime, long startTime) {
        // weird thing: inconsistent double/long units
        if (!sessionOngoing) {
            this.startTime = new Date(startTime);
            endTime = new Date(startTime + expectedSessionTime);
            name = sessionName;
            expectedTime = expectedSessionTime;
            sessionOngoing = true;

            // if runner is null: create runner
            // regardless: pass callback
            // regardless: start runner

            runner.setCallback(callback);
            runner.setStartTime(this.startTime.getTime());
            runner.setDuration(expectedSessionTime);
            // the runner and the session are now synchronized
            handler.post(runner);
        }
    }

    public void startSession(String sessionName, long expectedSessionTime) {
        startSession(sessionName, expectedSessionTime, System.currentTimeMillis());
    }

    /**
     * String formatting method
     * Seemed pretty convenient at the time so i put it here :)
     * @param seconds - Number of seconds
     * @return - String representing `seconds` in HH:MM:SS format
     */
    public static String formatTime(long seconds) {
        return String.format(Locale.US, "%02d:%02d:%02d",
                TimeUnit.SECONDS.toHours(seconds),
                TimeUnit.SECONDS.toMinutes(seconds) % 60,
                TimeUnit.SECONDS.toSeconds(seconds) % 60);
    }

    /**
     * If session is running and unpaused, pause it by removing the callback. Otherwise, do nothing.
     */
    public void pauseSession() {
        if (!sessionPaused && sessionOngoing) {
            sessionPaused = true;
            synchronized (runner) {
                handler.removeCallbacks(runner);
            }
        }
    }

    /**
     * Resumes a session which has been paused, but not yet stopped.
     */
    public void resumeSession() {
        // call the runnable right away (get updated data onscreen instantly)
        // from there the runner will figure out when to call itself again
        if (sessionPaused) {
            handler.post(runner);
            sessionPaused = false;
        }

    }

    /**
     * Preconditions: session must be ongoing (start session must have been called)
     *
     * Ends the current session as triggered by user
     * @return the duration of this session in minutes
     */
    public double endSession() {
        if (sessionOngoing) {
            long currentTime = System.currentTimeMillis();
            endTime = new Date(currentTime);
            // there could be some loss of precision here but since our session times
            // will likely be relatively short? like max 8 hours it shouldn't be a problem
            totalTime = getMinutes(startTime, endTime);
            sessionOngoing = false;

            synchronized (runner) {
                handler.removeCallbacks(runner);
            }

            if (completeCallback != null) {
                completeCallback.callbackFunc();
            }

            return totalTime;
        } else {
            return -1;
        }
    }

    public boolean isSessionOngoing() {
        return sessionOngoing;
    }

    /**
     * Precondition: percentProductiveTime is between 0.0 and 1.0 inclusive ([0.0, 1.0])
     *
     * Uses the user given percentage to calculate the amount (in minutes) of productive time
     * @param percentProductiveTime the percentage of the session time that the user was productive
     */
    public void setTotalProductiveTime(double percentProductiveTime) {
        // assert precondition is met
        assert (percentProductiveTime >= 0.0 && percentProductiveTime <= 1.0);
        // we might use this later
        percentProductive = percentProductiveTime;
        productiveTime = (totalTime * percentProductive);
    }

    /**
     * Precondition: session is not ongoing
     *
     * Gets the productive time of this session as calculated with the percentage productive time
     * given by user
     * @return the total productive time
     */
    public double getProductiveTime() {
        // assert precondition is met
        assert (!sessionOngoing);
        // this could be used by pet?
        return productiveTime;
    }


    /**
     * Gets the name of the session - usually a task name provided by user
     * @return the name of this session
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the session real time start time
     * @return the session start time as a date or null if the session has not been started
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Precondition: session is not ongoing
     *
     * @return the total time of this session (in minutes)
     */
    public double getTotalTime() {
        return totalTime;
    }

    /**
     * Returns the expected session time as indicated by the user on session start
     * @return expected time in minutes
     */
    public long getExpectedTime() {
        return expectedTime;
    }

    /**
     * Precondition: session is not ongoing
     *
     * @return the percent productive time for this session
     */
    public double getPercentProductive() {
        // assert precondition is met
        assert (!sessionOngoing);
        // this could also be used by pet
        return percentProductive;
    }


    /**
     * Precondition: endTime is later than startTime
     *
     * Private helper method
     * Takes two timestamps and gets the number of minutes between them
     * @param startTime the earlier time
     * @param endTime the later time
     * @return the number of minutes between the two times
     */
    private double getMinutes(Date startTime, Date endTime) {
        long start = startTime.getTime();
        long end = endTime.getTime();
        long diff = end - start;
        double minutes = diff / (60.0 * 1000.0);
        return minutes;
    }
}
