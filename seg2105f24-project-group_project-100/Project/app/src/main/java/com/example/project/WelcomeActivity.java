package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeMessage = findViewById(R.id.welcome_message);
        continueButton = findViewById(R.id.continue_button);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String role = intent.getStringExtra("role");

        welcomeMessage.setText("Welcome " + username + "! You are logged in as \"" + role + "\".");

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role.equals("Admin")) {
                    Intent adminIntent = new Intent(WelcomeActivity.this, AdminAccountActivity.class);
                    startActivity(adminIntent);
                } else if (role.equals("Renter")) {
                    Intent renterIntent = new Intent(WelcomeActivity.this, RenterAccountActivity.class);
                    renterIntent.putExtra("username", username);
                    startActivity(renterIntent);
                } else if (role.equals("Lessor")) {
                    Intent lessorIntent = new Intent(WelcomeActivity.this, LessorAccountActivity.class);
                    lessorIntent.putExtra("username", username);
                    startActivity(lessorIntent);
                }
            }
        });
    }
}

