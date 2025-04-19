package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.database.*;


import java.util.ArrayList;

public class LessorAccountActivity extends AppCompatActivity {
    private ListView itemList;
    private Button addItemButton;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> items;
    private ArrayList<Item> itemListData;
    private DatabaseReference databaseRef;
    private String userID;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessor_account);

        // Retrieve the username from the Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        // Convert the username to a unique hash code for userID
        userID = String.valueOf(username.hashCode());


        itemList = findViewById(R.id.item_list);
        addItemButton = findViewById(R.id.btn_add_item);
        items = new ArrayList<>();
        itemListData = new ArrayList<>();

        // Initialize custom adapter
        ItemAdapter customAdapter = new ItemAdapter(this, itemListData);
        itemList.setAdapter(customAdapter);

        // Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("items");

        // Read user data from Firebase
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemListData.clear(); // Clear the existing data
                for (DataSnapshot data : snapshot.getChildren()) {
                    Item item = data.getValue(Item.class);
                    // Check if the item is available and belongs to the current user
                    if (item != null && item.getAvailable() && userID != null && userID.equals(item.getDatabaseUserID())) {
                        itemListData.add(item); // Add only available items
                    }
                }
                customAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LessorAccountActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });


        // Add item button
        addItemButton.setOnClickListener(view -> {
            if(userID != null){
                Intent addItemIntent = new Intent(LessorAccountActivity.this, AddEditItemActivity.class);
                addItemIntent.putExtra("userID", userID);
                addItemIntent.putExtra("username", username);
                startActivity(addItemIntent);
            }
            else{
                Toast.makeText(this, "User ID is null. Cannot add item", Toast.LENGTH_SHORT).show();
            }

        });

        // Edit or delete item on long press
        itemList.setOnItemLongClickListener((parent, view, position, id) -> {
            Item selectedItem = itemListData.get(position);
            showEditDeleteDialog(selectedItem);
            return true;
        });


    Button logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(view -> {
        // Navigate back to MainActivity (initial screen)
        Intent logoutIntent = new Intent(this, MainActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Prevent back navigation
        startActivity(logoutIntent);
        finish(); // End current activity
    });
        Button viewLeasedItemsButton = findViewById(R.id.btn_view_leased_items);

        viewLeasedItemsButton.setOnClickListener(v -> {
            Intent LeasedItemsIntent = new Intent(LessorAccountActivity.this, LeasedItemsActivity.class);
            LeasedItemsIntent.putExtra("userID", userID); // Pass user ID
            LeasedItemsIntent.putExtra("username", username); // Pass username
            startActivity(LeasedItemsIntent);
        });
    }


    private void showEditDeleteDialog(Item item) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Edit or Delete Item")
                .setMessage("What would you like to do with this item?")
                .setNeutralButton("See Interested Users", (dialog, which) -> {
                    // Handle null case for interestedUsers
                    ArrayList<String> interestedUsers = item.getInterestedUsers() != null
                            ? new ArrayList<>(item.getInterestedUsers())
                            : new ArrayList<>();

                    Intent intent = new Intent(this, InterestedUsersActivity.class);
                    intent.putExtra("itemId", item.getId()); // Pass item ID
                    intent.putExtra("itemName", item.getName()); // Pass item name
                    intent.putExtra("interestedUsers", interestedUsers); // Pass interested users
                    startActivity(intent);
                })
                .setPositiveButton("Edit or Delete", (dialog, which) -> {
                    if (userID != null) {
                        Intent intent = new Intent(this, AddEditItemActivity.class);
                        intent.putExtra("userID", userID);
                        intent.putExtra("username", username);
                        intent.putExtra("itemId", item.getId());
                        intent.putExtra("item", item);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "User ID is null. Cannot edit item", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

}


