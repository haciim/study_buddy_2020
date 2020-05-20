package studyBuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.example.studdybuddy.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
        switch (view.getId()) {
            case R.id.new_session_outer:
                Intent intent = new Intent(this, SessionActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
    }
}
