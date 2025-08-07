package com.shen.kindergartenmathapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    Button btnCounting, btnRecognition, btnMissing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCounting = findViewById(R.id.btnCounting);
        btnRecognition = findViewById(R.id.btnRecognition);
        btnMissing = findViewById(R.id.btnMissing);

        btnCounting.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CountingActivity.class)));

        btnRecognition.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RecognitionActivity.class)));

        btnMissing.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MissingNumberActivity.class)));
    }
}