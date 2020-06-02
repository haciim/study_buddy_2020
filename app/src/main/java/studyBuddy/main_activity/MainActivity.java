package studyBuddy.main_activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
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

import studyBuddy.util.DataManager;
import studyBuddy.pet.Pet;
import studyBuddy.pet.PetAnimation;
import studyBuddy.util.PrimaryColorPicker;
import studyBuddy.session_activity.SessionActivity;
import studyBuddy.session_history_activity.SessionHistoryActivity;
import studyBuddy.pet_activity.PetActivity;
import studyBuddy.time_management.SessionBroadcastReceiver;
import studyBuddy.time_management.TimeSelectView;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private CardView newSession;
    private CardView sessionHistoryButton;
    private ImageView petView;
    private Pet pet;
    private PetAnimation petAnimation;

    private boolean timeSelectorIsOpen;
    int timerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup Activity and Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        
        timeSelectorIsOpen = false;

        PrimaryColorPicker.setBackgroundFilter(this, findViewById(R.id.session_history_inner));

        TextView newSessionText = findViewById(R.id.newSession);
        newSessionText.setTextColor(PrimaryColorPicker.getDayColorInt(this));

        // https://stackoverflow.com/questions/22192291/how-to-change-the-status-bar-color-in-android
        Window window = this.getWindow();

        window.setStatusBarColor(PrimaryColorPicker.getDayColorInt(this));

        PrimaryColorPicker.setBackgroundFilter(this, findViewById(R.id.main_background));

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

        // Load pet
        this.pet = DataManager.load(this, Pet.class);
        if (this.pet == null) {
            Log.i("Main", "Init new pet");
            this.pet = new Pet();
            this.pet.setName("Buddy");
        }
        this.petAnimation = new PetAnimation(this.pet);

        // Setup pet animation
        petView = findViewById(R.id.home_pet_view);
        petView.setOnClickListener(this);
        Glide.with(this).asGif().load(this.petAnimation.getCurGif()).into(petView);
    }

    @SuppressLint("ClickableViewAccessibility")
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
                    timerView.setClearListener((View v) -> {
                        ((ConstraintLayout)findViewById(R.id.base_layer)).removeView(timerView);
                        timeSelectorIsOpen = false;
                    });
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
            case R.id.home_pet_view:
                intent = new Intent(this, PetActivity.class);
                intent.putExtra("pet", this.pet);
                intent.putExtra("petAnimator", this.petAnimation);
                startActivity(intent);
                break;
        }
    }
}