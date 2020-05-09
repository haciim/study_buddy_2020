package studyBuddy;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studdybuddy.R;

import studyBuddy.timemanagement.TimelineView;

public class SessionActivity extends AppCompatActivity {

    private Session session;
    private long sessionDuration;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.session_layout);
        session = new Session();
        sessionDuration = 100000;    // dummy value
        TimelineView timeline = findViewById(R.id.timeLine);
        TextView timer = findViewById(R.id.time);


        // callback:
        //  - we'll have some idea of the duration of our session from the intent sent to it
        //  -

        SessionTimerCallback callback = (secondsPassed, duration) -> {
            double percentage = ((double)secondsPassed / duration);
            timeline.setPercentageCompletion(percentage);
            timer.setText(Session.formatTime(secondsPassed / 1000));
        };

        timer.setText(getResources().getText(R.string.zero_time));

        session.setTimerCallback(callback);
        session.startSession("hello", 100000);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}
