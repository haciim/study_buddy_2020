// Author: Georgina Chandler

package studyBuddy;

import android.os.Handler;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class Session {
    private Date startTime;
    private Date endTime;
    private String name;
    // In minutes
    private double expectedTime;
    private double productiveTime;
    private double totalTime;
    // Expected value between 0 and 1
    private double percentProductive;
    // TODO: fill this in correctly according to implementation of Time Management module
    // private TimeManagement timeManager;

    private boolean sessionOngoing;
    private SessionTimerCallback callback;
    private TimerRunner runner;
    private Handler handler;
    /**
     * Constructs a new studyBuddy.Session
     * Parameters are initialized to somewhat meaningless values
     * Session needs to be started by user
     */
    public Session() {
        startTime = null;
        endTime = null;
        name = null;
        expectedTime = 0.0;
        productiveTime = 0.0;
        totalTime = 0.0;
        // assume they were productive the whole time
        percentProductive = 1.0;
        expectedTime = 0.0;
        // timeManager = null;
        sessionOngoing = false;

        // manage session events
        callback = null;
        runner = null;
        handler = new Handler();
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

//    TODO
//    public void setTimeManager(TimeManagement manager);

    /**
     * Starts a session, triggered by user interaction
     * Sets the session name and expected duration as given by user
     * Records the real time session start time
     * @param sessionName the name of the task for this session
     * @param expectedSessionTime the expected duration of this session /
     *                            expected time to complete task
     */
    public void startSession(String sessionName, double expectedSessionTime) {
        long currentTime = System.currentTimeMillis();
        startTime = new Date(currentTime);
        name = sessionName;
        expectedTime = expectedSessionTime;
        sessionOngoing = true;

        // if runner is null: create runner
        // regardless: pass callback
        // regardless: start runner
        if (runner == null) {
            runner = new TimerRunner(handler);
        }

        runner.setCallback(callback);
        handler.postDelayed(runner, TimerRunner.SECOND_MILLIS);
    }

    /**
     * Preconditions: session must be ongoing (start session must have been called)
     *
     * Ends the current session as triggered by user
     * @return the duration of this session in minutes
     */
    public double endSession() {
        long currentTime = System.currentTimeMillis();
        endTime = new Date(currentTime);
        double sessionMinutes = getMinutes(startTime, endTime);
        // there could be some loss of precision here but since our session times
        // will likely be relatively short? like max 8 hours it shouldn't be a problem
        totalTime = sessionMinutes;
        sessionOngoing = false;
        // view should display this total time to user and then ask for percentProductiveTime
        // total time is in MINUTES
        handler.removeCallbacks(runner);
        return totalTime;
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
    public double getExpectedTime() {
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
