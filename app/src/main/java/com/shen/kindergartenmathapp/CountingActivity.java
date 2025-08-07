package com.shen.kindergartenmathapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class CountingActivity extends AppCompatActivity {

    ImageView[] imageViews;
    TextView questionText;
    Button[] optionButtons;
    int correctAnswer;
    int totalImages = 5; // You can adjust this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counting);

        imageViews = new ImageView[]{
                findViewById(R.id.img1),
                findViewById(R.id.img2),
                findViewById(R.id.img3),
                findViewById(R.id.img4),
                findViewById(R.id.img5)
        };

        questionText = findViewById(R.id.txtQuestion);
        optionButtons = new Button[]{
                findViewById(R.id.option1),
                findViewById(R.id.option2),
                findViewById(R.id.option3)
        };

        generateQuestion();
    }

    private void generateQuestion() {
        Random rand = new Random();
        correctAnswer = rand.nextInt(totalImages) + 1;

        // Show that many images
        for (int i = 0; i < totalImages; i++) {
            imageViews[i].setVisibility(i < correctAnswer ? View.VISIBLE : View.INVISIBLE);
        }

        questionText.setText("How many objects?");

        int correctPos = rand.nextInt(3);
        for (int i = 0; i < 3; i++) {
            if (i == correctPos) {
                optionButtons[i].setText(String.valueOf(correctAnswer));
                optionButtons[i].setOnClickListener(v -> showResult(true));
            } else {
                int wrongAnswer = correctAnswer;
                while (wrongAnswer == correctAnswer) {
                    wrongAnswer = rand.nextInt(totalImages) + 1;
                }
                int finalWrong = wrongAnswer;
                optionButtons[i].setText(String.valueOf(finalWrong));
                optionButtons[i].setOnClickListener(v -> showResult(false));
            }
        }
    }

    private void showResult(boolean isCorrect) {
        if (isCorrect) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
        }
        generateQuestion(); // Load next question
    }
}