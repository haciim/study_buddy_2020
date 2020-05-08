import android.os.Handler;
import android.os.Looper;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.atomic.AtomicInteger;

import studyBuddy.SessionTimerCallback;
import studyBuddy.TimerRunner;

public class TimerRunnerTests {

    private static long currentTime;
    private static SessionTimerCallback callback = new SessionTimerCallback() {
        public void callbackFunc(long secondsPassed) {
            System.out.println("called");
            System.out.println(secondsPassed + " seconds");
            // figure out which second should be called
            // gauge offset based on that (like keep it under 100 ms off i guess)
            long diff = (System.currentTimeMillis() - currentTime);
            long seconds = Math.round(diff / 1000.0);
            Assert.assertEquals(secondsPassed, seconds);
            // figure out offset
            long offset = Math.abs(diff - (secondsPassed * 1000));
            // stay roughly synchronized
            System.out.println("offset (ms): " + offset);
            Assert.assertTrue(offset < 100);

        }
    };

    private static final int MAX_CALLBACKS = 5;

    @Test
    public void TestTimerRunner() throws InterruptedException {
        Handler handler = Mockito.mock(Handler.class);
        TimerRunner runner = new TimerRunner(handler);
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
