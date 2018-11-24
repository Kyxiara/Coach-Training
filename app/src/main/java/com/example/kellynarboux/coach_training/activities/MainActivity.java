package com.example.kellynarboux.coach_training.activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.kellynarboux.coach_training.R;
import com.example.kellynarboux.coach_training.db.UserViewModel;
import com.example.kellynarboux.coach_training.model.CountableExercise;
import com.example.kellynarboux.coach_training.model.Exercise;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Button buttonOpenCV;
    Button button_begin;
    String textExercise;
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToogle;
    NavigationView navigationView;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) || !ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 1);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        mToogle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, userList -> {
            if(userList != null && userList.isEmpty()){  // FIXME
                navigationView.getMenu().findItem(R.id.navigation_profil).setVisible(false);
                navigationView.getMenu().findItem(R.id.navigation_calendrier).setVisible(false);
            }else{
                navigationView.getMenu().findItem(R.id.navigation_register).setVisible(false);
            }
        });

        button_begin = findViewById(R.id.button_begin);
        button_begin.setOnClickListener(v -> startSpeechToText());

        buttonOpenCV = findViewById(R.id.buttonOpenCV);
        buttonOpenCV.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OpenCVActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, 666);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 666: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textExercise = result.get(0);
                }
                break;
            }
        }

        Log.d("tag", textExercise);
        Exercise exercise = Exercise.textToExercise(textExercise);

        if (exercise != null) {
            CountableExercise countableExercise = (CountableExercise) exercise;
            Intent myIntent = new Intent(MainActivity.this, CountExercise.class);
            myIntent.putExtra("myName", countableExercise.getName());
            myIntent.putExtra("myNb", countableExercise.getCount());
            startActivity(myIntent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.navigation_profil :
                startActivity(new Intent(MainActivity.this, ProfilActivity.class));
                break;
            case R.id.navigation_exercices :
                break;
            case R.id.navigation_calendrier :
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                break;
            case R.id.navigation_options :
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.navigation_register :
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //super.onBackPressed();
        }
    }
}
