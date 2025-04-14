package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    ImageView resultImage;
    TextView resultDisease, resultConfidence, resultDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultImage = findViewById(R.id.resultImage);
        resultDisease = findViewById(R.id.resultDisease);
        resultConfidence = findViewById(R.id.resultConfidence);
        resultDescription = findViewById(R.id.resultDescription);

        // Get data from intent
        Intent intent = getIntent();
        String imageUriStr = intent.getStringExtra("imageUri");
        String disease = intent.getStringExtra("disease");
        double confidence = intent.getDoubleExtra("confidence", 0.0);
        String description = intent.getStringExtra("description");

        if (imageUriStr != null) {
            Uri imageUri = Uri.parse(imageUriStr);
            resultImage.setImageURI(imageUri);
        }

        resultDisease.setText("Disease: " + disease);
        resultConfidence.setText("Confidence: " + (int)(confidence * 100) + "%");
        resultDescription.setText(description);
    }
}
