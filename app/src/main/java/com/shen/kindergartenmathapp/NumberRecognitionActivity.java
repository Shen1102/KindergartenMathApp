package com.shen.kindergartenmathapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
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

public class NumberRecognitionActivity extends AppCompatActivity {

    private TextView questionText, numberText, scoreText;
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
        setContentView(R.layout.activity_number_recognition);

        // Toolbar with back arrow
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
        numberText   = findViewById(R.id.numberText);
        scoreText    = findViewById(R.id.scoreText);
        btnNext      = findViewById(R.id.btnNext);
        progress     = findViewById(R.id.progress);

        opts = new Button[] {
                findViewById(R.id.opt1),
                findViewById(R.id.opt2),
                findViewById(R.id.opt3),
                findViewById(R.id.opt4)
        };

        progress.setMax(roundSize);
        progress.setProgressCompat(0, false);

        for (Button b : opts) b.setOnClickListener(v -> onPick(b.getText().toString()));
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
        answer = randInRange(lo, hi);

        questionText.setText("Which word matches this number?");
        numberText.setText(String.valueOf(answer));

        // Build 4 options from 0..hi
        Set<Integer> nums = new HashSet<>();
        nums.add(answer);
        while (nums.size() < 4) nums.add(randInRange(lo, hi));
        List<Integer> numList = new ArrayList<>(nums);
        Collections.shuffle(numList, rand);

        int normal = ContextCompat.getColor(this, R.color.btnNormal);
        for (int i = 0; i < 4; i++) {
            String word = numberToWords(numList.get(i));
            opts[i].setText(word);
            opts[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(normal));
            opts[i].setEnabled(true);
        }
    }

    private void onPick(String pickedWord) {
        if (waiting) return;
        waiting = true;

        total++;
        boolean correct = pickedWord.equalsIgnoreCase(numberToWords(answer));
        if (correct) score++;

        Button clicked = null;
        for (Button b : opts) {
            if (b.getText().toString().equalsIgnoreCase(pickedWord)) { clicked = b; break; }
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

    private void playSound(MediaPlayer mp) {
        if (mp == null) return;
        try {
            if (mp.isPlaying()) mp.seekTo(0);
            mp.start();
        } catch (IllegalStateException ignored) {}
    }

    // 0..99 to words (lowercase; hyphen between tens and ones)
    private String numberToWords(int n) {
        final String[] ones = {
                "Zero","One","Two","Three","Four","Five","Six","Seven","Eight","Nine",
                "Ten","Eleven","Twelve","Thirteen","Fourteen","Fifteen","Sixteen","Seventeen","Eighteen","Nineteen"
        };
        final String[] tens = {"", "", "Twenty","Thirty","Forty","Fifty","Sixty","Seventy","Eighty","Ninety"};

        if (n < 20) return ones[n];
        int t = n / 10, o = n % 10;
        if (o == 0) return tens[t];
        return tens[t] + "-" + ones[o];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (correctSound != null) { correctSound.release(); correctSound = null; }
        if (wrongSound != null)   { wrongSound.release();   wrongSound = null; }
    }
}
