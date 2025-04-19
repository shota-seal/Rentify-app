package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
public class InterestedUsersActivity extends AppCompatActivity {

    private ListView interestedUsersListView;
    private InterestedUsersAdapter adapter;
    private ArrayList<String> interestedUsers;
    private DatabaseReference databaseRef;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested_users);

        // Get data from the intent
        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");
        String itemName = intent.getStringExtra("itemName");
        interestedUsers = intent.getStringArrayListExtra("interestedUsers");

        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("items").child(itemId);

        // Set up the ListView and adapter
        interestedUsersListView = findViewById(R.id.interested_users_list);
        adapter = new InterestedUsersAdapter(this, interestedUsers, (username, isAccept) -> {
            if (isAccept) {
                acceptInterest(username);
            } else {
                denyInterest(username);
            }
        });

        interestedUsersListView.setAdapter(adapter);

        // Display item name in a Toast
        Toast.makeText(this, "Viewing interest for: " + itemName, Toast.LENGTH_SHORT).show();

        // Back button functionality
        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> {
            finish(); // Close the activity and go back to the previous screen
        });
    }

    private void acceptInterest(String username) {
        // Mark the item as on loan to this user
        databaseRef.child("onLoanTo").setValue(username);
        // Mark the item as unavailable
        databaseRef.child("available").setValue(false);
        // Clear the interestedUsers list in Firebase
        databaseRef.child("interestedUsers").removeValue()
                .addOnSuccessListener(aVoid -> {
                    interestedUsers.clear(); // Clear the local list
                    adapter.notifyDataSetChanged(); // Update the UI
                    Toast.makeText(this, username + " has been accepted. All users cleared.", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to clear interested users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void denyInterest(String username) {
        // Remove the user from the local list
        interestedUsers.remove(username);
        // Update the interestedUsers list in Firebase
        databaseRef.child("interestedUsers").setValue(interestedUsers)
                .addOnSuccessListener(aVoid -> {
                    adapter.notifyDataSetChanged(); // Update the UI
                    Toast.makeText(this, username + " has been denied.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to remove user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
