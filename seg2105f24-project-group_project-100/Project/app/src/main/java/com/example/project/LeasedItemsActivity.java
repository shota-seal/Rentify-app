package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LeasedItemsActivity extends AppCompatActivity {

    private ListView leasedItemsListView;
    private ItemAdapter3 customAdapter;
    private ArrayList<Item> leasedItemsData;
    private DatabaseReference databaseRef;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leased_items);

        // Retrieve user ID from the Intent
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        // Initialize the ListView and data
        leasedItemsListView = findViewById(R.id.leased_items_list);
        leasedItemsData = new ArrayList<>();
        customAdapter = new ItemAdapter3(this, leasedItemsData);
        leasedItemsListView.setAdapter(customAdapter);

        // Back button functionality
        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(view -> finish());

        // Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("items");

        // Fetch leased items
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leasedItemsData.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Item item = data.getValue(Item.class);
                    if (item != null && !item.getAvailable() && userID.equals(item.getDatabaseUserID())) {
                        leasedItemsData.add(item);
                    }
                }
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LeasedItemsActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle item long click to show dialog
        leasedItemsListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Item selectedItem = leasedItemsData.get(position);
            showReturnDialog(selectedItem);
            return true;
        });
    }

    private void showReturnDialog(Item item) {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_got_returned, null);

        // Initialize dialog buttons
        Button gotReturnedButton = dialogView.findViewById(R.id.dialog_button_returned);
        Button cancelButton = dialogView.findViewById(R.id.dialog_button_cancel);

        // Create and show dialog
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Set "Got Returned" button functionality
        gotReturnedButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, AddEditItemActivity.class);
            intent.putExtra("itemId", item.getId());
            intent.putExtra("item", item);
            intent.putExtra("userID", userID);
            intent.putExtra("username", item.getLessorName());
            startActivity(intent);
        });

        // Set "Cancel" button functionality
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
