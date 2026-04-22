package com.example.implicitintent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editTextUrl = findViewById(R.id.editTextUrl);
        final Button buttonRedirect = findViewById(R.id.buttonRedirect);

        if (buttonRedirect != null) {
            buttonRedirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextUrl == null) {
                        Toast.makeText(MainActivity.this, "EditText not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Object textObj = editTextUrl.getText();
                    String url = (textObj != null) ? textObj.toString() : "";
                    
                    if (url == null) url = "";
                    url = url.trim();

                    if (!url.isEmpty()) {
                        // Ensure the URL has a protocol
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            url = "https://" + url;
                        }

                        try {
                            Uri uri = Uri.parse(url);
                            if (uri != null) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid URL format", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            String errorMsg = (e != null && e.getMessage() != null) ? e.getMessage() : "Unknown error";
                            Toast.makeText(MainActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter a URL", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
