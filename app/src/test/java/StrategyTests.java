import org.junit.Test;
import org.junit.Assert;

import java.util.List;

import studyBuddy.time_management.SessionType;
import studyBuddy.time_management.Strategy;
import studyBuddy.time_management.StrategyFactory;
import studyBuddy.time_management.StudyInterval;

public class StrategyTests {

    private static final long HOUR_MILLIS = (60 * 60 * 1000);

    @Test
    public void TestPomodoroCalls() {
        Strategy pomo = StrategyFactory.getStrategy(SessionType.POMODORO, HOUR_MILLIS);
        Assert.assertNotNull(pomo);
        List<StudyInterval> intervalList = pomo.getTimeTable();
        // ensure length is 60 mins (3600000 ms)
        long duration = 0;
        Assert.assertEquals(4, intervalList.size());
        for (StudyInterval entry : intervalList) {
            duration += (entry.end - entry.start);
        }

        Assert.assertEquals(HOUR_MILLIS, pomo.getDuration());
        Assert.assertEquals(SessionType.POMODORO, pomo.getSessionType());
    }

    @Test
    public void TestStandardCalls() {
        Strategy std = StrategyFactory.getStrategy(SessionType.STANDARD, HOUR_MILLIS);
        Assert.assertNull(std);
    }

    @Test
    public void TestFactoryMethod() {
        Assert.assertNull(StrategyFactory.getStrategy(SessionType.POMODORO, 142857));
        Assert.assertNull(StrategyFactory.getStrategy(SessionType.POMODORO, 0));
        Assert.assertNull(StrategyFactory.getStrategy(SessionType.POMODORO, 1499999));
    }
}
