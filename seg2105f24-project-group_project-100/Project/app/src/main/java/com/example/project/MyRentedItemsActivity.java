package com.example.project;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyRentedItemsActivity extends AppCompatActivity {

    private ListView rentedItemsListView;
    private ItemAdapter2 customAdapter;
    private ArrayList<Item> rentedItemsData;
    private DatabaseReference databaseRef;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rented_items);

        // Retrieve the username from the intent
        username = getIntent().getStringExtra("username");

        // Initialize UI components
        rentedItemsListView = findViewById(R.id.rented_items_list);
        Button backButton = findViewById(R.id.btn_back); // Back button
        rentedItemsData = new ArrayList<>();

        // Initialize the adapter
        customAdapter = new ItemAdapter2(this, rentedItemsData);
        rentedItemsListView.setAdapter(customAdapter);

        // Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("items");

        // Fetch rented items
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rentedItemsData.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Item item = data.getValue(Item.class);
                    // Check if the item is rented to the current user
                    if (item != null && username.equals(item.getOnLoanTo())) {
                        rentedItemsData.add(item);
                    }
                }
                customAdapter.notifyDataSetChanged(); // Notify the adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyRentedItemsActivity.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle back button click
        backButton.setOnClickListener(v -> finish());
    }
}
