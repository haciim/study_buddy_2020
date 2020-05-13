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

// TODO: change onTouchListener to onClickListener
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
        // runs the listener (UI)
        handler = new Handler(Looper.getMainLooper());
        // animator assigned to this view
        this.target = target;
        // ensures no thread conflicts
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
    }

    /**
     * When the button is pressed:
     *  - On first press, opens up the "end session" window
     *  - If a second press occurs within SECOND_TOUCH_CUTOFF ms, ends the session
     *
     * @param v - The view touched.
     * @param e - The motion event dispatched.
     * @return true if the event is consumed.
     */
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        v.performClick();
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (isOpen.get() && (System.currentTimeMillis() - lastPress) < SECOND_TOUCH_CUTOFF) {
                handler.removeCallbacks(runnable);
                session.endSession();
            } else {
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
