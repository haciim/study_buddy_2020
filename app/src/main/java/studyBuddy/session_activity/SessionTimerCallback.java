package studyBuddy.session_activity;

// string formatting?
public interface SessionTimerCallback {
    /**
     * Callback for this interface.
     * @param timeElapsed - The amount of time passed since start of session.
     * @param duration - The total length of the session.
     */
    void callbackFunc(long timeElapsed, long duration);
}
