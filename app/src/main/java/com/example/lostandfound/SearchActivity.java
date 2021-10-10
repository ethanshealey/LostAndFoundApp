package com.example.lostandfound;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    TextView query;
    Button searchBtn;
    Button backBtn;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar ab = getSupportActionBar();
        ab.hide();

        // setup DB
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        List<Locations> locations = new ArrayList<>();
        List<Locations> results = new ArrayList<>();

        db.collection("Locations").get().addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               for(QueryDocumentSnapshot doc: task.getResult()) {
                   Locations loc = new Locations();
                   loc.setName(doc.getData().get("name").toString());
                   loc.setLat(doc.getData().get("coords").toString().split(",")[0]);
                   loc.setLng(doc.getData().get("coords").toString().split(",")[1]);
                   loc.setDetails(doc.getData().get("details").toString());
                   locations.add(loc);
               }
           }
        });

        query = findViewById(R.id.searchQuery);
        searchBtn = findViewById(R.id.searchBtn);
        backBtn = findViewById(R.id.searchBackBtn);

        searchBtn.setOnClickListener(v -> {
            if(!query.getText().toString().matches("")) {
                for(Locations loc: locations) {
                    if(loc.getName().toLowerCase().contains(query.getText().toString().toLowerCase())) {
                        results.add(loc);
                    }
                }
                Intent resultsPage = new Intent(getApplicationContext(), SearchResultActivity.class);
                resultsPage.putExtra("locations", (Serializable) results);
                resultsPage.putExtra("query", (Serializable) query.getText().toString());
                startActivity(resultsPage);
            }
            else {
                Snackbar.make(v, "Please enter a query!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        backBtn.setOnClickListener(v -> {
            Intent back = new Intent(getApplicationContext(), ScrollingActivity.class);
            back.putExtra("locations", (Serializable) locations);
            startActivity(back);
        });
    }
}