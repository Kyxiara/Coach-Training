package com.example.kellynarboux.coach_training;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.kellynarboux.coach_training.Model.CountableExercise;

public class CountExercise extends AppCompatActivity {

    TextView message;
    CountableExercise exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_exercise);

        exercise = new CountableExercise(getIntent().getStringExtra("myName"), getIntent().getIntExtra("myNb", 10));

        message = (TextView)findViewById(R.id.message);
        message.setText("C'est parti pour " + exercise.getName() + " " + exercise.getCount());
    }

}
