package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lostandfound.databinding.ActivityScrollingBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private ActivityScrollingBinding binding;
    List<Locations> locations = new ArrayList<>();
    Intent intent;

    ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("Lost and Found");

        // get location list
        intent = getIntent();
        locations = (List<Locations>) intent.getSerializableExtra("locations");

        cl = (ConstraintLayout) findViewById(R.id.constraint_parent);
        int i = 0;

        for(Locations loc: locations) {
            addCard(cl, loc, i++);
        }

        FloatingActionButton fab = binding.fab;
        fab.setImageResource(R.drawable.back);
        fab.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
        });
    }

    public void addCard(ViewGroup vg, Locations loc, int i) {
        View v = LayoutInflater.from(this).inflate(R.layout.location_card, null);
        v.setId(i);
        vg.addView(v);
    }
}