package com.shen.kindergartenmathapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CountingActivity extends AppCompatActivity {

    private TextView questionText, scoreText;
    private GridLayout objectsContainer;
    private Button[] opts;
    private Button btnNext;
    private LinearProgressIndicator progress;

    // Sounds
    private MediaPlayer correctSound, wrongSound;

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

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Sounds
        correctSound = MediaPlayer.create(this, R.raw.correct);
        wrongSound   = MediaPlayer.create(this, R.raw.wrong);

        String diff = getIntent().getStringExtra(MainActivity.EXTRA_DIFFICULTY);
        if ("HARD".equals(diff)) { lo = 0; hi = 99; } else { lo = 0; hi = 10; }

        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        objectsContainer = findViewById(R.id.objectsContainer);
        btnNext = findViewById(R.id.btnNext);
        progress = findViewById(R.id.progress);

        opts = new Button[]{
                findViewById(R.id.opt1),
                findViewById(R.id.opt2),
                findViewById(R.id.opt3),
                findViewById(R.id.opt4)
        };

        progress.setMax(roundSize);
        progress.setProgressCompat(0, false);

        for (Button b : opts) b.setOnClickListener(v -> onPick(Integer.parseInt(b.getText().toString())));
        btnNext.setOnClickListener(v -> {
            if (waiting) return;
            if (currentQ >= roundSize) { showRoundSummary(); return; }
            resetButtonStyles();
            nextQuestion();
        });

        updateScoreUi();
        nextQuestion();
    }

    private void nextQuestion() {
        int count = randInRange(lo, hi);
        answer = count;
        questionText.setText("How many stars are there?");
        objectsContainer.post(() -> renderObjects(count));

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

        // Play sound
        playSound(correct ? correctSound : wrongSound);

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

    // Ten-frame grid with responsive squares
    private void renderObjects(int n) {
        objectsContainer.removeAllViews();
        final int COLS = 10;
        objectsContainer.setColumnCount(COLS);

        int totalBoxes = Math.max(((n + COLS - 1) / COLS) * COLS, COLS);

        int sidePaddingPx = dp(24) * 2;
        int availablePx = objectsContainer.getWidth();
        if (availablePx <= 0) {
            availablePx = getResources().getDisplayMetrics().widthPixels - sidePaddingPx;
        } else {
            availablePx -= objectsContainer.getPaddingLeft() + objectsContainer.getPaddingRight();
        }

        int gapPx = dp(4);
        int totalGapsPx = (COLS - 1) * gapPx;

        int boxSizePx = (availablePx - totalGapsPx) / COLS;
        if (boxSizePx < dp(28)) boxSizePx = dp(28);

        int pad = dp(6);

        for (int i = 0; i < totalBoxes; i++) {
            LinearLayout cell = new LinearLayout(this);
            cell.setOrientation(LinearLayout.VERTICAL);
            cell.setGravity(Gravity.CENTER);
            cell.setBackgroundResource(R.drawable.missing_number_normal);
            cell.setPadding(pad, pad, pad, pad);

            if (i < n) {
                ImageView iv = new ImageView(this);
                iv.setImageResource(R.drawable.ic_star_24);
                cell.addView(iv, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
            }

            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = boxSizePx;
            glp.height = boxSizePx;
            int col = i % COLS;
            int row = i / COLS;
            int left = (col == 0) ? 0 : gapPx;
            int top  = (row == 0) ? 0 : gapPx;
            glp.setMargins(left, top, 0, 0);

            cell.setLayoutParams(glp);
            objectsContainer.addView(cell);
        }
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

    private void playSound(MediaPlayer mp) {
        if (mp == null) return;
        try {
            if (mp.isPlaying()) mp.seekTo(0);
            mp.start();
        } catch (IllegalStateException ignored) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (correctSound != null) { correctSound.release(); correctSound = null; }
        if (wrongSound != null)   { wrongSound.release();   wrongSound = null; }
    }
}
