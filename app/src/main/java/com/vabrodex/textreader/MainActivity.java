package com.vabrodex.textreader;
//author: @iamvs-2002
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;


import java.io.File;
import java.io.IOException;
import java.util.List;

//Open CV configured successfully and camera has been connected successfully.

public class MainActivity extends AppCompatActivity
{

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    Button captureimage, detecttext;
    ImageView imageview;
    TextView textview;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        captureimage = findViewById(R.id.capture_image_button);
        detecttext = findViewById(R.id.display_text_button);
        imageview = findViewById(R.id.image_view);
        textview = findViewById(R.id.text_view);

        captureimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                textview.setText("");
            }
        });
        detecttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textRecogfromImage();
            }
        });

    }

    private void textRecogfromImage() {

        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextfromImage(firebaseVisionText);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void displayTextfromImage(FirebaseVisionText firebaseVisionText) {

        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if (blockList.size()==0){
            Toast.makeText(this, "No text found in image!", Toast.LENGTH_SHORT).show();
        }
        else{
            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks())
            {

                String text = block.getText();
                textview.setText(text);
            }
        }

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {//allowing the user to open phone camera and click a picture
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//once that picture is captured by camera, we are getting the data and displaying it in the imageview
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data"); //converting the image to image bitmap
            imageview.setImageBitmap(imageBitmap);
        }
    }

}