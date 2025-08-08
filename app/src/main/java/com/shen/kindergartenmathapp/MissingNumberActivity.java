package com.shen.kindergartenmathapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class MissingNumberActivity extends AppCompatActivity {
    private TextView titleText, questionText, scoreText;
    private Button[] opts;
    private Button btnMenu;
    private int answer;
    private int lo, hi;
    private int score = 0, total = 0;
    private final Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_number);

        String diff = getIntent().getStringExtra(MainActivity.EXTRA_DIFFICULTY);
        if ("HARD".equals(diff)) { lo = 0; hi = 99; } else { lo = 0; hi = 20; }

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
        for (Button b : opts) b.setOnClickListener(v -> onPick(Integer.parseInt(b.getText().toString())));

        btnMenu.setOnClickListener(v -> finish());

        titleText.setText("Missing Number");
        nextQuestion();
    }

    private void nextQuestion() {
        int step = (hi > 20) ? randInRange(2, 3) : 1;
        int start = randInRange(lo, Math.max(lo, hi - step * 4));
        int[] seq = new int[5];
        for (int i = 0; i < 5; i++) seq[i] = start + i * step;

        int missIndex = rand.nextInt(5);
        answer = seq[missIndex];

        StringBuilder q = new StringBuilder("Fill the missing number:\n");
        for (int i = 0; i < 5; i++) {
            q.append(i == missIndex ? " ? " : seq[i]);
            if (i < 4) q.append(", ");
        }
        questionText.setText(q.toString());

        Set<Integer> set = new HashSet<>();
        set.add(answer);
        while (set.size() < 4) set.add(randInRange(lo, hi));
        List<Integer> list = new ArrayList<>(set);
        Collections.shuffle(list, rand);
        for (int i = 0; i < 4; i++) opts[i].setText(String.valueOf(list.get(i)));
    }

    private void onPick(int picked) {
        total++;
        if (picked == answer) {
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
