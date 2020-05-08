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
            // figure out which second should be called
            // gauge offset based on that (like keep it under 100 ms off i guess)
            long diff = (System.currentTimeMillis() - currentTime);
            long seconds = Math.round(diff / 1000.0);
            Assert.assertEquals(secondsPassed, seconds);
            // figure out offset
            long offset = Math.abs(diff - (secondsPassed * 1000));
            // stay roughly synchronized
            System.out.println(offset);
            Assert.assertTrue(offset < 100);

        }
    };

    @Test
    public void TestTimerRunner() throws InterruptedException {
        currentTime = System.currentTimeMillis();
        Handler handler = Mockito.mock(Handler.class);
        TimerRunner runner = new TimerRunner(handler);
        runner.setCallback(callback);
        runner.setStartTime(currentTime);
        final AtomicInteger runs = new AtomicInteger(0);
        Mockito.when(handler.postDelayed(Mockito.eq(runner), Mockito.anyLong())).then(new Answer<Void>() {
            public Void answer(InvocationOnMock invoc) throws InterruptedException {
                // fake postDelayed
                if (runs.incrementAndGet() < 3) {
                    Thread.sleep((long)invoc.getArguments()[1]);
                    runner.run();
                }
                return null;
            }
        });

        runner.run();
    }
}
