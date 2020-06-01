package studyBuddy;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.example.studdybuddy.R;

import studyBuddy.timemanagement.SessionBroadcastReceiver;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private CardView newSession;
    private CardView sessionHistoryButton;
    private ImageView petView;
    private Pet pet;
    @Override
    /**
     * This method called when the app is opened.
     * Any data retrieval should start here.
     */
    protected void onCreate(Bundle savedInstanceState) {
        // Setup Activity and Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        System.out.println("new activity created");

        // see if we need to open the activity back up
        Intent appIntent = getIntent();
        if (appIntent.getBooleanExtra(SessionBroadcastReceiver.REOPEN_SESSION, false)) {
            Intent startSession = new Intent(this, SessionActivity.class);
            startSession.putExtra(SessionBroadcastReceiver.SESSION_START, appIntent.getLongExtra(SessionBroadcastReceiver.SESSION_START, System.currentTimeMillis()));
            startSession.putExtra(SessionBroadcastReceiver.SESSION_END, appIntent.getLongExtra(SessionBroadcastReceiver.SESSION_END, System.currentTimeMillis()));
            startSession.putExtra(SessionBroadcastReceiver.REOPEN_SESSION, true);
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

        pet = DataManager.load(Pet.class);
        if (pet == null) {
            Log.i("Main", "Init new pet");
            pet = new Pet("Test");
            pet.setName("Buddy");
        }
    }

    @Override
    /**
     * This method is called when any clickable item
     * in the home_layout is pressed.
     * Each item results in a different action taken.
     * @param view The view that called this method
     */
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.new_session_outer:
                intent = new Intent(this, SessionActivity.class);
                startActivity(intent);
                break;
            case R.id.session_history_outer:
                intent = new Intent(this, SessionHistoryActivity.class);
                startActivity(intent);
                break;
        }
    }
}