// Author: Georgina Chandler

package studyBuddy;

// Could change to java.util.Date
import java.sql.Timestamp;

public class Session {
    private Timestamp startTime;
    private Timestamp endTime;
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
        startTime = new Timestamp(currentTime);
        name = sessionName;
        expectedTime = expectedSessionTime;
        sessionOngoing = true;
    }

    /**
     * Preconditions: session must be ongoing (start session must have been called)
     *
     * Ends the current session as triggered by user
     * @return the duration of this session in minutes
     */
    public double endSession() {
        long currentTime = System.currentTimeMillis();
        Timestamp endTime = new Timestamp(currentTime);
        double sessionMinutes = getMinutes(startTime, endTime);
        // there could be some loss of precision here but since our session times
        // will likely be relatively short? like max 8 hours it shouldn't be a problem
        totalTime = sessionMinutes;
        sessionOngoing = false;
        // TODO: record to data base
        // view should display this total time to user and then ask for percentProductiveTime
        // total time is in MINUTES
        return totalTime;
    }

    /**
     * Precondition: percentProductiveTime is between 0.0 and 1.0 inclusive ([0.0, 1.0])
     *
     * Uses the user given percentage to calculate the amount (in minutes) of productive time
     * @param percentProductiveTime the percentage of the session time that the user was productive
     */
    public void setTotalProductiveTime(long percentProductiveTime) {
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
    private double getMinutes(Timestamp startTime, Timestamp endTime) {
        long start = startTime.getTime();
        long end = endTime.getTime();
        long diff = end - start;
        double minutes = diff / (60.0 * 1000.0);
        return minutes;
    }
}
