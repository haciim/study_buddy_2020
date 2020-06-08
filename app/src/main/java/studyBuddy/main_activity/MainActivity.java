package studyBuddy.main_activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.studdybuddy.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import studyBuddy.time_management.SessionRecord;
import studyBuddy.util.DataManager;
import studyBuddy.pet.Pet;
import studyBuddy.pet.PetAnimation;
import studyBuddy.util.GlideGifLoader;
import studyBuddy.util.PrimaryColorPicker;
import studyBuddy.session_activity.SessionActivity;
import studyBuddy.session_history_activity.SessionHistoryActivity;
import studyBuddy.pet_activity.PetActivity;
import studyBuddy.time_management.SessionBroadcastReceiver;
import studyBuddy.time_management.TimeSelectView;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final String PET_ANIMATION_KEY = "pet_animation";
    public static final String USER_MANUAL = "https://github.com/haciim/study_buddy_2020/blob/master/USER_MANUAL.md";

    private CardView newSession;
    private ImageButton sessionHistoryButton;
    private ImageButton infoButton;
    private ImageView petView;
    private PetAnimation petAnimation;
    private Pet pet;
    private TextView newSessionText;
    private List<SessionRecord> recordList;

    private boolean timeSelectorIsOpen;
    int timerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup Activity and Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        
        timeSelectorIsOpen = false;
        this.newSessionText = findViewById(R.id.new_session_text);
        // Load pet
        this.petAnimation = DataManager.load(this, PetAnimation.class);
        if (this.petAnimation == null) {
            Log.i("Main", "Init new pet");
            this.pet = new Pet();
            this.pet.setName("Buddy");
            this.petAnimation = new PetAnimation(this.pet);
        } else {
            this.pet = this.petAnimation.getPet();
        }

        // see if we need to open the activity back up
        Intent appIntent = getIntent();
        if (appIntent.getBooleanExtra(SessionBroadcastReceiver.REOPEN_SESSION, false)) {
            Intent startSession = new Intent(this, SessionActivity.class);
            // give field access to sessionactivity
            startSession.putExtra(SessionBroadcastReceiver.SESSION_START, appIntent.getLongExtra(SessionBroadcastReceiver.SESSION_START, System.currentTimeMillis()));
            startSession.putExtra(SessionBroadcastReceiver.SESSION_END, appIntent.getLongExtra(SessionBroadcastReceiver.SESSION_END, System.currentTimeMillis()));
            startSession.putExtra(SessionBroadcastReceiver.REOPEN_SESSION, true);
            startSession.putExtra(SessionActivity.SESSION_NAME_KEY, appIntent.getStringExtra(SessionActivity.SESSION_NAME_KEY));
            // the strategy was lost in translation -- the duration is used instead.
            startSession.putExtra(SessionActivity.SESSION_STRATEGY_KEY, appIntent.getLongExtra(SessionActivity.SESSION_STRATEGY_KEY, -1));
            if (appIntent.hasExtra(SessionBroadcastReceiver.DELETE_INTENT)) {
                startSession.putExtra(SessionBroadcastReceiver.DELETE_INTENT, (PendingIntent) appIntent.getParcelableExtra(SessionBroadcastReceiver.DELETE_INTENT));
                AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                assert alarmMgr != null;
                // prevents notifications from being triggered once the app has been opened again
                alarmMgr.cancel((PendingIntent) appIntent.getParcelableExtra(SessionBroadcastReceiver.DELETE_INTENT));
            }
            startSession.putExtra(PET_ANIMATION_KEY, this.petAnimation);
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

        sessionHistoryButton = findViewById(R.id.session_history_button);
        sessionHistoryButton.setOnClickListener(this);

        infoButton = findViewById(R.id.home_info_button);
        infoButton.setOnClickListener(this);

        // Setup pet animation
        petView = findViewById(R.id.home_pet_view);
        petView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load Session Records
        SessionRecord[] records = DataManager.load(this, SessionRecord[].class);
        if (records != null) {
            this.recordList = Arrays.asList(records);
        } else {
            this.recordList = new ArrayList<>();
        }

        newSessionText.setTextColor(PrimaryColorPicker.getDayColorInt(this));
        PrimaryColorPicker.setBackgroundFilter(this, this.sessionHistoryButton);


        // https://stackoverflow.com/questions/22192291/how-to-change-the-status-bar-color-in-android
        Window window = this.getWindow();
        window.setStatusBarColor(PrimaryColorPicker.getDayColorInt(this));

        PrimaryColorPicker.setBackgroundFilter(this, findViewById(R.id.main_background));
        PrimaryColorPicker.setBackgroundFilter(this, infoButton);
        petAnimation.maintenanceCheck(this.recordList);
        GlideGifLoader.loadGifIntoView(this, petView, petAnimation.getCurGif(this));
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Retrieve pet info from calling activity
        this.petAnimation = (PetAnimation) intent.getSerializableExtra(PET_ANIMATION_KEY);
        if (this.petAnimation == null) {
            Log.i("MainActivity", "New intent without PetAnimation");
            this.finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        DataManager.save(this, this.petAnimation);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onClick(View view) {
        // create this intent
        Intent intent;
        switch (view.getId()) {
            case R.id.new_session_outer:
                // Start new session
                // Allow user to set duration and strategy
                if (!timeSelectorIsOpen) {
                    TimeSelectView timerView = new TimeSelectView(this);
                    timerId = View.generateViewId();
                    timerView.setId(timerId);
                    timerView.setClearListener((View v) -> {
                        ((ConstraintLayout)findViewById(R.id.base_layer)).removeView(timerView);
                        timeSelectorIsOpen = false;
                        newSessionText.setText(R.string.new_session);
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
                    this.newSessionText.setText(R.string.start_session);
                } else {
                    intent = new Intent(this, SessionActivity.class);
                    TimeSelectView selectView = findViewById(timerId);
                    Log.d("Session Length: ", String.valueOf(selectView.getDuration()));
                    intent.putExtra(SessionActivity.SESSION_DURATION_KEY, (long) (selectView.getDuration() * 60 * 1000));
                    intent.putExtra(PET_ANIMATION_KEY, this.petAnimation);
                    if (selectView.getStrategy() != null) {
                        intent.putExtra(SessionActivity.SESSION_STRATEGY_KEY, (long) (selectView.getDuration() * 60 * 1000));
                    }
                    ((ConstraintLayout) findViewById(R.id.base_layer)).removeView(selectView);
                    startActivity(intent);
                    timeSelectorIsOpen = false;
                    newSessionText.setText(R.string.new_session);
                }
                break;
            case R.id.session_history_button:
                // Start Session History Activity
                intent = new Intent(this, SessionHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.home_pet_view:
                // Start Pet Activity
                intent = new Intent(this, PetActivity.class);
                intent.putExtra(PET_ANIMATION_KEY, this.petAnimation);
                startActivity(intent);
                break;
            case R.id.home_info_button:
                // Send user to Github User Manual
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(USER_MANUAL));
                startActivity(intent);
                break;
        }
    }
}