package com.example.controlhub;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private MaterialButton updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ratingBar = findViewById(R.id.rating_bar);
        progressBar = findViewById(R.id.progress_bar);
        updateBtn = findViewById(R.id.update_btn);

        updateBtn.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            int currentProgress = progressBar.getProgress();
            
            if (currentProgress < 100) {
                progressBar.setProgress(currentProgress + 10);
            } else {
                progressBar.setProgress(0);
            }

            Toast.makeText(this, "Rating: " + rating + " | Progress: " + progressBar.getProgress() + "%", Toast.LENGTH_SHORT).show();
        });
    }
}
