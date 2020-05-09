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
    private double expectedTime;
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
        expectedTime = 0.0;
        productiveTime = 0.0;
        totalTime = 0.0;
        // assume they were productive the whole time
        percentProductive = 1.0;
        expectedTime = 0.0;
        // timeManager = null;
        sessionOngoing = false;
        sessionPaused = false;

        // manage session events
        callback = null;

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
        // weird thing: inconsistent double/long units
        long currentTime = System.currentTimeMillis();
        startTime = new Date(currentTime);
        name = sessionName;
        expectedTime = expectedSessionTime;
        sessionOngoing = true;

        // if runner is null: create runner
        // regardless: pass callback
        // regardless: start runner

        runner.setCallback(callback);
        runner.setStartTime(startTime.getTime());
        runner.setDuration((long)expectedSessionTime);
        // the runner and the session are now synchronized
        handler.postDelayed(runner, TimerRunner.SECOND_MILLIS);
    }

    /**
     * Adjusts the start time of this session. Used to restore a session's state after
     * onDestroy() is called.
     * @param startTime - ms since epoch where this session began.
     */
    public void setStartTime(long startTime) {
        this.startTime = new Date(startTime);
        // todo: fix constructor so that we dont need to deal with this
        synchronized (runner) {
            runner.setStartTime(startTime);
        }
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

    // TODO: some conditions to handle with session:
    //       - user leaves the app (pauseSession + resumeSession)
    //       - app is destroyed (saveInstanceState + loadInstanceState calls, allow user to recover state)
    //          - this one is a todo until we can start making activities
    //       - user terminates the session prematurely (endsession should handle this)
    //          - another activity one -- onBackButton or some other calls

    // TODO: handle the endSession case.
    //       create a broadcastReceiver which gets pinged when our session ends
    //       additionally, provide sufficient information so that if we open the app after a session has completed,
    //       nothing weird happens

    //       if the user exits the app / restarts their phone, all is lost unfortunately
    //       but we should be able to store some data

    // implementation
    //      - pauseSession: stop the runnable but keep tracking time
    //      - the way it was done on the branch was to just save the initialTime, then add a method
    //        to the session which lets us update the current time
    //        the session would then be created onCreate and we could update its time in the onRestore call if it was available
    //      - resumeSession: update the runner's offset, figure out how much time until our next callback, then recreate the callback with that delay

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
        handler.post(runner);
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

        // ensure that we do not cancel a callback
        // callback is scheduled in synchro'd  run function
        synchronized (runner) {
            handler.removeCallbacks(runner);
        }

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
