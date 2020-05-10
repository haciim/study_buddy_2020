package studyBuddy;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studdybuddy.R;

import studyBuddy.timemanagement.EndSessionButtonListener;
import studyBuddy.timemanagement.TimelineView;

public class SessionActivity extends AppCompatActivity {

    private Session session;

    static private String SESSION_START_KEY = "sessionStart";
    static private String SESSION_DURATION_KEY = "sessionDuration";
    static private String SESSION_NAME_KEY = "sessionName";

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.session_layout);
        session = new Session();
        TimelineView timeline = findViewById(R.id.timeLine);
        TextView timer = findViewById(R.id.time);

        SessionTimerCallback callback = (secondsPassed, duration) -> {
            double percentage = ((double)secondsPassed / duration);
            timeline.setPercentageCompletion(Math.min(Math.max(percentage, 0.0), 1.0));
            timer.setText(Session.formatTime(Math.min(secondsPassed, duration) / 1000));
        };

        timer.setText(getResources().getText(R.string.zero_time));
        session.setTimerCallback(callback);

        if (savedInstanceBundle != null) {
            session.startSession(savedInstanceBundle.getString(SESSION_NAME_KEY),
                                 savedInstanceBundle.getLong(SESSION_DURATION_KEY),
                                 savedInstanceBundle.getLong(SESSION_START_KEY));
        } else {
            session.startSession("testname", 100000);
        }

        View button = findViewById(R.id.fob);
        View endSessionText = findViewById(R.id.endSessionText);
        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.text_animator);
        endSessionText.setTranslationX(750);

        animator.setTarget(endSessionText);

        // pass view
        // pass session

        // probably should use a looper on the ui thread to do the backwards anim
        button.setOnTouchListener(new EndSessionButtonListener(session, endSessionText, this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        session.resumeSession();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(SESSION_START_KEY, session.getStartTime().getTime());
        savedInstanceState.putLong(SESSION_DURATION_KEY, session.getExpectedTime());
        savedInstanceState.putString(SESSION_NAME_KEY, session.getName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        session.pauseSession();
    }
}
