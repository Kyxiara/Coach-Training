package com.example.kellynarboux.coach_training.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kellynarboux.coach_training.R;
import com.example.kellynarboux.coach_training.model.CountableExercise;
import com.example.kellynarboux.coach_training.model.CountableExerciseType;
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class CountExercise extends AppCompatActivity implements OnDSListener {

    TextView count, name;
    CountableExercise exercise;
    int counter = 0;
    private ProgressBar progressBarCircle;
    private TextToSpeech tts;
    DroidSpeech droidSpeech;

    private static final Pattern p = Pattern.compile("(\\D*(\\d+)\\D*)*");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_exercise);

        exercise = new CountableExercise(CountableExerciseType.valueOf(getIntent().getStringExtra("myName")), getIntent().getIntExtra("myNb", 10));

        tts = new TextToSpeech(CountExercise.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                tts.speak("C'est parti pour " + exercise.getCount() + exercise.getName(), TextToSpeech.QUEUE_ADD, null);
            }
        });
        tts.setLanguage(Locale.FRANCE);

        count = findViewById(R.id.id_count);
        name = findViewById(R.id.id_nameEx);
        count.setText(counter + "/" + exercise.getCount());
        name.setText(exercise.getName().toUpperCase());

        progressBarCircle = findViewById(R.id.progressBarCircle);
        progressBarCircle.setMax(exercise.getCount());
        progressBarCircle.setProgress(counter);

        droidSpeech = new DroidSpeech(this, null);
        droidSpeech.setOnDroidSpeechListener(this);
        droidSpeech.startDroidSpeechRecognition();
    }

    @Override
    public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages)
    {
        // Triggered when the device default languages are retrieved
    }

    @Override
    public void onDroidSpeechRmsChanged(float rmsChangedValue) {

    }

    @Override
    public void onDroidSpeechLiveResult(String liveSpeechResult) {
        Log.d("DroidSpeech", liveSpeechResult);
        String[] sentence = liveSpeechResult.split(" ");
        for (String word : sentence) Log.d("Mot", word);
        indentCounter(sentence);
    }

    @Override
    public void onDroidSpeechFinalResult(String finalSpeechResult) {
        // display the whole sentence
    }

    @Override
    public void onDroidSpeechClosedByUser() {

    }

    @Override
    public void onDroidSpeechError(String errorMsg) {
        Log.d("DroidSpeech", errorMsg);
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            int d = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    private void indentCounter(String[] s){
        int nb;
        for (String word : s) {
            if (isNumeric(word)) {
                nb = Integer.parseInt(word);
                if (nb > counter && nb <= exercise.getCount()) counter = nb;
                count.setText(counter + "/" + exercise.getCount());
                progressBarCircle.setProgress(counter);

                if (counter == exercise.getCount() / 2)
                    tts.speak("Courage ! Vous en êtes à la moitié !", TextToSpeech.QUEUE_ADD, null);
                if (counter == exercise.getCount() - 2)
                    tts.speak("Encore un petit effort !", TextToSpeech.QUEUE_ADD, null);
                if (counter == exercise.getCount()){
                    droidSpeech.closeDroidSpeechOperations();
                    Intent myIntent = new Intent(CountExercise.this, EndExercice.class);
                    myIntent.putExtra("nameExercice", exercise.getName());
                    myIntent.putExtra("nbExercice", exercise.getCount());
                    startActivity(myIntent);
                }
            }
        }
    }
}
