package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;
import android.widget.TextView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;

public class RenterAccountActivity extends AppCompatActivity {

    private ListView itemList;
    private ItemAdapter2 customAdapter;
    private ArrayList<Item> itemListData; // Original unfiltered list
    private ArrayList<Item> filteredList; // Filtered list to display
    private DatabaseReference databaseRef;
    private Button logoutButton;
    private String userID;
    private String username;
    private String selectedCategory = null; // Selected category from the spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_account);

        Spinner categorySpinner = findViewById(R.id.category_spinner);
        SearchView itemSearchBar = findViewById(R.id.item_search_bar);

        // Load categories into Spinner
        loadCategories(categorySpinner);

        // Retrieve the username from the Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        // Convert the username to a unique hash code for userID
        userID = String.valueOf(username.hashCode());

        // Initialize UI components
        itemList = findViewById(R.id.item_list);
        logoutButton = findViewById(R.id.btn_logout);
        Button myRentedItemsButton = findViewById(R.id.btn_my_rented_items);
        itemListData = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Initialize custom adapter
        customAdapter = new ItemAdapter2(this, filteredList); // Use filteredList
        itemList.setAdapter(customAdapter);

        // Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("items");

        // Retrieve available items
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemListData.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Item item = data.getValue(Item.class);
                    // Ensure the item is available
                    if (item != null && item.getAvailable()) {
                        itemListData.add(item);
                    }
                }
                filterItems(); // Apply filters after loading data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RenterAccountActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });

        // Add action to "See My Rented Items" button
        myRentedItemsButton.setOnClickListener(view -> {
            Intent rentedItemsIntent = new Intent(RenterAccountActivity.this, MyRentedItemsActivity.class);
            rentedItemsIntent.putExtra("username", username);
            startActivity(rentedItemsIntent);
        });

        // Logout button
        logoutButton.setOnClickListener(view -> {
            Intent logoutIntent = new Intent(this, MainActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });

        itemList.setOnItemLongClickListener((parent, view, position, id) -> {
            Item selectedItem = filteredList.get(position); // Use filteredList here
            showItemDetails(selectedItem);
            return true;
        });

        // Handle category selection
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();
                selectedCategory = category.equals("All Categories") ? null : category;
                filterItems(); // Reapply filters when the category changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
                filterItems();
            }
        });

        // Handle search input
        itemSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems();
                return true;
            }
        });
    }

    // Move loadCategories method outside onCreate
    private void loadCategories(Spinner categorySpinner) {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("All Categories"); // Option to view all items

        // Create and set adapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Fetch categories from Firebase
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryName = categorySnapshot.child("categorieName").getValue(String.class);
                    if (categoryName != null && !categoryList.contains(categoryName)) {
                        categoryList.add(categoryName);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter that data has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RenterAccountActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterItems() {
        SearchView itemSearchBar = findViewById(R.id.item_search_bar);
        String query = itemSearchBar.getQuery().toString().toLowerCase();

        filteredList.clear();
        for (Item item : itemListData) {
            boolean matchesCategory = selectedCategory == null ||
                    (item.getCategory() != null && item.getCategory().getCategorieName().equals(selectedCategory));
            boolean matchesQuery = item.getName().toLowerCase().contains(query);

            if (matchesCategory && matchesQuery) {
                filteredList.add(item);
            }
        }
        customAdapter.notifyDataSetChanged();
    }

    private void showItemDetails(Item item) {
        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_item_details, null);

        // Initialize views in the custom layout
        TextView descriptionView = dialogView.findViewById(R.id.dialog_item_description);
        TextView feeView = dialogView.findViewById(R.id.dialog_item_fee);
        TextView timePeriodView = dialogView.findViewById(R.id.dialog_item_time_period);
        TextView categoryView = dialogView.findViewById(R.id.dialog_item_category);
        TextView lessorView = dialogView.findViewById(R.id.dialog_item_lessor);
        Button interestedButton = dialogView.findViewById(R.id.dialog_button_interested);
        Button okButton = dialogView.findViewById(R.id.dialog_button_ok);

        // Set data
        descriptionView.setText("Description: " + item.getDescription());
        feeView.setText("Fee: $" + item.getFee());
        timePeriodView.setText("Time Period: " + item.getTimePeriod());
        categoryView.setText("Category: " + (item.getCategory() != null ? item.getCategory().getCategorieName() : "N/A"));
        lessorView.setText("Lessor: " + (item.getLessorName() != null ? item.getLessorName() : "Unknown"));

        // Check if the user is already interested
        boolean isInterested = item.getInterestedUsers() != null && item.getInterestedUsers().contains(username);
        interestedButton.setText(isInterested ? "Not Interested" : "Interested");

        // Create and show the dialog
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Set button click listeners
        interestedButton.setOnClickListener(v -> {
            if (isInterested) {
                // Remove the user from the interested list
                item.removeInterestedUser(username);
            } else {
                // Add the user to the interested list
                item.addInterestedUser(username);
            }

            // Update the item in Firebase
            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("items").child(item.getId());
            itemRef.setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, isInterested ? "You are no longer interested in " + item.getName() : "You are now interested in " + item.getName(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // Close the dialog after successful update
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update interest status.", Toast.LENGTH_SHORT).show();
                    });
        });

        okButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


}
