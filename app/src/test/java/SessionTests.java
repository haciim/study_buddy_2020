import android.os.Handler;
import android.os.Looper;

import org.junit.Test;
import org.junit.Assert;
import org.mockito.Mockito;

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
        double time = testSession.endSession();
        Assert.assertEquals(time, -1.0, 0.001);
    }
}
