package com.example.lostandfound;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.lostandfound.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/***
 * MapsActivity
 *
 * This activity handles the main page, which displays a full-screen
 * google maps widget, and two buttons for `list view` and
 * `add location`. These buttons lead to `ScrollingActivity` and
 * `AddLocationActivity` respectively.
 *
 * This activity has direct access to the firestore db, referenced
 * by the variable `db`.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // define google map widget
    private GoogleMap mMap;
    private UiSettings uiSettings;
    private ActivityMapsBinding binding;

    // init firestore db
    FirebaseFirestore db;
    public static final String TAG = "LostAndFound";

    // init list to store db info
    List<Locations> locations = new ArrayList<>();

    // init widgets
    Button listview;
    Button addlocation;

    // define strings to later use in `OnMapReady`
    String clickId = "", oldClickId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // setup DB
        db = FirebaseFirestore.getInstance();

        // widgets
        listview = findViewById(R.id.listview);
        addlocation = findViewById(R.id.addloc);

        // listen for click on `List View` button
        // this leads to -> ScrollingActivity
        listview.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ScrollingActivity.class);
            intent.putExtra("locations", (Serializable) locations);
            startActivity(intent);
        });

        // listen for click on `Add Location` button
        // this leads to -> AddLocationActivity
        addlocation.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
            startActivity(intent);
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // init google maps
        mMap = googleMap;

        // set UI to liking
        uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true); // show zoom buttons
        googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json))); // dark mode

        // access the database
        db.collection("Locations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {

                    // init name amd details
                    String name, details;

                    // init lat and lng
                    double lat, lng;

                    // get coords
                    lat = Double.parseDouble(doc.getData().get("coords").toString().split(",")[0]);
                    lng = Double.parseDouble(doc.getData().get("coords").toString().split(",")[1]);

                    // get name and details
                    name = doc.getData().get("name").toString();
                    details = doc.getData().get("details").toString();

                    // add marker on the map
                    Marker m = mMap.addMarker((new MarkerOptions().position(new LatLng(lat, lng)).title(name)));

                    // listen for click on each marker
                    // if marker clicked once: displays marker title
                    // if marker clicked twice: leads to -> LocationView
                    mMap.setOnMarkerClickListener(marker -> {

                        // get the current marker ID
                        clickId = marker.getId();

                        // if same marker clicked twice ->
                        // change view to LocationView
                        if(clickId.matches(oldClickId)) {
                            Intent intent = new Intent(getApplicationContext(), LocationView.class);
                            intent.putExtra("Location", (Serializable) marker.getTitle());
                            intent.putExtra("PREVIOUS", (Serializable) "MAPVIEW");
                            startActivity(intent);
                        }

                        // set oldClickId to current ID
                        oldClickId = clickId;

                        return false;
                    });

                    // add location to list of locations
                    Locations loc = new Locations();
                    loc.setID(doc.getId());
                    loc.setName(name);
                    loc.setLat(doc.getData().get("coords").toString().split(",")[0]);
                    loc.setLng(doc.getData().get("coords").toString().split(",")[1]);
                    loc.setDetails(details);
                    locations.add(loc);
                }

                // move the camera to a predefined area (Hard coded to High Point University)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.973891187827405, -79.99543973385548), 7.0f));
            }
            else {
                // if an error occurs, log it
                Log.w(TAG, "Error: " + task.getException());
            }
        });
    }
}