package studyBuddy.timemanagement;

/**
 * Cleans up some of the redundant bits regarding creating a session
 */
public class StrategyFactory {
    /**
     * Creates and returns the desired strategy, given that it is valid.
     * @param type - The type of strategy we wish to create.
     * @param duration - The length of the strategy, in milliseconds.
     * @return - A new strategy constructed based on the given parameters, or null if
     *           the parameters are invalid.
     */
    public static Strategy getStrategy(SessionType type, long duration) {
        switch(type) {
            case STANDARD:
                if (StandardStrategy.isDurationValid(duration)) {
                    return new StandardStrategy(duration);
                } else {
                    return null;
                }
            case POMODORO:
                if (PomodoroStrategy.isDurationValid(duration)) {
                    return new PomodoroStrategy(duration);
                } else {
                    return null;
                }
            default:
                return null;
        }
    }
}
