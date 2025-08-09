package com.shen.kindergartenmathapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class MissingNumberActivity extends AppCompatActivity {

    // UI
    private TextView titleText, scoreText;
    private LinearLayout sequenceContainer;
    private Button[] opts;
    private Button btnMenu, btnNext;
    private LinearProgressIndicator progress;

    // Game state
    private int answer;
    private int lo, hi;
    private int score = 0, total = 0;
    private final Random rand = new Random();

    private final int roundSize = 10;   // questions per round
    private int currentQ = 0;
    private boolean waiting = false;  // lock while showing feedback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_number);

        String diff = getIntent().getStringExtra(MainActivity.EXTRA_DIFFICULTY);
        if ("HARD".equals(diff)) { lo = 0; hi = 99; } else { lo = 0; hi = 20; }

        // Find views
        titleText = findViewById(R.id.titleText);
        scoreText = findViewById(R.id.scoreText);
        btnMenu = findViewById(R.id.btnMenu);
        sequenceContainer = findViewById(R.id.sequenceContainer);
        progress = findViewById(R.id.progress);
        btnNext = findViewById(R.id.btnNext);

        opts = new Button[] {
                findViewById(R.id.opt1),
                findViewById(R.id.opt2),
                findViewById(R.id.opt3),
                findViewById(R.id.opt4)
        };

        // Progress init
        progress.setMax(roundSize);
        progress.setProgressCompat(0, false);

        // Manual next (optional; respects waiting + end-of-round)
        btnNext.setOnClickListener(v -> {
            if (waiting) return;
            // If round is complete, show summary instead of advancing
            if (currentQ >= roundSize) {
                showRoundSummary();
                return;
            }
            // Move to a new question without changing score/total
            resetButtonStyles();
            nextQuestion();
        });

        // Option click listeners
        for (Button b : opts) {
            b.setOnClickListener(v -> onPick(Integer.parseInt(b.getText().toString())));
        }

        btnMenu.setOnClickListener(v -> finish());

        titleText.setText("Missing Number");
        updateScoreUi();
        nextQuestion();
    }

    private void nextQuestion() {
        // create a simple arithmetic sequence; use step > 1 on higher range
        int step = (hi > 20) ? randInRange(2, 3) : 1;
        int start = randInRange(lo, Math.max(lo, hi - step * 4)); // ensure room for 5 terms
        int[] seq = new int[5];
        for (int i = 0; i < 5; i++) seq[i] = start + i * step;

        int missIndex = rand.nextInt(5);
        answer = seq[missIndex];

        // Clear old sequence and build new "cards"
        sequenceContainer.removeAllViews();

        for (int i = 0; i < 5; i++) {
            TextView tv = new TextView(this);

            if (i == missIndex) {
                tv.setText("");
                tv.setBackgroundResource(R.drawable.missing_number_highlight); // blue dashed bg
                tv.setContentDescription("Missing number");
            } else {
                tv.setText(String.valueOf(seq[i]));
                tv.setBackgroundResource(R.drawable.missing_number_normal);    // gray border bg
                tv.setContentDescription(String.valueOf(seq[i]));
            }

            tv.setTextSize(18f);
            tv.setGravity(Gravity.CENTER);
            tv.setIncludeFontPadding(false);
            tv.setPadding(dp(8), dp(6), dp(8), dp(6));
            // Uniform size & prevent wrap of two-digit numbers
            tv.setMinWidth(dp(44));
            tv.setMaxWidth(dp(44));
            tv.setMinHeight(dp(44));
            tv.setMaxLines(1);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(dp(4), 0, dp(4), 0);
            tv.setLayoutParams(lp);

            sequenceContainer.addView(tv);
        }

        // Build 4 answer options (include correct + 3 unique distractors)
        Set<Integer> set = new HashSet<>();
        set.add(answer);
        while (set.size() < 4) set.add(randInRange(lo, hi));
        List<Integer> list = new ArrayList<>(set);
        Collections.shuffle(list, rand);

        // Set option text and reset base tint each question
        int normal = ContextCompat.getColor(this, R.color.btnNormal);
        for (int i = 0; i < 4; i++) {
            opts[i].setText(String.valueOf(list.get(i)));
            opts[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(normal));
            opts[i].setEnabled(true);
        }
    }

    private void onPick(int picked) {
        if (waiting) return;
        waiting = true;

        total++;
        boolean correct = (picked == answer);
        if (correct) score++;

        // find which button was clicked (to color it)
        Button clicked = null;
        for (Button b : opts) {
            if (b.getText().toString().equals(String.valueOf(picked))) { clicked = b; break; }
        }

        if (clicked != null) showFeedback(clicked, correct);
        setButtonsEnabled(false);

        Toast.makeText(this, correct ? "Correct!" : "Try again!", Toast.LENGTH_SHORT).show();
        updateScoreUi();

        // progress
        currentQ++;
        progress.setProgressCompat(Math.min(currentQ, roundSize), true);

        // auto-advance; on round-end show summary and STOP there
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (currentQ >= roundSize) {
                showRoundSummary();   // do not reset here; wait for user choice
                return;
            }
            resetButtonStyles();
            setButtonsEnabled(true);
            waiting = false;
            nextQuestion();
        }, 700);
    }

    // --- helpers ---

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
                    // Reset state ONLY when the user chooses to play again
                    score = 0;
                    total = 0;
                    currentQ = 0;
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
