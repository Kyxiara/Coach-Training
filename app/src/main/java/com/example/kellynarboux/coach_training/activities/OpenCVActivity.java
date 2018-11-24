package com.example.kellynarboux.coach_training.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.kellynarboux.coach_training.R;

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


public class OpenCVActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OPENCV";
    private static Mat background = null;  // init with the first frame
    private static final double DEFAULT_FILTER = 300.;
    private static final int MIN_FRAME_INTERVAL = 10;
    private static int FRAME_DIFF_REQUIRED = 5;

    // openCV variable
    private static int cmptExercise = 0;
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
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    private void onMovementDone(){
        Runnable r = () -> Toast.makeText(OpenCVActivity.this, cmptExercise + " exercices", Toast.LENGTH_SHORT).show();
        runOnUiThread(r);
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        Mat res = inputFrame.rgba();
        Mat gray = inputFrame.gray();

//        Mat tmp = inputFrame.rgba();
//        Mat res = tmp.t();
//        Core.flip(tmp.t(), res, 1);
//        Imgproc.resize(res, res, res.size());
//        tmp.release();

//        Mat tmp2 = inputFrame.gray();
//        Mat gray = tmp2.t();
//        Core.flip(tmp2.t(), gray, 1);
//        Imgproc.resize(gray, gray, tmp2.size());
//        tmp2.release();


        org.opencv.core.Size s = new Size(21,21);
        Imgproc.GaussianBlur(gray, gray, s, 0);


        if(background == null){
            background = new Mat();
            gray.copyTo(background);
            Log.i(TAG, "background initialised");
        }

        Core.absdiff(background, gray, gray);
        Imgproc.threshold(gray, gray, 25, 255, Imgproc.THRESH_BINARY);
        //Imgproc.dilate(gray, gray, new Mat());  FIXME
        List<MatOfPoint> cnts = new ArrayList<>();
        Imgproc.findContours(gray, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        gray.release();

        int nbDetection = 0;
        double localMinyY = Double.NaN;
        for(MatOfPoint cnt : cnts){
            if(Imgproc.contourArea(cnt) > filter){
                nbDetection++;
                Rect box = Imgproc.boundingRect(cnt);
                Point p1 = new Point(box.x, box.y);
                Point p2 = new Point(box.x + box.width, box.y + box.height);
                Imgproc.rectangle(res, p1, p2, new Scalar(0, 255, 0), 2);
                if (Double.isNaN(localMinyY) || p1.y < localMinyY)
                    localMinyY = p1.y;
            }
        }

        frameDelta++;
        // we process the y value
        if(!Double.isNaN(localMinyY)) {
            if (isGrowing && localMinyY > exerciseYMargin + prevY) {
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
            } else if(!isGrowing && localMinyY + exerciseYMargin < prevY){
                nbrFrameDiffDown = 0;
                nbrFrameDiffUp += 1;
                if(nbrFrameDiffUp >= FRAME_DIFF_REQUIRED){
                    isGrowing = true;
                    if (frameDelta < MIN_FRAME_INTERVAL)
                        exerciseYMargin++;
                    frameDelta = 0;
                    cmptExercise++;
                    Log.i("EXERCISE", "going up ! : " + cmptExercise + " exercise(s)");
                    nbrFrameDiffUp = 0;
                    onMovementDone();
                }
            }
            prevY = localMinyY;
        }

        // on ajuste les paramètres pour la prochaine image
        if(nbDetection> 3){
            filter *= 1.1;
            Log.d("PARAM", "=> set filter to " + filter + " since too many noise (" +
                    nbDetection + " detections)");
        }

        return res;
    }
}
