package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminAccountActivity extends AppCompatActivity {

    private ListView userListView;
    private Button toCategoriesButton;
    private DatabaseReference userDatabase;
    private Button logoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_account);

        userDatabase = FirebaseDatabase.getInstance().getReference(getString(R.string.database_user_address));//get the address for the 'users' branch of the database

        userListView = findViewById(R.id.user_list_view);
        toCategoriesButton = findViewById(R.id.to_categories_button);
        logoutButton = findViewById(R.id.btn_logout);

        List<Account> allAccounts = Account.getAccounts();
        List<String> userDetails = new ArrayList<>();


        //Check for changes to the database, and update those changes on the users list
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clearing the previous user list
                userDetails.clear();
                allAccounts.clear();

                //iterating through all the nodes
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    //getting accounts
                    Account account = userSnapshot.getValue(Account.class);
                    String role = userSnapshot.child("role").getValue(String.class);//See note 1 at the bottom of file for why we have the role set up this way
                     userDetails.add("Username: " + account.getUsername() + "\n"+"Role: " + role + ",  IsDisabled: " + !account.getEnabled());
                    allAccounts.add(account);
                }
                //creating adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminAccountActivity.this, android.R.layout.simple_list_item_1, userDetails);
                userListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Logout button
        logoutButton.setOnClickListener(view -> {
            Intent logoutIntent = new Intent(this, MainActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });

        //Navigate to Categories Activity
        toCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adminIntent = new Intent(AdminAccountActivity.this, AdminCategoriesActivity.class);
                startActivity(adminIntent);
            }
        });

        //ListView onLonClick
        userListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDisableDeleteDialog(allAccounts.get(i));// doing i and not i++ as before
                return true;
            }
        });
    }
    private void showDisableDeleteDialog(Account account) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_admin_update_users, null);
        dialogBuilder.setView(dialogView);
        final TextView textViewEnabledStatusDisplay  = (TextView) dialogView.findViewById(R.id.textViewEnabledStatusDisplay);
        final Button buttonDisable = (Button) dialogView.findViewById(R.id.buttonDisableUser);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteUser);

        dialogBuilder.setTitle(account.getUsername());
        final AlertDialog b = dialogBuilder.create();
        b.show();


        // Retrieve the current description from the database to prefill the description field
        userDatabase.child(account.getID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Account account = dataSnapshot.getValue(Account.class);
                if (account != null) {
                    textViewEnabledStatusDisplay.setText(account.getEnabled() ? "User is not disabled" : "User is disabled");
                    buttonDisable.setText(account.getEnabled() ? "Disable account" : "Enable account");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                handleDatabaseError(databaseError); // Reuse the error handling method
            }
        });

        buttonDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = account.getUsername();
                if (!TextUtils.isEmpty(name)) {
                    account.setEnable(!account.getEnabled());
                    updateUserEnabled(account.getID(), account.getEnabled() );
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(account.getID());
                b.dismiss();
            }
        });
    }

    private void deleteUser(String id) {
        userDatabase.child(id).removeValue();
        Toast.makeText(getApplicationContext(), "User Deleted", Toast.LENGTH_LONG).show();
    }

    private void updateUserEnabled(String id, boolean isEnabled){
        userDatabase.child(id).child("enabled").setValue(isEnabled);
        Toast.makeText(getApplicationContext(), "User Enable Toggled", Toast.LENGTH_LONG).show();
    }



    // Reusable error handling method
    private void handleDatabaseError(DatabaseError databaseError) {
        Toast.makeText(this, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
    }

}
/*
Notes
1.  The role is read directly from the database instead of the getRole() method because when the information is read from the database, it is
    casted to the Account class. That means when we call the getRole() method, it calls it in the Account Class, not the Rentor or Lessor class.
    For this reason, the role is read directly from the database. This could potentially be fixed using a Factory class if needed.
 */

