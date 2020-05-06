package studyBuddy.timemanagement;

/**
 * Cleans up some of the redundant bits regarding creating a session
 */
public class StrategyFactory {
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
