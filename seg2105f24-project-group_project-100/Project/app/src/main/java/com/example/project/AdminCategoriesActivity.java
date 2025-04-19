package com.example.project;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class AdminCategoriesActivity extends AppCompatActivity {

    EditText editTextName;
    EditText editTextDescription;
    Button buttonAddCategorie;
    ListView listViewCategories;
    Button backToAccounts;

    List<Categorie> categories;
    DatabaseReference databaseCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        databaseCategories = FirebaseDatabase.getInstance().getReference(getString(R.string.database_categories_address));//get the address for the 'categories' branch of the database
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_categories);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        listViewCategories = (ListView) findViewById(R.id.listViewCategories);
        buttonAddCategorie = (Button) findViewById(R.id.addButton);
        backToAccounts = findViewById(R.id.back_to_accounts_button);

        categories = new ArrayList<>();

        backToAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent adminIntent = new Intent(AdminCategoriesActivity.this, AdminAccountActivity.class);
                startActivity(adminIntent);
            }
        });

        //adding an onclicklistener to button
        buttonAddCategorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategorie();
            }
        });

        //When an item in the list is long clicked
        listViewCategories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Categorie categorie = categories.get(i);
                showUpdateDeleteDialog(categorie.getId(), categorie.getCategorieName());
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check when values in the database are changed
        databaseCategories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categories.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Categorie categorie = postSnapshot.getValue(Categorie.class);
                    categories.add(categorie);
                }

                CategorieList categoriesAdapter = new CategorieList(AdminCategoriesActivity.this, categories);
                listViewCategories.setAdapter(categoriesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showUpdateDeleteDialog(final String categorieId, String categorieName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_admin_update_categories, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final EditText editTextDescription  = (EditText) dialogView.findViewById(R.id.editTextDescription);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonDisableUser);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteUser);

        dialogBuilder.setTitle(categorieName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        // Prefill the EditText fields with current category data
        editTextName.setText(categorieName);

        // Retrieve the current description from the database to prefill the description field
        databaseCategories.child(categorieId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Categorie categorie = dataSnapshot.getValue(Categorie.class);
                if (categorie != null) {
                    editTextDescription.setText(categorie.getDescription());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                handleDatabaseError(databaseError); // Reuse the error handling method
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    updateCategorie(categorieId, name, description);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCategorie(categorieId);
                b.dismiss();
            }
        });
    }


    private void updateCategorie(String id, String name, String description) {
        // Check if name and description are provided
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_LONG).show();
            return; // Exit the method if name is empty
        }


        // Check for existing categories with the same name, except the current category being updated
        databaseCategories.orderByChild("categorieName").equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean exists = false;

                        // Check if there is another category with the same name
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Categorie existingCategorie = snapshot.getValue(Categorie.class);
                            if (existingCategorie != null && !existingCategorie.getId().equals(id)) {
                                exists = true;
                                break;
                            }
                        }

                        if (exists) {
                            // Another category with this name already exists
                            Toast.makeText(AdminCategoriesActivity.this,
                                    "Category with this name already exists",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if (TextUtils.isEmpty(description)) {
                                Toast.makeText(AdminCategoriesActivity.this, "Please enter a description", Toast.LENGTH_LONG).show();
                                return; // Exit the method if description is empty
                            }
                            // Update the category in the database
                            DatabaseReference dR = FirebaseDatabase.getInstance().getReference(getString(R.string.database_categories_address)).child(id);
                            Categorie categorie = new Categorie(id, name, description);
                            dR.setValue(categorie);
                            Toast.makeText(getApplicationContext(), "Category Updated", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        handleDatabaseError(databaseError); // Reuse the error handling method
                    }
                });
    }


    private void deleteCategorie(String id) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference(getString(R.string.database_categories_address)).child(id);
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Categorie Deleted", Toast.LENGTH_LONG).show();
    }

    private void addCategorie() {
        // Getting the values to save
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        // Checking if the value is provided
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_LONG).show();
            return; // Exit the method if name is empty
        }


        // Check for existing categories with the same name
        databaseCategories.orderByChild("categorieName").equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // A category with this name already exists
                            Toast.makeText(AdminCategoriesActivity.this,
                                    "Category with this name already exists",
                                    Toast.LENGTH_LONG).show();
                        } else {

                            if (TextUtils.isEmpty(description)) {
                                Toast.makeText(AdminCategoriesActivity.this, "Please enter a description", Toast.LENGTH_LONG).show();
                                return; // Exit the method if description is empty
                            }
                            // Getting a unique id using push().getKey() method
                            String id = databaseCategories.push().getKey();

                            // Creating a Categorie Object
                            Categorie categorie = new Categorie(id, name, description);

                            // Saving the Categorie
                            databaseCategories.child(id).setValue(categorie);

                            // Setting editText to blank again
                            editTextName.setText("");
                            editTextDescription.setText("");

                            // Displaying a success toast
                            Toast.makeText(AdminCategoriesActivity.this, "Category added", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        handleDatabaseError(databaseError); // Reuse the error handling method
                    }
                });
    }

    // Reusable error handling method
    private void handleDatabaseError(DatabaseError databaseError) {
        Toast.makeText(this, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
    }


}

