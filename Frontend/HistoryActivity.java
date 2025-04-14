package com.example.myapplication;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyList;
    private View emptyView;
    private List<HistoryItem> historyItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        historyList = findViewById(R.id.historyList);
        emptyView = findViewById(R.id.emptyView);
        ImageView deleteButton = findViewById(R.id.deleteButton);

        // Load history items
        loadHistoryItems();

        // Set adapter
        HistoryAdapter adapter = new HistoryAdapter(this, historyItems);
        historyList.setAdapter(adapter);
        historyList.setEmptyView(emptyView);

        // Delete button click listener
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void loadHistoryItems() {
        // Sample data - in real app, load from database or SharedPreferences
        historyItems.add(new HistoryItem("Acne", 0.92, "2023-05-15T10:30:00", "content://image1"));
        historyItems.add(new HistoryItem("Eczema", 0.88, "2023-05-14T15:45:00", "content://image2"));
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Clear History")
                .setMessage("Are you sure you want to delete all scan history?")
                .setPositiveButton("Clear", (dialog, which) -> clearHistory())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearHistory() {
        historyItems.clear();
        ((HistoryAdapter) historyList.getAdapter()).notifyDataSetChanged();
        Snackbar.make(findViewById(android.R.id.content), "History cleared", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    static class HistoryItem {
        String disease;
        double confidence;
        String date;
        String imageUri;

        HistoryItem(String disease, double confidence, String date, String imageUri) {
            this.disease = disease;
            this.confidence = confidence;
            this.date = date;
            this.imageUri = imageUri;
        }
    }

    static class HistoryAdapter extends ArrayAdapter<HistoryItem> {
        HistoryAdapter(Context context, List<HistoryItem> items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_history, parent, false);
            }

            HistoryItem item = getItem(position);
            if (item == null) return convertView;

            ImageView imageView = convertView.findViewById(R.id.historyImage);
            TextView diseaseText = convertView.findViewById(R.id.diseaseText);
            TextView confidenceText = convertView.findViewById(R.id.confidenceText);
            TextView dateText = convertView.findViewById(R.id.dateText);

            // Load image - in real app use Glide/Picasso
            try {
                imageView.setImageURI(Uri.parse(item.imageUri));
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.ic_placeholder);
            }

            diseaseText.setText(item.disease);
            confidenceText.setText(String.format(Locale.getDefault(),
                    "Confidence: %.1f%%", item.confidence * 100));
            dateText.setText(formatDate(item.date));

            return convertView;
        }

        private String formatDate(String isoDate) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy - hh:mm a", Locale.getDefault());
                Date date = inputFormat.parse(isoDate);
                return outputFormat.format(date);
            } catch (ParseException e) {
                return "Unknown date";
            }
        }
    }
}