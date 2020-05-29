package studyBuddy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
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
    private ImageView pet;
    @Override
    /**
     * This method called when the app is opened.
     * Any data retrieval should start here.
     */
    protected void onCreate(Bundle savedInstanceState) {
        // Setup Activity and Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        // see if we need to open the activity back up
        Intent appIntent = getIntent();
        if (appIntent.getBooleanExtra(SessionBroadcastReceiver.REOPEN_SESSION, false)) {
            Intent startSession = new Intent(this, SessionActivity.class);
            startSession.putExtra(SessionBroadcastReceiver.SESSION_START, appIntent.getLongExtra(SessionBroadcastReceiver.SESSION_START, System.currentTimeMillis()));
            startSession.putExtra(SessionBroadcastReceiver.SESSION_END, appIntent.getLongExtra(SessionBroadcastReceiver.SESSION_END, System.currentTimeMillis()));
            startSession.putExtra(SessionBroadcastReceiver.REOPEN_SESSION, true);
            if (appIntent.hasExtra(SessionBroadcastReceiver.DELETE_INTENT)) {
                AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
                assert alarmMgr != null;
                // prevents notifications from being triggered once the app has been opened again
                alarmMgr.cancel((PendingIntent)appIntent.getParcelableExtra(SessionBroadcastReceiver.DELETE_INTENT));
            }

            startActivity(startSession);
        }


        // Setup component interactions
        newSession = findViewById(R.id.new_session_outer);
        newSession.setOnClickListener(this);

        // Setup pet animation
        pet = findViewById(R.id.home_pet_view);
        Glide.with(this).asGif().load(R.raw.pet_idle).into(pet);
    }

    @Override
    /**
     * This method is called when any clickable item
     * in the home_layout is pressed.
     * Each item results in a different action taken.
     * @param view The view that called this method
     */
    public void onClick(View view) {
        // create this intent
        switch (view.getId()) {
            case R.id.new_session_outer:
                Intent intent = new Intent(this, SessionActivity.class);
                startActivity(intent);
                break;
        }
    }
}