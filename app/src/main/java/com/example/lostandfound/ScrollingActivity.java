package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lostandfound.databinding.ActivityScrollingBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * ScrollingActivity
 *
 * This activity handles the ListView page,
 * a floating action button, and n amount of
 * cards for each location in the database.
 * The floating actions button leads to `SearchActivity`,
 * and each card will lead to `LocationView`.
 */

public class ScrollingActivity extends AppCompatActivity {

    private ActivityScrollingBinding binding;

    // init list of locations
    List<Locations> locations = new ArrayList<>();

    // get the intent
    Intent intent;

    // define the LinearLayout used to dynamically
    // add cards for each location
    LinearLayout dyno;

    // defined the firestore db
    FirebaseFirestore db;
    public static final String TAG = "LostAndFound";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // setup DB
        db = FirebaseFirestore.getInstance();

        //setup the toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("Lost and Found");
        toolBarLayout.setCollapsedTitleTextAppearance(R.style.BitterBold);
        toolBarLayout.setExpandedTitleTextAppearance(R.style.BitterBold);

        // get location list
        intent = getIntent();
        locations = (List<Locations>) intent.getSerializableExtra("locations");

        // get the LinearLayout
        dyno = findViewById(R.id.dynamic);

        // for each location, add a card
        int i = 0;
        for(Locations loc: locations) {
            addCard(dyno, loc, i++);
        }

        // create the floating action button
        FloatingActionButton fab = binding.fab;
        // set the image to what we want (search icon)
        fab.setImageResource(R.drawable.search);
        // listen for a click
        // leads to -> `SearchActivity`
        fab.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), SearchActivity.class);
            intent.putExtra("locations", (Serializable) locations);
            startActivity(intent);
        });
    }

    /**
     * addCard
     *
     * Dynamically adds a card to the view
     *
     * @param vg
     * @param loc
     * @param id
     */
    public void addCard(ViewGroup vg, Locations loc, int id) {
        // inflate the view with a `location_card`
        View v = LayoutInflater.from(this).inflate(R.layout.location_card, null);
        // set the id
        v.setId(id);

        // listen for a click on the card
        // leads to -> `LocationView`
        v.setOnClickListener(vi -> {
            Intent intent = new Intent(getApplicationContext(), LocationView.class);
            intent.putExtra("Location", (Serializable) loc.getName());
            intent.putExtra("PREVIOUS", (Serializable) "LISTVIEW");
            startActivity(intent);
        });

        // set card name
        TextView locName = v.findViewById(R.id.searchQuery);
        locName.setText(loc.getName());

        // set card location
        TextView locLoc =  v.findViewById(R.id.location_location1);
        locLoc.setText(String.format("%.5g",Double.parseDouble(loc.getLat())) + ", " + String.format("%.5g%n", Double.parseDouble(loc.getLng())));

        // set card details
        TextView locDetails = v.findViewById(R.id.location_details1);
        locDetails.setText(loc.getDetails());

        // add the card
        vg.addView(v);
    }

    @Override
    public void onBackPressed() {
        // override the back button to lead to `MapView`
        Intent toMap = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(toMap);
    }
}