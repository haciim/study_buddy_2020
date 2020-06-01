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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.studdybuddy.R;

import java.util.ArrayList;
import java.util.List;

import studyBuddy.timemanagement.EndSessionButtonListener;
import studyBuddy.timemanagement.SessionBroadcastReceiver;
import studyBuddy.timemanagement.TimelineView;

public class SessionActivity extends AppCompatActivity {

    private Session session;
    private NotificationChannel channel;
    private ImageView pet;
    private List<Session> sessions;

    static private String SESSION_START_KEY = "sessionStart";

    static public String SESSION_DURATION_KEY = "sessionDuration";

    static private String SESSION_NAME_KEY = "sessionName";

    static public int INTENT_ID = 142857;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        Log.e("hello", "moto");
        // note: i'm pretty sure this will restore
        setContentView(R.layout.session_layout);
        session = new Session(new Handler(Looper.getMainLooper()));
        TimelineView timeline = findViewById(R.id.timeLine);
        TextView timer = findViewById(R.id.time);
        sessions = DataManager.load(getApplicationContext(), ArrayList.class);
        Intent sessionIntent = getIntent();

        if(sessions == null) {
            sessions = new ArrayList<>();
        }

        createNotificationChannel();

        SessionTimerCallback callback = (secondsPassed, duration) -> {
            double percentage = ((double)secondsPassed / duration);
            timeline.setPercentageCompletion(Math.min(Math.max(percentage, 0.0), 1.0));
            timer.setText(Session.formatTime(Math.min(secondsPassed, duration) / 1000));
        };

        timer.setText(getResources().getText(R.string.zero_time));
        session.setTimerCallback(callback);

        // if the savedinstancebundle is there: read that (it probably wont be)
        if (savedInstanceBundle != null) {
            session.startSession(savedInstanceBundle.getString(SESSION_NAME_KEY),
                                 savedInstanceBundle.getLong(SESSION_DURATION_KEY),
                                 savedInstanceBundle.getLong(SESSION_START_KEY));
        } else if (sessionIntent.getBooleanExtra(SessionBroadcastReceiver.REOPEN_SESSION, false)) {
            long duration = sessionIntent.getLongExtra(SessionBroadcastReceiver.SESSION_END, System.currentTimeMillis()) - sessionIntent.getLongExtra(SessionBroadcastReceiver.SESSION_START, System.currentTimeMillis());
            session.startSession(sessionIntent.getStringExtra(SESSION_NAME_KEY),
                                 duration,
                                 sessionIntent.getLongExtra(SessionBroadcastReceiver.SESSION_START, System.currentTimeMillis()));
        } else {
            session.startSession("testname", getIntent().getLongExtra(SESSION_DURATION_KEY, 480000));
        }

        // Setup pet animation
        pet = findViewById(R.id.session_pet_image);
        Glide.with(this).asGif().load(R.raw.pet_idle).into(pet);

        View button = findViewById(R.id.fob);
        View endSessionText = findViewById(R.id.endSessionText);
        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.text_animator);
        endSessionText.setTranslationX(750);

        animator.setTarget(endSessionText);
        button.setOnTouchListener(new EndSessionButtonListener(session, endSessionText, this));

        final PendingIntent deleteIntent = getIntent().getParcelableExtra(SessionBroadcastReceiver.DELETE_INTENT);

        SessionCompleteCallback completeCallback = (elapsedTime) -> {
            setContentView(R.layout.finish_session_view);
            TextView elapsedText = findViewById(R.id.sessionTime);
            elapsedText.setText(Session.formatTime(elapsedTime));
            View doneButton = findViewById(R.id.doneButton);
            doneButton.setOnClickListener(new DoneButtonListener(this));
//            if (deleteIntent != null) {
//                AlarmManager mgr = (AlarmManager)getSystemService(ALARM_SERVICE);
//                assert mgr != null;
//                mgr.cancel(deleteIntent);
//            }
        };

        session.setFinishedCallback(completeCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        session.resumeSession();
        Intent broadcast = new Intent(this, SessionBroadcastReceiver.class);
        AlarmManager mgr = (AlarmManager)getSystemService(ALARM_SERVICE);
        assert mgr != null;
        // prevent notification from updating
        mgr.cancel(PendingIntent.getBroadcast(this, SessionActivity.INTENT_ID, broadcast, PendingIntent.FLAG_UPDATE_CURRENT));
        NotificationManager notifMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        assert notifMgr != null;
        notifMgr.cancel(SessionActivity.INTENT_ID);
        // clear notification
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
            Log.e("Expected time", String.valueOf(session.getExpectedTime()));
            broadcastIntent.putExtra(SessionBroadcastReceiver.SESSION_END, session.getExpectedTime() + session.getStartTime().getTime());
            broadcastIntent.putExtra(SessionBroadcastReceiver.SESSION_START, session.getStartTime().getTime());
            PendingIntent pendingBroadcast = PendingIntent.getBroadcast(this, INTENT_ID, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarm_mgr = (AlarmManager)getSystemService(ALARM_SERVICE);
            if (alarm_mgr == null) throw new AssertionError();
            alarm_mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingBroadcast);
        }
    }

    /**
     * Creates as notification channel if the build version allows it.
     */
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
        if(!session.isSessionOngoing()){
            session.clean();
            sessions.add(session);
            DataManager.save(getApplicationContext(), sessions);
        }
    }
}
