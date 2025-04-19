package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//Data base libraries
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button createAccountButton;

    //database variables
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        createAccountButton = findViewById(R.id.create_account_button);

        Admin.start();

        //Get Database reference
        DatabaseReference theDataBaseRef = database.getReference(getString(R.string.database_user_address));//get the address for the 'users' branch of the database

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, RoleSelectionActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (username.equals("admin")) {
                    Account account = Account.findAccount(username, password);
                    if (account != null) {
                        Intent intent = new Intent(MainActivity.this, AdminAccountActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    String userId = ((Integer)username.hashCode()).toString();
                    theDataBaseRef.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                                Toast.makeText(MainActivity.this, "Invalid username.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                User user = task.getResult().getValue(User.class);
                                String role = task.getResult().child("role").getValue(String.class);
                                //TODO: this is quick fix in the case that a user signs into an accounts that doesnt exist. this will possibly need addressing.
                                if(user == null || role == null){
                                    Toast.makeText(MainActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Account account = UserFactory.createUser(user, role);
                                if (password.equals(account.getPassword())){
                                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                    intent.putExtra("username", account.getUsername());
                                    intent.putExtra("role", account.getRole());
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }

                /*
                Account account = Account.findAccount(username, password);
                if (account != null) {
                    if(account.getRole()=="Admin"){
                        Intent intent = new Intent(MainActivity.this, AdminAccountActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        intent.putExtra("username", account.getUsername());
                        intent.putExtra("role", account.getRole());
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
                */
            }
        });
    }
}
