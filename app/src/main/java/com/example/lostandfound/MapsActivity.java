package com.example.lostandfound;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    FirebaseFirestore db;
    public static final String TAG = "LostAndFound";

    List<Locations> locations = new ArrayList<>();

    Button listview;
    Button addlocation;
    private UiSettings uiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // setup DB
        db = FirebaseFirestore.getInstance();

        // widgets
        listview = (Button) findViewById(R.id.listview);
        addlocation = (Button) findViewById(R.id.addloc);

        listview.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ScrollingActivity.class);
            intent.putExtra("locations", (Serializable) locations);
            startActivity(intent);
        });

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
        mMap = googleMap;
        uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
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

                    AtomicReference oldClickId = new AtomicReference("");
                    // add marker on the map
                    Marker m = mMap.addMarker((new MarkerOptions().position(new LatLng(lat, lng)).title(name)));

                    mMap.setOnMarkerClickListener(marker -> {

                        String clickId = marker.getId();

                        if(clickId.matches((String) oldClickId.get())) {
                            Intent intent = new Intent(getApplicationContext(), LocationView.class);
                            intent.putExtra("Location", (Serializable) marker.getTitle());
                            intent.putExtra("PREVIOUS", (Serializable) "MAPVIEW");
                            startActivity(intent);
                        }
                        
                        oldClickId.set(clickId);
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
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.973891187827405, -79.99543973385548), 7.0f));
            }
            else {
                Log.w(TAG, "Error: " + task.getException());
            }
        });
    }
}