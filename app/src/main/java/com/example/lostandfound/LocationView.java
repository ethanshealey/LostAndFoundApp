package com.example.lostandfound;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocationView extends AppCompatActivity {

    TextView locName;
    TextView locLoc;
    TextView locDetails;
    StreetViewPanorama svp;
    FloatingActionButton fab;

    FirebaseFirestore db;
    public static final String TAG = "LostAndFound";

    List<Locations> locations = new ArrayList<>();
    Locations location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);

        ActionBar ab = getSupportActionBar();
        ab.hide();

        location = new Locations();

        locName = findViewById(R.id.location_name);
        locName.setText(location.getName());
        locLoc = findViewById(R.id.location_loc);
        locLoc.setText(location.getCoords());
        locDetails = findViewById(R.id.location_details);
        locDetails.setText(location.getDetails());
        fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.googleicon);

        // get the item requested
        Intent intent = getIntent();
        String query = intent.getSerializableExtra("Location").toString();

        // setup DB
        db = FirebaseFirestore.getInstance();
        db.collection("Locations").get().addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               for(QueryDocumentSnapshot doc: task.getResult()) {
                   System.out.println(doc.getData().get("name").toString() + " " + query);
                   if(doc.getData().get("name").toString().matches(query)) {

                       System.out.println(doc.getData().toString());
                       locName.setText(doc.getData().get("name").toString());
                       locLoc.setText(doc.getData().get("coords").toString());
                       if(doc.getData().get("details") != null)
                           locDetails.setText(doc.getData().get("details").toString());
                       else
                           locDetails.setText("");
                   }
               }
           }
        });

        fab.setOnClickListener(v -> {

            String lat = locLoc.getText().toString().split(",")[0];
            String lng = locLoc.getText().toString().split(",")[1];
            String strUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng;
            Intent mapsIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
            mapsIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(mapsIntent);
        });

    }

}