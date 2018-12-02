package com.example.kellynarboux.coach_training.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.kellynarboux.coach_training.R;
import com.example.kellynarboux.coach_training.model.CountableExercise;
import com.example.kellynarboux.coach_training.model.CountableExerciseType;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class OpenCVActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OPENCV";  // used in toaster
    private static Mat background = null;  // init with the first frame
    private static final double DEFAULT_FILTER = 300.;
    private static final int MIN_FRAME_INTERVAL = 10;
    private static int FRAME_DIFF_REQUIRED = 5;

    CountableExercise exercise;
    private int cmptExercise = 0;
    private TextToSpeech tts;

    // openCV variable
    private static double filter = DEFAULT_FILTER;
    private static boolean isGrowing = false;
    private static double exerciseYMargin = 1.;
    private static double prevY = 0.;
    private static int nbrFrameDiffDown = 0;
    private static int nbrFrameDiffUp = 0;
    private static int frameDelta = 0;

    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public OpenCVActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_open_cv);

        exercise = new CountableExercise(CountableExerciseType.valueOf(getIntent().getStringExtra("myName")),
                getIntent().getIntExtra("myNb", 10));
        tts = new TextToSpeech(OpenCVActivity.this, i ->
                tts.speak("C'est parti pour " + exercise.getCount() + exercise.getName(), TextToSpeech.QUEUE_ADD, null));
        tts.setLanguage(Locale.FRANCE);

        mOpenCvCameraView = findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);


    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        background.release();
        background = null;
        filter = DEFAULT_FILTER;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        if(background != null)
            background.release();
        if(tts != null) {
            tts.stop();
            tts.shutdown();
            Log.d(TAG, "TTS Destroyed");
        }
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    private void onMovementDone(){
        Runnable r = () -> Toast.makeText(OpenCVActivity.this,
                cmptExercise + " exercices", Toast.LENGTH_SHORT).show();
        runOnUiThread(r);
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        Mat res = inputFrame.rgba();  // the image displayed
        Mat gray = inputFrame.gray();

        org.opencv.core.Size s = new Size(21,21);
        Imgproc.GaussianBlur(gray, gray, s, 0);  // blur to avoid artifacts

        // we initialize background with the first frame
        if(background == null){
            background = new Mat();
            gray.copyTo(background);
            Log.i(TAG, "background initialised");
        }

        // we look for differences between the first frame and the current frame
        // and store those differences under rectangles
        Core.absdiff(background, gray, gray);
        Imgproc.threshold(gray, gray, 25, 255, Imgproc.THRESH_BINARY);
        //Imgproc.dilate(gray, gray, new Mat());  FIXME
        List<MatOfPoint> cnts = new ArrayList<>();
        Imgproc.findContours(gray, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        gray.release();

        int nbDetection = 0;
        double localMinY = Double.NaN;
        for(MatOfPoint cnt : cnts){
            if(Imgproc.contourArea(cnt) > filter){
                nbDetection++;
                Rect box = Imgproc.boundingRect(cnt);  // we retrieve the stored rectangle
                Point p1 = new Point(box.x, box.y);
                Point p2 = new Point(box.x + box.width, box.y + box.height);
                // draw the rectangle on the screen and check if it's the highest
                Imgproc.rectangle(res, p1, p2, new Scalar(0, 255, 0), 2);
                if (Double.isNaN(localMinY) || p1.y < localMinY)
                    localMinY = p1.y;
            }
        }

        frameDelta++;
        // we process the y value
        if(!Double.isNaN(localMinY)) {
            if (isGrowing && localMinY > exerciseYMargin + prevY) {
                nbrFrameDiffUp = 0;
                nbrFrameDiffDown += 1;
                if (nbrFrameDiffDown >= FRAME_DIFF_REQUIRED) {
                    isGrowing = false;
                    if (frameDelta < MIN_FRAME_INTERVAL)
                        exerciseYMargin++;
                    frameDelta = 0;
                    Log.i("EXERCISE", "going down");
                    nbrFrameDiffDown = 0;
                }
            } else if(!isGrowing && localMinY + exerciseYMargin < prevY){
                nbrFrameDiffDown = 0;
                nbrFrameDiffUp += 1;
                if(nbrFrameDiffUp >= FRAME_DIFF_REQUIRED){
                    isGrowing = true;
                    if (frameDelta < MIN_FRAME_INTERVAL)
                        exerciseYMargin++;
                    frameDelta = 0;
                    cmptExercise++;

                    // we handle the cmptExercise
                    if (cmptExercise == exercise.getCount() / 2)
                        tts.speak("Courage champion ! Vous en êtes à la moitié !", TextToSpeech.QUEUE_ADD, null);
                    if (cmptExercise == exercise.getCount() - 2)
                        tts.speak("Encore un petit effort !", TextToSpeech.QUEUE_ADD, null);
                    if (cmptExercise == exercise.getCount()){
                        Intent myIntent = new Intent(OpenCVActivity.this, EndExercice.class);
                        myIntent.putExtra("nameExercice", exercise.getName());
                        myIntent.putExtra("nbExercice", exercise.getCount());
                        startActivity(myIntent);
                    }

                    Log.i("EXERCISE", "going up ! : " + cmptExercise + " exercise(s)");
                    nbrFrameDiffUp = 0;
                    onMovementDone();
                }
            }
            prevY = localMinY;
        }

        // we adjust parameters for next frames
        if(nbDetection > 3){
            filter *= 1.1;
            Log.d("PARAM", "=> set filter to " + filter + " since too many noise (" +
                    nbDetection + " detections)");
        }

        return res;
    }
}
