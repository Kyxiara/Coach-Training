package com.example.kellynarboux.coach_training;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class CountExercise extends AppCompatActivity {

    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_exercise);

        message = (TextView)findViewById(R.id.message);
        message.setText(getIntent().getStringExtra("mytext"));
    }

}
