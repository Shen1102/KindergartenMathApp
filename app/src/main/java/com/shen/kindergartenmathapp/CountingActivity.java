package com.shen.kindergartenmathapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
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

public class CountingActivity extends AppCompatActivity {

    private TextView titleText, questionText, scoreText;
    private GridLayout objectsContainer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counting);

        String diff = getIntent().getStringExtra(MainActivity.EXTRA_DIFFICULTY);
        if ("HARD".equals(diff)) { lo = 0; hi = 99; } else { lo = 0; hi = 10; }

        titleText = findViewById(R.id.titleText);
        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        objectsContainer = findViewById(R.id.objectsContainer);
        btnMenu = findViewById(R.id.btnMenu);
        btnNext = findViewById(R.id.btnNext);
        progress = findViewById(R.id.progress);

        opts = new Button[]{
                findViewById(R.id.opt1),
                findViewById(R.id.opt2),
                findViewById(R.id.opt3),
                findViewById(R.id.opt4)
        };

        // progress
        progress.setMax(roundSize);
        progress.setProgressCompat(0, false);

        for (Button b : opts) b.setOnClickListener(v -> onPick(Integer.parseInt(b.getText().toString())));
        btnMenu.setOnClickListener(v -> finish());
        btnNext.setOnClickListener(v -> {
            if (waiting) return;
            if (currentQ >= roundSize) { showRoundSummary(); return; }
            resetButtonStyles();
            nextQuestion();
        });

        titleText.setText("Counting");
        updateScoreUi();
        nextQuestion();
    }

    private void nextQuestion() {
        int count = randInRange(lo, hi);
        answer = count;
        questionText.setText("How many stars are there?");
        renderObjects(count);

        Set<Integer> set = new HashSet<>();
        set.add(answer);
        while (set.size() < 4) set.add(randInRange(lo, hi));
        List<Integer> list = new ArrayList<>(set);
        Collections.shuffle(list, rand);
        int normal = ContextCompat.getColor(this, R.color.btnNormal);
        for (int i = 0; i < 4; i++) {
            opts[i].setText(String.valueOf(list.get(i)));
            opts[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(normal));
            opts[i].setEnabled(true);
        }
    }

    private void renderObjects(int n) {
        objectsContainer.removeAllViews();
        int pad = dp(4);
        for (int i = 0; i < n; i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_star_24);
            iv.setPadding(pad, pad, pad, pad);
            objectsContainer.addView(iv);
        }
    }

    private void onPick(int picked) {
        if (waiting) return;
        waiting = true;

        total++;
        boolean correct = (picked == answer);
        if (correct) score++;

        Button clicked = null;
        for (Button b : opts) {
            if (b.getText().toString().equals(String.valueOf(picked))) { clicked = b; break; }
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
    private int dp(int v) { return Math.round(getResources().getDisplayMetrics().density * v); }
    private void updateScoreUi() { scoreText.setText("Score: " + score + "/" + total); }
}
