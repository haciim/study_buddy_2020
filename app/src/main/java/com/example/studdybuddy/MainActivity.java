package com.example.studdybuddy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // test
        setContentView(R.layout.activity_main);
        System.out.println("hello world");
    }
}
