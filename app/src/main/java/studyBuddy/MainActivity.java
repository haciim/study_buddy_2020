package studyBuddy;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bumptech.glide.Glide;
import com.example.studdybuddy.R;

import studyBuddy.timemanagement.PomodoroStrategy;
import studyBuddy.timemanagement.SessionBroadcastReceiver;
import studyBuddy.timemanagement.TimeSelectView;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private CardView newSession;
    private CardView sessionHistoryButton;
    private ImageView petView;
    private Pet pet;

    private boolean timeSelectorIsOpen;
    int timerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup Activity and Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        timeSelectorIsOpen = false;

        ColorMatrixColorFilter filter = PrimaryColorPicker.getDayColorMatrixFilter(this);

        ImageView sessionHistory = findViewById(R.id.session_history_inner);
        sessionHistory.getDrawable().mutate().setColorFilter(filter);

        ImageView circle = findViewById(R.id.circle_button_inner);
        circle.getDrawable().mutate().setColorFilter(filter);

        TextView info = findViewById(R.id.info_button_text);
        info.setTextColor(PrimaryColorPicker.getDayColorInt(this));

        TextView newSessionText = findViewById(R.id.newSession);
        newSessionText.setTextColor(PrimaryColorPicker.getDayColorInt(this));

        // https://stackoverflow.com/questions/22192291/how-to-change-the-status-bar-color-in-android
        Window window = this.getWindow();

        window.setStatusBarColor(PrimaryColorPicker.getDayColorInt(this));

        ImageView background = findViewById(R.id.main_background);
        background.getDrawable().mutate().setColorFilter(filter);

        // see if we need to open the activity back up
        Intent appIntent = getIntent();
        if (appIntent.getBooleanExtra(SessionBroadcastReceiver.REOPEN_SESSION, false)) {
            Intent startSession = new Intent(this, SessionActivity.class);
            // give field access to sessionactivity
            startSession.putExtra(SessionBroadcastReceiver.SESSION_START, appIntent.getLongExtra(SessionBroadcastReceiver.SESSION_START, System.currentTimeMillis()));
            startSession.putExtra(SessionBroadcastReceiver.SESSION_END, appIntent.getLongExtra(SessionBroadcastReceiver.SESSION_END, System.currentTimeMillis()));
            startSession.putExtra(SessionBroadcastReceiver.REOPEN_SESSION, true);
            // the strategy was lost in translation -- the duration is used instead.
            startSession.putExtra(SessionActivity.SESSION_STRATEGY_KEY, appIntent.getLongExtra(SessionActivity.SESSION_STRATEGY_KEY, -1));
            if (appIntent.hasExtra(SessionBroadcastReceiver.DELETE_INTENT)) {
                startSession.putExtra(SessionBroadcastReceiver.DELETE_INTENT, (PendingIntent) appIntent.getParcelableExtra(SessionBroadcastReceiver.DELETE_INTENT));
                AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                assert alarmMgr != null;
                // prevents notifications from being triggered once the app has been opened again
                alarmMgr.cancel((PendingIntent) appIntent.getParcelableExtra(SessionBroadcastReceiver.DELETE_INTENT));
            }

            startActivity(startSession);
        } else {
            Intent broadcast = new Intent(this, SessionBroadcastReceiver.class);
            AlarmManager mgr = (AlarmManager)getSystemService(ALARM_SERVICE);
            assert mgr != null;
            // prevent notification from updating
            mgr.cancel(PendingIntent.getBroadcast(this, SessionActivity.INTENT_ID, broadcast, PendingIntent.FLAG_UPDATE_CURRENT));
            NotificationManager notifMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            assert notifMgr != null;
            notifMgr.cancel(SessionActivity.INTENT_ID);
        }


        // Setup component interactions
        newSession = findViewById(R.id.new_session_outer);
        newSession.setOnClickListener(this);

        sessionHistoryButton = findViewById(R.id.session_history_outer);
        sessionHistoryButton.setOnClickListener(this);

        // Setup pet animation
        petView = findViewById(R.id.home_pet_view);
        Glide.with(this).asGif().load(R.raw.pet_idle).into(petView);

        pet = DataManager.load(this, Pet.class);
        if (pet == null) {
            Log.i("Main", "Init new pet");
            pet = new Pet();
            pet.setName("Buddy");
        }
    }

    @Override
    public void onClick(View view) {
        // create this intent
        Intent intent;
        switch (view.getId()) {
            case R.id.new_session_outer:
                if (!timeSelectorIsOpen) {
                    TimeSelectView timerView = new TimeSelectView(this);
                    timerId = View.generateViewId();
                    timerView.setId(timerId);
                    ConstraintLayout layout = findViewById(R.id.base_layer);
                    layout.addView(timerView);
                    ConstraintSet timerConstraints = new ConstraintSet();
                    timerConstraints.clone(layout);
                    timerConstraints.connect(timerView.getId(), ConstraintSet.TOP, R.id.new_session_outer, ConstraintSet.BOTTOM, 0);
                    timerConstraints.connect(timerView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                    timerConstraints.connect(timerView.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                    timerConstraints.applyTo(layout);
                    timeSelectorIsOpen = true;
                } else {
                    intent = new Intent(this, SessionActivity.class);
                    TimeSelectView selectView = findViewById(timerId);
                    Log.d("Session Length: ", String.valueOf(selectView.getDuration()));
                    intent.putExtra(SessionActivity.SESSION_DURATION_KEY, (long) (selectView.getDuration() * 60 * 1000));
                    if (selectView.getStrategy() != null) {
                        intent.putExtra(SessionActivity.SESSION_STRATEGY_KEY, (long) (selectView.getDuration() * 60 * 1000));
                    }
                    ((ConstraintLayout) findViewById(R.id.base_layer)).removeView(selectView);
                    startActivity(intent);
                    timeSelectorIsOpen = false;
                }
                break;
            case R.id.session_history_outer:
                intent = new Intent(this, SessionHistoryActivity.class);
                startActivity(intent);
                break;
        }
    }
}