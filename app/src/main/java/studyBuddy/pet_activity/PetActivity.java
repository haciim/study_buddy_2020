package studyBuddy.pet_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.studdybuddy.R;

import studyBuddy.main_activity.MainActivity;
import studyBuddy.pet.Pet;
import studyBuddy.pet.PetAnimation;
import studyBuddy.util.PrimaryColorPicker;

public class PetActivity extends AppCompatActivity
    implements View.OnClickListener{
    private CardView homeButton;
    private CardView feedButton;
    private CardView batheButton;
    private CardView colorButton;

    private RelativeLayout petNameButton;

    private TextView petName;
    private TextView petMood;
    private TextView petTrust;

    private ImageView petView;

    private PetStatus pStatus;

    private Pet pet;

    private PetAnimation petAnimation;

    @IdRes
    private static final int[] buttons = {
        R.id.pet_bathe_button_outer, R.id.pet_feed_button_outer,
        R.id.pet_color_button_outer, R.id.pet_home_button_outer
    };

    private enum PetStatus {
        FEEDING, BATHING, NAMING, CHOOSING_COLOR, IDLE
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.pet_layout);
        Intent intent = getIntent();
        if (intent != null) {
            pet = (Pet) intent.getSerializableExtra(MainActivity.PET_KEY);
            petAnimation = (PetAnimation) intent.getSerializableExtra(MainActivity.PET_ANIMATION_KEY);
        } else {
            Log.i("PET_ACTIVITY", "Bad Parent, Could not load pet");
            this.finish();
            return;
        }

        this.pStatus = PetStatus.IDLE;

        // Set time of day lighting
        PrimaryColorPicker.setBackgroundFilter(this, findViewById(R.id.pet_home_button_inner));
        PrimaryColorPicker.setBackgroundFilter(this, findViewById(R.id.pet_activity_background));

        Window window = this.getWindow();
        window.setStatusBarColor(PrimaryColorPicker.getDayColorInt(this));

        // Initialize view components and callback listeners
        homeButton = findViewById(R.id.pet_home_button_outer);
        homeButton.setOnClickListener(this);

        feedButton = findViewById(R.id.pet_feed_button_outer);
        feedButton.setOnClickListener(this);

        batheButton = findViewById(R.id.pet_bathe_button_outer);
        batheButton.setOnClickListener(this);

        colorButton = findViewById(R.id.pet_color_button_outer);
        colorButton.setOnClickListener(this);

        petName = findViewById(R.id.pet_name_text);
        petName.setText(pet.getName());

        petNameButton = findViewById(R.id.pet_name_outer);
        petNameButton.setOnClickListener(this);

        petMood = findViewById(R.id.pet_mood_text);
        String mood = "Mood: " + pet.getMoodLevel();
        petMood.setText(mood);

        petTrust = findViewById(R.id.pet_trust_text);
        String trust = "Trust: " + pet.getTrustLevel();
        petTrust.setText(trust);

        // Display pet gif
        petAnimation.maintenanceCheck();
        petView = findViewById(R.id.pet_pet_image);
        Glide.with(this).asGif().load(petAnimation.getCurGif()).into(petView);
    }

    @Override
    public void onClick(View v) {
        // TODO: change animations based off of action
        switch (v.getId()) {
            case R.id.pet_bathe_button_outer:
                if (pet.getIsBathed()) {
                    Toast.makeText(this, "Pet has been recently bathed", Toast.LENGTH_SHORT);
                } else {
                    pet.bathe();
                }
                break;
            case R.id.pet_feed_button_outer:
                if (pet.getIsFed()) {
                    Toast.makeText(this, "Pet has been recently fed", Toast.LENGTH_SHORT).show();
                } else {
                    pet.feed();
                }
                break;
            case R.id.pet_color_button_outer:
                if (pet.getMoodLevel() >= 2) {
                    // TODO: Recolor pet
                } else {
                    Toast.makeText(this, "Pet needs mood level of 2 or higher to change color",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pet_name_outer:
                if (pet.getTrustLevel() >= 2) {
                    // TODO: Rename pet
                } else {
                    Toast.makeText(this, "Pet needs trust level of 2 or higher to respond to new name",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pet_home_button_outer:
                this.finish();
                break;
        }
    }
}
