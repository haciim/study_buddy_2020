import android.os.Handler;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.atomic.AtomicInteger;

import studyBuddy.session_activity.SessionTimerCallback;
import studyBuddy.session_activity.TimerRunner;

public class TimerRunnerTests {

    private static long currentTime;
    private static SessionTimerCallback callback = new SessionTimerCallback() {
        public void callbackFunc(long secondsPassed, long secondsDuration) {
            System.out.println("called");
            System.out.println(secondsPassed + " ms");
            // figure out which second should be called
            // gauge offset based on that (like keep it under 100 ms off i guess)
            long diff = (System.currentTimeMillis() - currentTime);
            long seconds = Math.round(diff / 1000.0);
            Assert.assertEquals((secondsPassed / 1000), seconds);
            // figure out offset
            long offset = Math.abs(diff % 1000);
            // stay roughly synchronized
            System.out.println("offset (ms): " + offset);
            Assert.assertTrue(offset < 100);

        }
    };

    private static final int MAX_CALLBACKS = 5;

    // ensure the timer runner is called on correct intervals
    @Test
    public void TestTimerRunner() {
        Handler handler = Mockito.mock(Handler.class);
        TimerRunner runner = new TimerRunner(handler);
        runner.setDuration(100000);
        final AtomicInteger runs = new AtomicInteger(0);
        Mockito.when(handler.postDelayed(Mockito.eq(runner), Mockito.anyLong())).then(new Answer<Void>() {
            public Void answer(InvocationOnMock invoc) throws InterruptedException {
                // fake postDelayed with callback cutoff
                // in implementation this cutoff will be handled by cancelling the callback via the handler
                if (runs.incrementAndGet() < MAX_CALLBACKS) {
                    Thread.sleep((long) invoc.getArguments()[1]);
                    runner.run();
                }
                return null;
            }
        });

        currentTime = System.currentTimeMillis();
        runner.setCallback(callback);
        runner.setStartTime(currentTime);

        // in code: use handler.post instead
        // this just ensures that our function runs only as long as the runner is active
        runner.run();
    }
}
