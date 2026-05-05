package com.example.swiftregister;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText nameEdit, emailEdit, phoneEdit, passwordEdit;
    private MaterialButton registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEdit = findViewById(R.id.name_edit);
        emailEdit = findViewById(R.id.email_edit);
        phoneEdit = findViewById(R.id.phone_edit);
        passwordEdit = findViewById(R.id.password_edit);
        registerBtn = findViewById(R.id.register_btn);

        registerBtn.setOnClickListener(v -> {
            if (validate()) {
                String message = getString(R.string.registration_success) + " " + nameEdit.getText().toString();
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validate() {
        if (nameEdit.getText().toString().isEmpty()) {
            nameEdit.setError("Name is required");
            return false;
        }
        if (emailEdit.getText().toString().isEmpty() || !emailEdit.getText().toString().contains("@")) {
            emailEdit.setError("Valid email is required");
            return false;
        }
        if (passwordEdit.getText().toString().length() < 6) {
            passwordEdit.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }
}
