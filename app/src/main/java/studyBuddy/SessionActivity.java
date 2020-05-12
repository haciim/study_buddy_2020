package studyBuddy;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studdybuddy.R;

import studyBuddy.timemanagement.EndSessionButtonListener;
import studyBuddy.timemanagement.SessionBroadcastReceiver;
import studyBuddy.timemanagement.TimelineView;

public class SessionActivity extends AppCompatActivity {

    private Session session;
    private NotificationChannel channel;

    static private String SESSION_START_KEY = "sessionStart";
    static private String SESSION_DURATION_KEY = "sessionDuration";
    static private String SESSION_NAME_KEY = "sessionName";

    static public int INTENT_ID = 142857;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.session_layout);
        session = new Session();
        TimelineView timeline = findViewById(R.id.timeLine);
        TextView timer = findViewById(R.id.time);

        createNotificationChannel();

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
            // get these values from intent
            session.startSession("testname", 480000);
        }

        View button = findViewById(R.id.fob);
        View endSessionText = findViewById(R.id.endSessionText);
        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.text_animator);
        endSessionText.setTranslationX(750);

        animator.setTarget(endSessionText);
        button.setOnTouchListener(new EndSessionButtonListener(session, endSessionText, this));

        // todo: perform some sort of animation once our session is ended via the finishcallback

        SessionCompleteCallback completeCallback = (elapsedTime) -> {
            setContentView(R.layout.finish_session_view);
            TextView elapsedText = findViewById(R.id.sessionTime);
            elapsedText.setText(Session.formatTime(elapsedTime));
            View doneButton = findViewById(R.id.doneButton);
            doneButton.setOnClickListener(new DoneButtonListener(this));
        };

        session.setFinishedCallback(completeCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        session.resumeSession();
        // clear notification
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // check for an intent
        if (intent.hasExtra(SessionBroadcastReceiver.DELETE_INTENT)) {
            AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
            assert alarmMgr != null;
            // prevents notifications from being triggered
            alarmMgr.cancel((PendingIntent)intent.getParcelableExtra(SessionBroadcastReceiver.DELETE_INTENT));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(SESSION_START_KEY, session.getStartTime().getTime());
        savedInstanceState.putLong(SESSION_DURATION_KEY, session.getExpectedTime());
        savedInstanceState.putString(SESSION_NAME_KEY, session.getName());
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (session.isSessionOngoing()) {
            // if the user leaves the activity, only display a notif if the session is still running
            session.pauseSession();
            Intent broadcastIntent = new Intent(this, SessionBroadcastReceiver.class);
            broadcastIntent.putExtra(SessionBroadcastReceiver.NOTIFICATION_ID, INTENT_ID);
            broadcastIntent.putExtra(SessionBroadcastReceiver.SESSION_END, session.getExpectedTime() + session.getStartTime().getTime());
            PendingIntent pendingBroadcast = PendingIntent.getBroadcast(this, INTENT_ID, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarm_mgr = (AlarmManager)getSystemService(ALARM_SERVICE);
            if (alarm_mgr == null) throw new AssertionError();
            alarm_mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingBroadcast);
        }
    }

    private void createNotificationChannel() {
        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (mgr == null) throw new AssertionError();
        if (Build.VERSION.SDK_INT >= 26) {
            channel = new NotificationChannel(SessionBroadcastReceiver.NOTIFICATION_CHANNEL,
                                              SessionBroadcastReceiver.NOTIFICATION_CHANNEL,
                                              NotificationManager.IMPORTANCE_DEFAULT);
            mgr.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
