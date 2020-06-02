package studyBuddy.session_activity;

public interface SessionCompleteCallback {
    /**
     * Called once the session completes.
     * @param elapsedTime - The total length of the session.
     */
    void callbackFunc(long elapsedTime);
}
