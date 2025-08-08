package com.shen.kindergartenmathapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class NumberRecognitionActivity extends AppCompatActivity {
    private TextView titleText, questionText, scoreText;
    private Button[] opts;
    private Button btnMenu;
    private int answer;
    private int lo, hi;
    private int score = 0, total = 0;
    private final Random rand = new Random();

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

        opts = new Button[]{
                findViewById(R.id.opt1),
                findViewById(R.id.opt2),
                findViewById(R.id.opt3),
                findViewById(R.id.opt4)
        };
        for (Button b : opts) b.setOnClickListener(v -> onPick(b.getText().toString()));

        btnMenu.setOnClickListener(v -> finish());

        titleText.setText("Number Recognition");
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
        for (int i = 0; i < 4; i++) opts[i].setText(list.get(i));
    }

    private void onPick(String pickedWord) {
        total++;
        if (pickedWord.equalsIgnoreCase(NUMBER_WORDS[answer])) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
        }
        scoreText.setText("Score: " + score + "/" + total);
        nextQuestion();
    }

    private int randInRange(int a, int b) { return a + rand.nextInt(b - a + 1); }
}
