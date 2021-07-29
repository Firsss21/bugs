package com.study.bugs.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.study.bugs.R;
import com.study.bugs.Utils;
import com.study.bugs.intents.Music;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private TextView recordValText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();
        startButton = findViewById(R.id.bt_start);
        recordValText = findViewById(R.id.tv_record_val);
        recordValText.setText(Integer.toString(Utils.loadSettings("record", this)));

        startService(new Intent(this, Music.class));
        startButton.setOnClickListener(v -> {
            Context context = MainActivity.this;
            Class destinationActivity = GameActivity.class;
            Intent gameActivityIntent = new Intent(context, destinationActivity);
            startActivity(gameActivityIntent);
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, Music.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, Music.class));
        recordValText.setText(Integer.toString(Utils.loadSettings("record", this)));
    }

}
