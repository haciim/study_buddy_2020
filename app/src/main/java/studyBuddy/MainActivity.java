package studyBuddy;

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

import java.util.List;

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

        // Setup component interactions
        newSession = findViewById(R.id.new_session_outer);
        newSession.setOnClickListener(this);

        sessionHistoryButton = findViewById(R.id.session_history_outer);
        sessionHistoryButton.setOnClickListener(this);

        // Setup pet animation
        petView = findViewById(R.id.home_pet_view);
        Glide.with(this).asGif().load(R.raw.pet_idle).into(petView);

        pet = DataManager.load(Pet.class, this.getApplicationContext());
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