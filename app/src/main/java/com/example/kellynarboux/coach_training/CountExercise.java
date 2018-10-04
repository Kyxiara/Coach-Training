package com.example.kellynarboux.coach_training;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kellynarboux.coach_training.model.CountableExercise;
import com.example.kellynarboux.coach_training.model.CountableExerciseType;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountExercise extends AppCompatActivity {

    TextView count, name;
    CountableExercise exercise;
    int counter = 0;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening;
    private ProgressBar progressBarCircle;

    private Integer original_volume_level;
    private AudioManager audioManager;

    private static final Pattern p = Pattern.compile("(\\D*(\\d+)\\D*)*");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_exercise);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        original_volume_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);

        mIslistening = false;

        exercise = new CountableExercise(CountableExerciseType.valueOf(getIntent().getStringExtra("myName")), getIntent().getIntExtra("myNb", 10));

        count = findViewById(R.id.id_count);
        name = findViewById(R.id.id_nameEx);
        count.setText(counter + "/" + exercise.getCount());
        name.setText(exercise.getName());

        progressBarCircle = findViewById(R.id.progressBarCircle);
        progressBarCircle.setMax(exercise.getCount());
        progressBarCircle.setProgress(counter);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());

        if (!mIslistening)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }

        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
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
        for (int i = 0; i < s.length; i++){
            if (isNumeric(s[i])){
                nb = Integer.parseInt(s[i]);
                if (nb > counter && nb <= exercise.getCount()) counter = nb;
                count.setText(counter + "/" + exercise.getCount());
                progressBarCircle.setProgress(counter);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, original_volume_level, 0);

        super.onDestroy();
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            Log.d("TAG", "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            Log.d("TAG", "onEndOfSpeech");
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }

        @Override
        public void onError(int error)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            //Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Log.d("TAG", "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> s = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Log.d("TAG", s.get(0));
            Log.d("Nb", String.valueOf(s.size()));
            /*Matcher m = p.matcher(s.get(0));
            if(m.matches()){
                Log.d("Regex", m.group(0));
            }*/
            String[] sentence = s.get(0).split(" ");
            for (int i = 0; i < sentence.length; i++) Log.d("Mot", sentence[i]);
            indentCounter(sentence);
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }
}
