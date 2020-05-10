package studyBuddy.timemanagement;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import com.example.studdybuddy.R;

import java.util.concurrent.atomic.AtomicBoolean;

import studyBuddy.Session;

public class EndSessionButtonListener implements View.OnTouchListener {

    private Handler handler;
    private Session session;
    private Runnable runnable;
    private ObjectAnimator animator;

    private AtomicBoolean isOpen;
    private long lastPress;

    private View target;

    private static final long SECOND_TOUCH_CUTOFF = 5000;

    public EndSessionButtonListener(Session session, View target, Context ctx) {
        handler = new Handler(Looper.getMainLooper());
        this.target = target;
        this.isOpen = new AtomicBoolean(false);
        this.session = session;
        this.lastPress = 0;
        animator = (ObjectAnimator) AnimatorInflater.loadAnimator(ctx, R.animator.text_animator);
        animator.setStartDelay(0);
        animator.setTarget(target);
        runnable = () -> {
            animator.reverse();
            isOpen.set(false);
        };

        // add notifications post-beta
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        v.performClick();
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (isOpen.get() && (System.currentTimeMillis() - lastPress) < SECOND_TOUCH_CUTOFF) {
                handler.removeCallbacks(runnable);
                session.endSession();
            } else {
                // edge case: tap after session ends
                isOpen.set(true);
                animator.start();
                lastPress = System.currentTimeMillis();
                handler.postDelayed(runnable, SECOND_TOUCH_CUTOFF);
            }

            return true;
        }

        return false;
    }
}
