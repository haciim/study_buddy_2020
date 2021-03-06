package studyBuddy.session_activity;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.load.resource.gif.GifBitmapProvider;
import com.example.studdybuddy.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import studyBuddy.main_activity.MainActivity;
import studyBuddy.util.DataManager;
import studyBuddy.pet.Pet;
import studyBuddy.pet.PetAnimation;
import studyBuddy.util.GlideGifLoader;
import studyBuddy.util.PrimaryColorPicker;
import studyBuddy.time_management.EndSessionButtonListener;
import studyBuddy.time_management.SessionBroadcastReceiver;
import studyBuddy.time_management.SessionRecord;
import studyBuddy.time_management.SessionType;
import studyBuddy.time_management.Strategy;
import studyBuddy.time_management.StrategyFactory;
import studyBuddy.time_management.TimelineView;

public class SessionActivity extends AppCompatActivity {

    private Session session;
    private NotificationChannel channel;
    private ImageView petView;
    private List<SessionRecord> sessions;
    private Strategy strategy;
    private Pet pet;
    private PetAnimation petAnimation;

    static private String SESSION_START_KEY = "sessionStart";

    static public String SESSION_DURATION_KEY = "sessionDuration";

    static public String SESSION_NAME_KEY = "sessionName";

    static public String SESSION_STRATEGY_KEY = "sessionStrategy";

    static public int INTENT_ID = 142857;


    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.session_layout);

        View fob = findViewById(R.id.fob);

        session = new Session(new Handler(Looper.getMainLooper()));
        TimelineView timeline = findViewById(R.id.timeLine);
        TextView timer = findViewById(R.id.time);
      
        SessionRecord[] sessionRecords = DataManager.load(this, SessionRecord[].class);
        Intent sessionIntent = getIntent();

        this.petAnimation = (PetAnimation) sessionIntent.getSerializableExtra(MainActivity.PET_ANIMATION_KEY);
        this.pet = petAnimation.getPet();

        if(sessionRecords == null) {
            sessions = new ArrayList<>();
        } else {
            sessions = new ArrayList<>(Arrays.asList(sessionRecords));
        }

        createNotificationChannel();

        long sessionLength = getIntent().getLongExtra(SESSION_DURATION_KEY, 480000);

        if (sessionLength == 0) {
            ((ConstraintLayout)timeline.getParent()).removeView(timeline);
        }

        SessionTimerCallback callback = (secondsPassed, duration) -> {
            double percentage = ((double)secondsPassed / duration);
            if (duration != 0) {
                timeline.setPercentageCompletion(Math.min(Math.max(percentage, 0.0), 1.0));
                timer.setText(Session.formatTime(Math.min(secondsPassed, duration) / 1000));
            } else {
                timer.setText(Session.formatTime(secondsPassed / 1000));
            }
        };

        timer.setText(getResources().getText(R.string.zero_time));
        session.setTimerCallback(callback);

        if (savedInstanceBundle != null) {
            session.startSession(savedInstanceBundle.getString(SESSION_NAME_KEY),
                                 savedInstanceBundle.getLong(SESSION_DURATION_KEY),
                                 savedInstanceBundle.getLong(SESSION_START_KEY));
        } else if (sessionIntent.getBooleanExtra(SessionBroadcastReceiver.REOPEN_SESSION, false)) {
            long duration = sessionIntent.getLongExtra(SessionBroadcastReceiver.SESSION_END, System.currentTimeMillis()) - sessionIntent.getLongExtra(SessionBroadcastReceiver.SESSION_START, System.currentTimeMillis());
            if (duration == 0) {
                // lol
                ((ConstraintLayout)timeline.getParent()).removeView(timeline);
            }
            strategy = StrategyFactory.getStrategy(SessionType.POMODORO, (sessionIntent.getLongExtra(SESSION_STRATEGY_KEY, -1)));
            session.startSession(sessionIntent.getStringExtra(SESSION_NAME_KEY),
                                 duration,
                                 sessionIntent.getLongExtra(SessionBroadcastReceiver.SESSION_START, System.currentTimeMillis()));
        } else {
            strategy = StrategyFactory.getStrategy(SessionType.POMODORO, (sessionIntent.getLongExtra(SESSION_STRATEGY_KEY, -1)));

            // https://stackoverflow.com/questions/10903754/input-text-dialog-android

            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setView(input);
            builder.setTitle("Enter a name for this session");
            // new session
            builder.setPositiveButton("START SESSION", (DialogInterface dialog, int which) -> {
                String sessionName = input.getText().toString();
                if (sessionName.equals("")) {
                    sessionName = "Unnamed Session";
                }

                System.out.println(sessionName);

                session.startSession(sessionName, sessionIntent.getLongExtra(SESSION_DURATION_KEY, 480000));
            });

            builder.show();
        }

        // Setup pet animation
        this.petAnimation.maintenanceCheck(this.sessions);
        petView = findViewById(R.id.session_pet_image);
        petAnimation.setCurAnimation("studying");
        GlideGifLoader.loadGifIntoView(this, petView, petAnimation.getCurGif(this));

        View endSessionText = findViewById(R.id.endSessionText);
        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.text_animator);
        endSessionText.setTranslationX(750);

        animator.setTarget(endSessionText);
        fob.setOnTouchListener(new EndSessionButtonListener(session, endSessionText, this));

        session.setFinishedCallback(elapsedTime -> {
            setContentView(R.layout.percent_productive_view);
            TextView percentageText = findViewById(R.id.percentage);
            PiePercentageView pieView = findViewById(R.id.pie_view);
            pieView.setTextPercentageView(percentageText);
            findViewById(R.id.confirm_percentage).setOnClickListener((View v) -> {
                session.setTotalProductiveTime(pieView.getPercentage());
                setContentView(R.layout.finish_session_view);
                TextView elapsedText = findViewById(R.id.sessionTime);
                elapsedText.setText(Session.formatTime(elapsedTime));
                View doneButton = findViewById(R.id.doneButton);
                doneButton.setOnClickListener(new DoneButtonListener(this));
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        session.resumeSession();
        cancelOldIntent();
        View background = findViewById(R.id.session_base);
        background.setBackgroundColor(PrimaryColorPicker.getDayColorInt(this));

        View fob = findViewById(R.id.fob);
        Drawable buttonContents = ((ImageView)fob).getDrawable();

        buttonContents.mutate().setColorFilter(PrimaryColorPicker.getDayColorMatrixFilter(this));

        Window window = this.getWindow();

        window.setStatusBarColor(PrimaryColorPicker.getDayColorInt(this));
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
        if (strategy != null) {
            savedInstanceState.putLong(SESSION_STRATEGY_KEY, strategy.getDuration());
        } else {
            savedInstanceState.putLong(SESSION_STRATEGY_KEY, -1);
        }

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
            broadcastIntent.putExtra(SessionBroadcastReceiver.SESSION_START, session.getStartTime().getTime());
            broadcastIntent.putExtra(SESSION_NAME_KEY, session.getName());
            if (strategy != null) {
                broadcastIntent.putExtra(SESSION_STRATEGY_KEY, strategy.getDuration());
            }
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
        if(!session.isSessionOngoing()) {
                sessions.add(0, new SessionRecord(session));
                Log.d("Session", "Storing session data...");
                SessionRecord[] arr = sessions.toArray(new SessionRecord[0]);
                DataManager.save(this, arr);
        }
    }

    private void cancelOldIntent() {
        Intent broadcast = new Intent(this, SessionBroadcastReceiver.class);
        AlarmManager mgr = (AlarmManager)getSystemService(ALARM_SERVICE);
        assert mgr != null;
        // prevent notification from updating
        mgr.cancel(PendingIntent.getBroadcast(this, INTENT_ID, broadcast, PendingIntent.FLAG_UPDATE_CURRENT));
        NotificationManager notifMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        assert notifMgr != null;
        notifMgr.cancel(INTENT_ID);
    }
}
