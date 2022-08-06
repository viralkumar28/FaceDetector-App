package com.example.facedetector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
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

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_IMAGE_CAPTURE = 300;
    Button Camera;
    InputImage image;
    FaceDetector detector;


    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        Camera = findViewById(R.id.camera_btn);
        Camera.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(MainActivity.this,"Camera Not Working",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK || requestCode == 121) {

            Bundle extra = data.getExtras();
            Bitmap bitmap = (Bitmap) extra.get("data");
            detectFace(bitmap);
        }
    }

    private  void detectFace(Bitmap bitmap) {
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .enableTracking()
                        .build();

        try {
            image = InputImage.fromBitmap(bitmap,0);
            detector = com.google.mlkit.vision.face.FaceDetection.getClient(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        detector.process(image).addOnSuccessListener(faces -> {
            String resultText = "";
            int i = 0;
            DecimalFormat df = new DecimalFormat("#.00");
            for (Face face:faces) {

//                resultText = resultText.concat("\n\nFACE NUMBER  " + i)
//                        .concat(
//                                "\nSmile:   "
//                                        + (df.format(face.getSmilingProbability()
//                                        * 100))
//                                        + "%")
//                        .concat(
//                                "\nLeft Eye:  "
//                                        + (df.format(face.getLeftEyeOpenProbability()
//                                        * 100))
//                                        + "%")
//                        .concat(
//                                "\nRight Eye:  "
//                                        + (df.format(face.getRightEyeOpenProbability()
//                                        * 100))
//                                        + "%");
                i++;
            }
            resultText = resultText.concat("\n\n"+i+" Faces Detected");
            if (faces.size() == 0) {
                Toast.makeText(MainActivity.this, "No Faces Are Detected", Toast.LENGTH_LONG).show();


            }else {
                Bundle bundle = new Bundle();
                bundle.putString(FaceDetection.RESULT_TEXT,resultText);
                DialogFragment resultDialog = new resultdialog();
                resultDialog.setArguments(bundle);
                resultDialog.setCancelable(false);
                resultDialog.show(getSupportFragmentManager(), FaceDetection.RESULT_DIALOG);
            }
        });
    }


}