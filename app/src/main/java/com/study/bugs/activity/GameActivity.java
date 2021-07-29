package com.study.bugs.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.study.bugs.intents.GameMusic;
import com.study.bugs.logic.GameField;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameField(this));
        startService(new Intent(this, GameMusic.class));
        this.getSupportActionBar().hide();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, GameMusic.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, GameMusic.class));
    }

}
