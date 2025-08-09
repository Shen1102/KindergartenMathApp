package com.shen.kindergartenmathapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class NumberRecognitionActivity extends AppCompatActivity {

    private TextView titleText, questionText, scoreText;
    private Button[] opts;
    private Button btnMenu, btnNext;
    private LinearProgressIndicator progress;

    private int answer;
    private int lo, hi;
    private int score = 0, total = 0;
    private final Random rand = new Random();

    private final int roundSize = 10;
    private int currentQ = 0;
    private boolean waiting = false;

    private static final String[] NUMBER_WORDS = {
            "zero","one","two","three","four","five","six","seven","eight","nine","ten",
            "eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eighteen","nineteen","twenty"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_recognition);

        String diff = getIntent().getStringExtra(MainActivity.EXTRA_DIFFICULTY);
        if ("HARD".equals(diff)) { lo = 0; hi = 20; } else { lo = 0; hi = 10; }

        titleText = findViewById(R.id.titleText);
        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        btnMenu = findViewById(R.id.btnMenu);
        btnNext = findViewById(R.id.btnNext);
        progress = findViewById(R.id.progress);

        opts = new Button[] {
                findViewById(R.id.opt1),
                findViewById(R.id.opt2),
                findViewById(R.id.opt3),
                findViewById(R.id.opt4)
        };

        progress.setMax(roundSize);
        progress.setProgressCompat(0, false);

        for (Button b : opts) b.setOnClickListener(v -> onPick(b.getText().toString()));
        btnMenu.setOnClickListener(v -> finish());
        btnNext.setOnClickListener(v -> {
            if (waiting) return;
            if (currentQ >= roundSize) { showRoundSummary(); return; }
            resetButtonStyles();
            nextQuestion();
        });

        titleText.setText("Number Recognition");
        updateScoreUi();
        nextQuestion();
    }

    private void nextQuestion() {
        answer = randInRange(lo, hi);
        questionText.setText("Which word matches this number: " + answer + " ?");

        Set<String> set = new HashSet<>();
        set.add(NUMBER_WORDS[answer]);
        while (set.size() < 4) set.add(NUMBER_WORDS[randInRange(lo, hi)]);
        List<String> list = new ArrayList<>(set);
        Collections.shuffle(list, rand);

        int normal = ContextCompat.getColor(this, R.color.btnNormal);
        for (int i = 0; i < 4; i++) {
            opts[i].setText(list.get(i));
            opts[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(normal));
            opts[i].setEnabled(true);
        }
    }

    private void onPick(String pickedWord) {
        if (waiting) return;
        waiting = true;

        total++;
        boolean correct = pickedWord.equalsIgnoreCase(NUMBER_WORDS[answer]);
        if (correct) score++;

        Button clicked = null;
        for (Button b : opts) {
            if (b.getText().toString().equalsIgnoreCase(pickedWord)) { clicked = b; break; }
        }
        if (clicked != null) showFeedback(clicked, correct);
        setButtonsEnabled(false);

        Toast.makeText(this, correct ? "Correct!" : "Try again!", Toast.LENGTH_SHORT).show();
        updateScoreUi();

        currentQ++;
        progress.setProgressCompat(Math.min(currentQ, roundSize), true);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (currentQ >= roundSize) { showRoundSummary(); return; }
            resetButtonStyles();
            setButtonsEnabled(true);
            waiting = false;
            nextQuestion();
        }, 700);
    }

    private void setButtonsEnabled(boolean enabled) {
        for (Button b : opts) b.setEnabled(enabled);
        btnNext.setEnabled(enabled);
    }

    private void showFeedback(Button pickedBtn, boolean correct) {
        int color = ContextCompat.getColor(this, correct ? R.color.correctGreen : R.color.wrongRed);
        pickedBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
    }

    private void resetButtonStyles() {
        int normal = ContextCompat.getColor(this, R.color.btnNormal);
        for (Button b : opts) {
            b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(normal));
        }
    }

    private void showRoundSummary() {
        String msg = "You scored " + score + " out of " + total + "!";
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Round Complete")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Play again", (d, w) -> {
                    score = 0; total = 0; currentQ = 0;
                    progress.setProgressCompat(0, false);
                    updateScoreUi();
                    resetButtonStyles();
                    setButtonsEnabled(true);
                    waiting = false;
                    nextQuestion();
                })
                .setNegativeButton("Back to Menu", (d, w) -> finish())
                .show();
    }

    private int randInRange(int a, int b) { return a + rand.nextInt(b - a + 1); }
    private void updateScoreUi() { scoreText.setText("Score: " + score + "/" + total); }
}
