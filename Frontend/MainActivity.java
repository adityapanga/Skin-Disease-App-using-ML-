package com.example.myapplication;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private ImageView previewImage;
    private ProgressBar progressBar;
    private TextView noImageText;
    private ImageView cameraIcon;
    private GridView featuresGrid;
    private Uri imageUri; // for camera result

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Button to open DiseaseInfoActivity
        Button newButton = findViewById(R.id.newButton);
        newButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DiseaseInfoActivity.class);
            startActivity(intent);
        });

        previewImage = findViewById(R.id.previewImage);
        progressBar = findViewById(R.id.progressBar);
        noImageText = findViewById(R.id.noImageText);
        cameraIcon = findViewById(R.id.cameraIcon);
        featuresGrid = findViewById(R.id.featuresGrid);

        // Feature Grid setup
        List<Feature> features = new ArrayList<>();
        features.add(new Feature("Live Camera", R.drawable.ic_camera));
        features.add(new Feature("Gallery Upload", R.drawable.ic_gallery));
        features.add(new Feature("AI Prediction", R.drawable.ic_ai));
        features.add(new Feature("Scan History", R.drawable.ic_history));
        features.add(new Feature("Tips & Info", R.drawable.ic_info));
        features.add(new Feature("Settings", R.drawable.ic_settings));

        FeatureAdapter adapter = new FeatureAdapter(this, features);
        featuresGrid.setAdapter(adapter);

        // Button listeners
        findViewById(R.id.cameraButton).setOnClickListener(v -> checkAndOpenCamera());
        findViewById(R.id.galleryButton).setOnClickListener(v -> openGallery());
        findViewById(R.id.historyButton).setOnClickListener(v -> openHistory());
        findViewById(R.id.settingsButton).setOnClickListener(v -> openSettings());
    }

    // Request camera permission if not granted
    private void checkAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            openCamera();
        }
    }

    // Called after user responds to permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    // Opens the default camera app and stores to gallery
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
    }

    // Opens gallery to choose an image
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void openHistory() {
        startActivity(new Intent(this, HistoryActivity.class));
    }

    private void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    // Handles the result from camera/gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                displayImage(selectedImageUri);
                processImage(selectedImageUri);
            } else if (requestCode == CAMERA_REQUEST && imageUri != null) {
                displayImage(imageUri);
                processImage(imageUri);
            }
        }
    }

    // Displays selected or captured image
    private void displayImage(Uri uri) {
        previewImage.setImageURI(uri);
        previewImage.setVisibility(View.VISIBLE);
        noImageText.setVisibility(View.GONE);
        cameraIcon.setVisibility(View.GONE);
    }

    // Simulates image processing and launches result screen
    private void processImage(Uri uri) {
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);

            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("imageUri", uri.toString());
            intent.putExtra("disease", "Acne");
            intent.putExtra("confidence", 0.92);
            intent.putExtra("description", "Common skin condition when hair follicles plug with oil and dead skin cells.");
            startActivity(intent);
        }, 2000);
    }
}

// --- Supporting Classes ---

class Feature {
    String name;
    int iconRes;

    public Feature(String name, int iconRes) {
        this.name = name;
        this.iconRes = iconRes;
    }
}

class FeatureAdapter extends ArrayAdapter<Feature> {
    public FeatureAdapter(Context context, List<Feature> features) {
        super(context, 0, features);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_feature, parent, false);
        }

        Feature feature = getItem(position);

        ImageView icon = convertView.findViewById(R.id.featureIcon);
        TextView name = convertView.findViewById(R.id.featureName);

        icon.setImageResource(feature.iconRes);
        name.setText(feature.name);

        return convertView;
    }
}
