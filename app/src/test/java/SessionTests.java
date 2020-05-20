import org.junit.Test;
import org.junit.Assert;

import studyBuddy.Session;

public class SessionTests {

    @Test
    public void TestStartSession() {
        Session testSession = new Session();
        // create two minute session
        testSession.startSession("test task", 120000);
        Assert.assertTrue(testSession.isSessionOngoing());
        Assert.assertEquals(testSession.getName(), "test task");
    }

    @Test
    public void TestPauseSession() {
        Session testSession = new Session();
        testSession.startSession("test task", 120000);
        testSession.pauseSession();
        Assert.assertTrue(testSession.isSessionOngoing());
    }
    
    @Test
    public void TestEndSession() {
        Session testSession = new Session();
        testSession.startSession("test task", 120000);
        testSession.endSession();
        Assert.assertFalse(testSession.isSessionOngoing());
        double time = testSession.endSession();
        Assert.assertEquals(time, -1.0, 0.001);
    }
}
