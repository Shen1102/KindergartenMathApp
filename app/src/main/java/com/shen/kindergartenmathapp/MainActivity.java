package com.shen.kindergartenmathapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import com.google.android.material.button.MaterialButtonToggleGroup;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_DIFFICULTY = "difficulty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioGroup rg = findViewById(R.id.rgDifficulty);
        final String[] diff = new String[]{"EASY"};
        rg.setOnCheckedChangeListener((group, checkedId) ->
                diff[0] = (checkedId == R.id.rbEasy) ? "EASY" : "HARD"
        );

        Button btnCounting = findViewById(R.id.btnCounting);
        btnCounting.setOnClickListener(v -> {
            Intent i = new Intent(this, CountingActivity.class);
            i.putExtra(EXTRA_DIFFICULTY, diff[0]);
            startActivity(i);
        });

        Button btnNumberRecog = findViewById(R.id.btnNumberRecog);
        btnNumberRecog.setOnClickListener(v -> {
            Intent i = new Intent(this, NumberRecognitionActivity.class);
            i.putExtra(EXTRA_DIFFICULTY, diff[0]);
            startActivity(i);
        });

        Button btnMissing = findViewById(R.id.btnMissing);
        btnMissing.setOnClickListener(v -> {
            Intent i = new Intent(this, MissingNumberActivity.class);
            i.putExtra(EXTRA_DIFFICULTY, diff[0]);
            startActivity(i);
        });
    }
}
