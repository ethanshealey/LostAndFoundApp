package com.example.lostandfound;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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

        locName = (TextView) findViewById(R.id.location_name1);
        locCoords = (TextView) findViewById(R.id.loc_location);
        locDetails = (TextView) findViewById(R.id.loc_details);

        back = (Button) findViewById(R.id.backBtn);
        back.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
        });

        addLocation = (Button) findViewById(R.id.addBtn);
        addLocation.setOnClickListener(v -> {
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
        });

    }
}