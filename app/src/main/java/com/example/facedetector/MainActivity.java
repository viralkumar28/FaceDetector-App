package com.example.facedetector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button cameraButton;

    //Whenever using your own custom permissions then we need to use a flg value(which is an integer) to have a check
    //whether the user has granted our pp that permission or not
    private final static int REQUEST_IMAGE_CAPTURE = 124;
    private InputImage image;
    private FaceDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        cameraButton = findViewById(R.id.camera_button);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                //Checking if the user has given correct permission to our app
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Checking requestCode if it matches with our app's requestCode or not coz there might be other apps which may use
        // camera too and also checking resultCode coz it might be possible that the user just opens and closes the app without
        // clicking the open camera button
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            //Using bundle coz the image captured will be sent in bitmap format in the vision library and as bitmap is a
            // collection of data containing many strings,ints etc we need to bind that data together using bundle
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            detectFace(bitmap);
        }
    }

    private void detectFace(Bitmap bitmap) {
        // High-accuracy landmark detection and face classification
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setMinFaceSize(0.15f)
                        .enableTracking()
                        .build();

        try {
            //To create an InputImage object from a Bitmap object
            //The image is represented by a Bitmap object together with rotation degrees.
            image = InputImage.fromBitmap(bitmap,0);

            //Get an instance of FaceDetector
            detector = com.google.mlkit.vision.face.FaceDetection.getClient(highAccuracyOpts);
        } catch (Exception e) {
            e.printStackTrace();
        }

        detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(List<Face> faces) {
                String resultText = "";

                //Integer i if there are more than 1 faces in the List
                int i = 1;

                //Looping thru the list named faces using for each loop
                for (Face face:faces) {

//                            .concat("\nSmile: "+face.getSmilingProbability()*100+"%")
//                            .concat("\nLeft Eye: "+face.getLeftEyeOpenProbability()*100+"%")
//                            .concat("\nRight Eye: "+face.getRightEyeOpenProbability()*100+"%");
                    i++;
                }
                resultText = resultText.concat("\n"+i+" Faces Detected");

                if (faces.size() == 0)
                    Toast.makeText(MainActivity.this, "NO FACES", Toast.LENGTH_SHORT).show();
                else{
                    Bundle bundle = new Bundle();
                    bundle.putString(FaceDetection.RESULT_TEXT,resultText);
                    DialogFragment resultDialog = new ResultDialog();
                    resultDialog.setArguments(bundle);
                    resultDialog.setCancelable(false);
                    resultDialog.show(getSupportFragmentManager(), FaceDetection.RESULT_DIALOG);
                }

            }
        });

    }
}