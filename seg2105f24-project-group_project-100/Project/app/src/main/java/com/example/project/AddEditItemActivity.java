package com.example.project;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.util.Calendar;

import java.util.ArrayList;

public class AddEditItemActivity extends AppCompatActivity {
    private EditText itemName, itemFee, itemDescription;
    private TextView periodFromTextView, periodToTextView,itemTimePeriod;
    private Button confirmButton;
    private Spinner categorySpinner;

    private DatabaseReference databaseRef;
    private String itemId;
    private String userID;
    private String username;
    private Categorie selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        // Getting username from the previous intent
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        username = intent.getStringExtra("username");

        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("items");

        // Initialize UI elements
        itemName = findViewById(R.id.item_name);
        itemFee = findViewById(R.id.item_fee);
        itemTimePeriod = findViewById(R.id.item_time_period);
        itemDescription = findViewById(R.id.item_description);
        confirmButton = findViewById(R.id.btn_confirm);
        periodFromTextView = findViewById(R.id.periodFromTextView);
        periodToTextView = findViewById(R.id.periodToTextView);
        categorySpinner = findViewById(R.id.category_spinner);
        Button deleteButton = findViewById(R.id.btn_delete_item); // Delete button

        // Load categories into the spinner
        loadCategories();

        // Check if an item is passed via Intent
        if (intent.hasExtra("itemId")) {
            itemId = intent.getStringExtra("itemId");
            Item item = (Item) intent.getSerializableExtra("item");
            if (item != null) {
                // Populate fields with the item's data
                itemName.setText(item.getName());
                itemFee.setText(String.format(Locale.getDefault(), "%.2f", item.getFee()));
                itemTimePeriod.setText(item.getTimePeriod());
                itemDescription.setText(item.getDescription());
                String[] dates = item.getTimePeriod().split(" - ");
                periodFromTextView.setText("From: " + dates[0]);
                periodToTextView.setText("To: " + dates[1]);

                // Pre-select the category in the spinner
                categorySpinner.post(() -> {
                    for (int i = 0; i < categorySpinner.getAdapter().getCount(); i++) {
                        Categorie category = (Categorie) categorySpinner.getAdapter().getItem(i);
                        if (category.getId().equals(item.getCategory().getId())) {
                            categorySpinner.setSelection(i);
                            break;
                        }
                    }
                });

                // Show the delete button
                deleteButton.setVisibility(View.VISIBLE);

                // Set delete button action
                deleteButton.setOnClickListener(v -> {
                    // Confirm delete
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Delete Item")
                            .setMessage("Are you sure you want to delete this item?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Remove item from Firebase
                                databaseRef.child(itemId).removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Item deleted successfully.", Toast.LENGTH_SHORT).show();
                                            finish(); // Close the activity
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to delete item.", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .setNegativeButton("No", null)
                            .show();
                });
            }
        }

        confirmButton.setOnClickListener(view -> {
            String name = itemName.getText().toString().trim();
            String feeInput = itemFee.getText().toString().trim();
            String fromPeriod = periodFromTextView.getText().toString().trim().replace("From: ", "");
            String toPeriod = periodToTextView.getText().toString().trim().replace("To: ", "");
            String timePeriod = fromPeriod + " - " + toPeriod;
            String description = itemDescription.getText().toString().trim();
            Categorie selectedCategory = (Categorie) categorySpinner.getSelectedItem();

            // Validate inputs
            if (name.isEmpty() || feeInput.isEmpty() || fromPeriod.isEmpty() || toPeriod.isEmpty() || description.isEmpty() || selectedCategory == null) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            float fee;
            try {
                fee = Float.parseFloat(feeInput);
                fee = Math.round(fee * 100) / 100f; // Round to the nearest hundredth
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Fee must be a valid number.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate date range
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                dateFormat.setLenient(false);

                Date startDate = dateFormat.parse(fromPeriod);
                Date endDate = dateFormat.parse(toPeriod);

                if (startDate == null || endDate == null || !endDate.after(startDate)) {
                    Toast.makeText(this, "End date must be later than the start date.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(this, "Invalid date format. Please use yyyy/MM/dd.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prepare item for saving
            Item newItem;
            boolean fieldsChanged = true;

            if (itemId == null) { // New item
                String newId = databaseRef.push().getKey();
                newItem = new Item(newId, name, fee, timePeriod, description, username, userID, selectedCategory);
            } else { // Update existing item
                Item oldItem = (Item) intent.getSerializableExtra("item");
                newItem = new Item(itemId, name, fee, timePeriod, description, username, userID, selectedCategory);

                // Check if fields have changed
                fieldsChanged = !name.equals(oldItem.getName()) ||
                        fee != oldItem.getFee() ||
                        !timePeriod.equals(oldItem.getTimePeriod()) ||
                        !description.equals(oldItem.getDescription()) ||
                        !selectedCategory.getId().equals(oldItem.getCategory().getId());

                // Preserve the interestedUsers list if no fields have changed
                if (!fieldsChanged) {
                    newItem.setInterestedUsers(oldItem.getInterestedUsers());
                }
            }

            // Save item to Firebase
            databaseRef.child(newItem.getId()).setValue(newItem)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show();

                        // Return to the previous activity
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "ItemSaved");
                        setResult(RESULT_OK, returnIntent);
                        finish(); // Close this activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });


        periodToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(periodToTextView, "To: ");
            }
        });

        periodFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(periodFromTextView, "From: ");
            }
        });

    }
    private void openDialog(TextView timeDisplay, String prefix) {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Note: Month is 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog with today's date as the default
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++; // Adjust for 0-based month
                String setText = prefix + year + "/" + month + "/" + dayOfMonth;
                timeDisplay.setText(setText);
            }
        }, year, month, day);

        dialog.show();
    }

    private void loadCategories() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
        ArrayList<Categorie> categoryList = new ArrayList<>();
        ArrayAdapter<Categorie> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Categorie category = categorySnapshot.getValue(Categorie.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                adapter.notifyDataSetChanged();

                // Ensure the spinner pre-selects the correct category if editing an item
                if (itemId != null) {
                    Intent intent = getIntent();
                    Item item = (Item) intent.getSerializableExtra("item");
                    if (item != null) {
                        for (int i = 0; i < categoryList.size(); i++) {
                            if (categoryList.get(i).getId().equals(item.getCategory().getId())) {
                                categorySpinner.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AddEditItemActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (Categorie) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });
    }
}
