package com.example.zad1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_INDEX = "currentIndex";
    private static final String QUIZ_TAG = "MainActivity";
    private static final int REQUEST_CODE_PROMPT = 0;
    public static final String KEY_EXTRA_ANSWER = "pl.edu.pb.wi.quiz.correctAnswer";
    private Button trueButton;
    private Button falseButton;
    private Button nextButton;
    private TextView questionTextView;
    private Button promptButton;

    private Question[] questions = new Question[]{
            new Question(R.string.q_activity,true),
            new Question(R.string.q_find_resources, false),
            new Question(R.string.q_listener,true),
            new Question(R.string.q_resources,true),
            new Question(R.string.q_version,false)
    };

    private int currentIndex = 0, correctAnswers = 0;
    private boolean[] isAnswered = new boolean[questions.length];
    public boolean answerWasShown;


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(QUIZ_TAG, "Wywołana została metoda: OnSaveInstanceState");
        outState.putInt(KEY_CURRENT_INDEX, currentIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(QUIZ_TAG, "OnCreate started");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        nextButton = findViewById(R.id.next_button);
        questionTextView = findViewById(R.id.question_text_view);
        promptButton = findViewById(R.id.hint_button);

        for (int i = 0; i < questions.length; i++) {
            isAnswered[i] = false;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswerCorrectness(true);
            }
        });

        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswerCorrectness(false);
            }
        });

        nextButton.setOnClickListener(view -> {
                if (isAnswered[currentIndex] == true) {
                    currentIndex = (currentIndex + 1) % questions.length;
                    answerWasShown = false;
                    setNextQuestion();
                } else {
                    Toast.makeText(this, "Musisz odpowiedzieć na pytenie", Toast.LENGTH_SHORT).show();
                }
            }
        );
        setNextQuestion();

        promptButton.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, PromptActivity.class);
            boolean correctAnswer = questions[currentIndex].isTrueAnswer();
            intent.putExtra(KEY_EXTRA_ANSWER, correctAnswer);
            startActivityForResult(intent, REQUEST_CODE_PROMPT);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(QUIZ_TAG, "OnStart started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(QUIZ_TAG, "OnResume started");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(QUIZ_TAG, "OnPause started");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(QUIZ_TAG, "OnStop started");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(QUIZ_TAG, "OnDestroy started");
    }

    private void setNextQuestion() {
        if (currentIndex == 0 && isAllAnswers()) {
            Toast.makeText(this, "Wynik: " + correctAnswers + "/" + questions.length, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Rozpoczęto nową sesję", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < questions.length; i++) {
                isAnswered[i] = false;
                correctAnswers = 0;
            }
        }
        questionTextView.setText(questions[currentIndex].getQuestionId());

    }

    private boolean isAllAnswers() {
        for (boolean answerd : isAnswered) {
            if(!answerd)
                return false;
        }
            return true;
    }

    private void checkAnswerCorrectness(boolean userAnswer){
            boolean correctAnswer = questions[currentIndex].isTrueAnswer();
            int resultMessegeId = 0;
            if (answerWasShown) {
                resultMessegeId = R.string.answer_was_shown;
                isAnswered[currentIndex] = true;
                Toast.makeText(this, resultMessegeId, Toast.LENGTH_SHORT).show();
            } else {
                if (!isAnswered[currentIndex]) {
                    if (userAnswer == correctAnswer) {
                        resultMessegeId = R.string.correct_answer;
                        correctAnswers++;
                    } else {
                        resultMessegeId = R.string.incorrect_answer;
                    }
                    isAnswered[currentIndex] = true;
                    Toast.makeText(this, resultMessegeId, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Udzieliłeś już odpowiedzi na to pytanie", Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {return;}
        if (requestCode == REQUEST_CODE_PROMPT) {
            if (data == null) {return;}
            answerWasShown = data.getBooleanExtra(PromptActivity.KEY_EXTRA_ANSWER_SHOWN, false);
        }
    }
}

