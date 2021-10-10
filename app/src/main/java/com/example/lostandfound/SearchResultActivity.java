package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;

import com.example.lostandfound.databinding.ActivityScrollingBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lostandfound.databinding.ActivitySearchResultBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private ActivitySearchResultBinding binding;

    List<Locations> locations = new ArrayList<>();
    Intent intent;

    LinearLayout dyno;

    FirebaseFirestore db;
    public static final String TAG = "LostAndFound";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // setup DB
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("Results");

        // get location list
        intent = getIntent();
        locations = (List<Locations>) intent.getSerializableExtra("locations");

        dyno = findViewById(R.id.dynamic);
        int i = 0;

        for(Locations loc: locations) {
            addCard(dyno, loc, i++);
        }

        FloatingActionButton fab = binding.fab;
        fab.setImageResource(R.drawable.back);
        fab.setOnClickListener(view -> {
            Intent back = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(back);
        });
    }

    public void addCard(ViewGroup vg, Locations loc, int id) {
        View v = LayoutInflater.from(this).inflate(R.layout.location_card, null);
        v.setId(id);

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
}