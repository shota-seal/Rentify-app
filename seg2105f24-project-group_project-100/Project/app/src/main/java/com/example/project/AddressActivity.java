package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
//Data base libraries
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddressActivity extends AppCompatActivity {

    private EditText apartmentNumInput, streetNumInput, cityInput, postalCodeInput;
    private Button saveAddressButton;

    //database variables
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        DatabaseReference theDataBaseRef = database.getReference(getString(R.string.database_user_address));//get the address for the 'users' branch of the

        apartmentNumInput = findViewById(R.id.apartmentNum);
        streetNumInput = findViewById(R.id.streetNum);
        cityInput = findViewById(R.id.city);
        postalCodeInput = findViewById(R.id.postalCode);

        Intent intent = getIntent();
        String role = intent.getStringExtra("role");
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String email = intent.getStringExtra("email");

        saveAddressButton = findViewById(R.id.save_address_button);
        saveAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String apartmentNum = apartmentNumInput.getText().toString();
                String streetNum = streetNumInput.getText().toString();
                String city = cityInput.getText().toString();
                String postalCode = postalCodeInput.getText().toString();

                postalCode = postalCode.replaceAll("\\s", "");

                if (streetNum.isEmpty() || city.isEmpty() || postalCode.isEmpty()) {
                    Toast.makeText(AddressActivity.this, "Please fill in all necessary fields", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!Address.isValidCanadianPostalCode(postalCode)) {
                    Toast.makeText(AddressActivity.this, "Please enter a valid postal code", Toast.LENGTH_SHORT).show();
                    return;
                }

                Address address = new Address(apartmentNum, streetNum, city, postalCode);

                if (role.equals("Lessor")) {
                    String id = ((Integer) username.hashCode()).toString();
                    Lessor lessor = new Lessor(username, password,id, firstName, lastName, email, address);
                    Account.addAccount(lessor);
                    //TODO: Check if there is already a child node at this location, if so, create a toast saying that username is already taken
                    theDataBaseRef.child(id).setValue(lessor);
                } else{
                    String id = ((Integer) username.hashCode()).toString();
                    Renter renter = new Renter(username, password,id, firstName, lastName, email, address);
                    Account.addAccount(renter);
                    //TODO: Check if there is already a child node at this location, if so, create a toast saying that username is already taken
                    theDataBaseRef.child(id).setValue(renter);
                }

                Toast.makeText(AddressActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AddressActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

