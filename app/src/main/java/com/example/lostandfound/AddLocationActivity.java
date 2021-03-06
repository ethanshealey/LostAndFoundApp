package com.example.lostandfound;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * AddLocationView
 *
 * This activity handles the add location page, where the
 * user can enter in the `Name`, `Coords`, and `Details`
 * of a location and add it to the map
 *
 * Required attributes:
 *  - name: Must not be left empty
 *  - coords: Must be valid coordinates. The pattern goes as
 *            follows: Can be started with -, must contains atleast
 *            one number, a period, atleast one more number, then a
 *            comma and optional whitespace followed by another optional
 *            -, atleast one number, period, then atleast one last number
 *  - details: No requirements
 */

public class AddLocationActivity extends AppCompatActivity {

    // define widgets
    Intent intent;
    Button back;
    Button addLocation;
    TextView locName;
    TextView locCoords;
    TextView locDetails;

    // init firestore db
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        // init widgets
        locName = (TextView) findViewById(R.id.searchQuery);
        locCoords = (TextView) findViewById(R.id.loc_location);
        locDetails = (TextView) findViewById(R.id.loc_details);
        back = (Button) findViewById(R.id.backBtn);
        addLocation = (Button) findViewById(R.id.searchBtn);

        // listen for click on `Back` button
        // leads to -> MapsActivity
        back.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
        });

        // init list of location names
        List<String> locationNames = new ArrayList<>();

        // get all location names from the db
        db.collection("Locations").get().addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               for(QueryDocumentSnapshot doc: task.getResult()) {
                   locationNames.add(doc.getData().get("name").toString());
               }
           }
        });

        // listen for click on `Add Location` button
        // leads to -> MapActivity
        addLocation.setOnClickListener(v -> {

            // validate name
            if(locName.getText().toString().matches("")) {
                Snackbar.make(v, "Invalid location entered!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            // make sure location name is unique
            else if(locationNames.contains(locName.getText().toString())) {
                Snackbar.make(v, "The location name has already been used, please pick another!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            // validate that a valid coordinate was entered
            else if(!locCoords.getText().toString().matches("^-?([1-8]?[1-9]*|[1-9]*0)\\.{1}\\d{1,}+,{1}\\s?+-?([1-8]?[1-9]|[1-9]0)\\.{1}\\d{1,}") || locCoords.getText().toString().matches("")) {
                Snackbar.make(v, "Invalid coordinate given", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            // else whats entered is valid
            else {
                // create a new object
                Map<String, Object> loc = new HashMap<>();
                // fill with needed data
                loc.put("name", locName.getText().toString());
                loc.put("coords", locCoords.getText().toString());
                loc.put("details", locDetails.getText().toString());
                // add new object to firestore
                db.collection("Locations").add(loc).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // change view to MapsView
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

    }
}