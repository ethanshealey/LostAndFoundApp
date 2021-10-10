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

public class AddLocationActivity extends AppCompatActivity {

    Intent intent;
    Button back;
    Button addLocation;

    TextView locName;
    TextView locCoords;
    TextView locDetails;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        ActionBar ab = getSupportActionBar();
        ab.hide();

        locName = (TextView) findViewById(R.id.searchQuery);
        locCoords = (TextView) findViewById(R.id.loc_location);
        locDetails = (TextView) findViewById(R.id.loc_details);

        back = (Button) findViewById(R.id.backBtn);
        back.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
        });

        List<String> locationNames = new ArrayList<>();

        db.collection("Locations").get().addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               for(QueryDocumentSnapshot doc: task.getResult()) {
                   locationNames.add(doc.getData().get("name").toString());
               }
           }
        });

        addLocation = (Button) findViewById(R.id.searchBtn);
        addLocation.setOnClickListener(v -> {

            if(locName.getText().toString().matches("")) {
                Snackbar.make(v, "Invalid location entered!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else if(locationNames.contains(locName.getText().toString())) {
                Snackbar.make(v, "The location name has already been used, please pick another!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else if(!locCoords.getText().toString().matches("^-?([1-8]?[1-9]*|[1-9]*0)\\.{1}\\d{1,}+,{1}\\s?+-?([1-8]?[1-9]|[1-9]0)\\.{1}\\d{1,}") || locCoords.getText().toString().matches("")) {
                Snackbar.make(v, "Invalid coordinate given", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else {
                Map<String, Object> loc = new HashMap<>();
                loc.put("name", locName.getText().toString());
                loc.put("coords", locCoords.getText().toString());
                loc.put("details", locDetails.getText().toString());
                db.collection("Locations").add(loc).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

    }
}