package studyBuddy.pet_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.studdybuddy.R;

import studyBuddy.main_activity.MainActivity;
import studyBuddy.pet.Pet;
import studyBuddy.pet.PetAnimation;
import studyBuddy.util.PrimaryColorPicker;

public class PetActivity extends AppCompatActivity
    implements View.OnClickListener{
    private ImageView homeButton;
    private ImageView feedButton;
    private ImageView batheButton;
    private ImageView colorButton;

    private RelativeLayout petNameButton;

    private TextView petName;
    private TextView petMood;
    private TextView petTrust;

    private EditText nameEdit;

    private ImageView petView;

    private PetStatus pStatus;

    private Pet pet;

    private PetAnimation petAnimation;

    private boolean buttonsDisabled;

    @IdRes
    private static final int[] buttons = {
        R.id.pet_bathe_button_inner, R.id.pet_feed_button_inner,
        R.id.pet_color_button_inner, R.id.pet_home_button_inner,
        R.id.pet_name_outer
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
        this.buttonsDisabled = false;

        // Set time of day lighting
        PrimaryColorPicker.setBackgroundFilter(this, findViewById(R.id.pet_home_button_inner));
        PrimaryColorPicker.setBackgroundFilter(this, findViewById(R.id.pet_activity_background));

        Window window = this.getWindow();
        window.setStatusBarColor(PrimaryColorPicker.getDayColorInt(this));

        // Initialize view components and callback listeners
        homeButton = findViewById(R.id.pet_home_button_inner);
        homeButton.setOnClickListener(this);

        feedButton = findViewById(R.id.pet_feed_button_inner);
        feedButton.setOnClickListener(this);

        batheButton = findViewById(R.id.pet_bathe_button_inner);
        batheButton.setOnClickListener(this);

        colorButton = findViewById(R.id.pet_color_button_inner);
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

        nameEdit = findViewById(R.id.pet_name_edit_text);
        // https://developer.android.com/training/keyboard-input/style
        nameEdit.setOnEditorActionListener((v, a, e)-> {
            boolean handled = false;
            if (a == EditorInfo.IME_ACTION_DONE) {
                setNewName(nameEdit.getText().toString());
                handled = true;
            }
            return handled;
        });

        // Display pet gif
        petAnimation.maintenanceCheck();
        petView = findViewById(R.id.pet_pet_image);
        Glide.with(this).asGif().load(petAnimation.getCurGif(this)).into(petView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.PET_KEY, this.pet);
        intent.putExtra(MainActivity.PET_ANIMATION_KEY, this.petAnimation);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        // TODO: change animations based off of action
        switch (v.getId()) {
            case R.id.pet_bathe_button_inner:
                if (pet.getIsBathed()) {
                    Toast.makeText(this, pet.getName() + " been recently bathed", Toast.LENGTH_SHORT).show();
                } else {
                    petAnimation.setCurAnimation("bathing");
                    Glide.with(this).asGif().load(petAnimation.getCurGif(this)).into(petView);
                    pet.bathe();
                }
                break;
            case R.id.pet_feed_button_inner:
                if (pet.getIsFed()) {
                    Toast.makeText(this, pet.getName() + " has been recently fed", Toast.LENGTH_SHORT).show();
                } else {
                    petAnimation.setCurAnimation("feeding");
                    Glide.with(this).asGif().load(petAnimation.getCurGif(this)).into(petView);
                    pet.feed();
                }
                break;
            case R.id.pet_color_button_inner:
                if (pet.getTrustLevel() >= 7) {
                    disableButtons();
                    // Recolor pet
                } else {
                    Toast.makeText(this, pet.getName() + " needs trust level of 7 or higher to change color",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pet_name_outer:
                if (pet.getTrustLevel() >= 2) {
                    disableButtons();
                    nameEdit.setText(pet.getName());
                    nameEdit.setVisibility(View.VISIBLE);
                    nameEdit.setEnabled(true);
                    nameEdit.setSelection(nameEdit.length());
                } else {
                    Toast.makeText(this, pet.getName() + " needs trust level of 2 or higher to respond to new name",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pet_home_button_inner:
                this.finish();
                break;
        }
    }

    private void disableButtons() {
        for (int i = 0; i < buttons.length; i++) {
            int id = buttons[i];
            View view = findViewById(id);
            if (view instanceof ImageView) {
                if (buttonsDisabled) {
                    view.setBackground(getDrawable(R.drawable.button_base));
                } else {
                    view.setBackground(getDrawable(R.drawable.button_base_disabled));
                }
            }
            view.setClickable(buttonsDisabled);
        }
        buttonsDisabled = !buttonsDisabled;
    }

    private void setNewName(String name) {
        pet.setName(name);
        nameEdit.setVisibility(View.INVISIBLE);
        nameEdit.setEnabled(false);
        petName.setText(pet.getName());
        disableButtons();
    }
}
