import android.os.Handler;
import android.os.Looper;

import org.junit.Test;
import org.junit.Assert;
import org.mockito.Mockito;

import java.util.Date;

import studyBuddy.Session;

public class SessionTests {
    private Handler handler = Mockito.mock(Handler.class);
    private Session testSession = new Session(handler);

    @Test
    public void TestStartSession() {
        // create two minute session
        testSession.startSession("test task", 120000);
        Assert.assertTrue(testSession.isSessionOngoing());
        Assert.assertEquals(testSession.getName(), "test task");
    }

    @Test
    public void TestPauseSession() {
        testSession.startSession("test task", 120000);
        testSession.pauseSession();
        Assert.assertTrue(testSession.isSessionOngoing());
    }

    @Test
    public void TestEndSession() {
        testSession.startSession("test task", 120000);
        testSession.endSession();
        Assert.assertFalse(testSession.isSessionOngoing());
        double percentProductive = 0.9;
        testSession.setTotalProductiveTime(percentProductive);
        double totalTime = testSession.getTotalTime();
        double productiveTime = testSession.getProductiveTime();
        Assert.assertEquals(productiveTime, totalTime * percentProductive, 0.001);
        double badTime = testSession.endSession();
        Assert.assertEquals(badTime, -1.0, 0.001);
    }

    @Test
    public void TestDates() {
        long currentTime = System.currentTimeMillis();
        Date firstTime = new Date(currentTime);
        // plus one minute
        long oneMinute = 60000;
        Date secondTime = new Date(currentTime + oneMinute);
        double minutes = testSession.getMinutes(firstTime, secondTime);
        long seconds = testSession.getSeconds(firstTime, secondTime);
        Assert.assertEquals(seconds, oneMinute, 0.001);
        Assert.assertEquals(minutes, 1.0, 0.001);
    }
}
