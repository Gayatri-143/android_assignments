package com.example.datavault;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText fileNameEdit, fileDataEdit;
    private MaterialButton saveBtn, loadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileNameEdit = findViewById(R.id.file_name_edit);
        fileDataEdit = findViewById(R.id.file_data_edit);
        saveBtn = findViewById(R.id.save_btn);
        loadBtn = findViewById(R.id.load_btn);

        saveBtn.setOnClickListener(v -> saveFile());
        loadBtn.setOnClickListener(v -> loadFile());
    }

    private void saveFile() {
        String filename = fileNameEdit.getText().toString();
        String data = fileDataEdit.getText().toString();

        if (filename.isEmpty()) {
            Toast.makeText(this, "Enter a filename", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            FileOutputStream fOut = openFileOutput(filename, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
            Toast.makeText(this, "Saved to " + filename, Toast.LENGTH_SHORT).show();
            fileDataEdit.setText("");
        } catch (Exception e) {
            Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFile() {
        String filename = fileNameEdit.getText().toString();
        if (filename.isEmpty()) {
            Toast.makeText(this, "Enter a filename to load", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            FileInputStream fIn = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fIn);
            char[] inputBuffer = new char[100];
            String s = "";
            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                s += readString;
            }
            isr.close();
            fileDataEdit.setText(s);
            Toast.makeText(this, "Loaded " + filename, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "File not found or unreadable", Toast.LENGTH_SHORT).show();
        }
    }
}
