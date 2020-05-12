package studyBuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.studdybuddy.R;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener {

    private CardView newSession;
    @Override
    /**
     * This method called when the app is opened.
     * Any data retrieval should start here.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        newSession = findViewById(R.id.new_session_outer);
        newSession.setOnClickListener(this);
    }

    @Override
    /**
     * This method is called when any clickable item
     * in the home_layout is pressed.
     * Each item results in a different action taken.
     * @param view The view that called this method
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_session_outer:
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
                break;
        }
    }
}
