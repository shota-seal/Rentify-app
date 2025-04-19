package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {

    private RadioGroup roleGroup;
    private RadioButton selectedRoleButton;
    private Button continueButton;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        roleGroup = findViewById(R.id.role_group);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        firstNameInput = findViewById(R.id.firstName_input);
        lastNameInput = findViewById(R.id.lastName_input);
        emailInput = findViewById(R.id.email_input);
        continueButton = findViewById(R.id.continue_button);


        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = roleGroup.getCheckedRadioButtonId();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String email = emailInput.getText().toString();

                if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                    Toast.makeText(RoleSelectionActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RoleSelectionActivity.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }else if (Account.usernameTaken(username)) {
                    Toast.makeText(RoleSelectionActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
                    return;
                } else if (User.emailTaken(email)) {
                    Toast.makeText(RoleSelectionActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedId != -1) {
                    selectedRoleButton = findViewById(selectedId);
                    String selectedRole = selectedRoleButton.getText().toString();

                    Intent intent = new Intent(RoleSelectionActivity.this, AddressActivity.class);
                    intent.putExtra("role", selectedRole);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("firstName", firstName);
                    intent.putExtra("lastName", lastName);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(RoleSelectionActivity.this, "Please select a role", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
