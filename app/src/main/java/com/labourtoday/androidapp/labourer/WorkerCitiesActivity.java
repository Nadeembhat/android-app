package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.util.ArrayList;

public class WorkerCitiesActivity extends AppCompatActivity {
    private Drawer result;
    private SharedPreferences settings;

    private String cities;
    private ArrayList<String> data;
    private CheckBox van,burn,sur,newWest,coq,northShore,rich,delta;
    private ArrayList<CheckBox> listCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_cities);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        result = new DrawerBuilder().withActivity(this).withToolbar(toolbar)
                .withSavedInstance(savedInstanceState).build();
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        data = getIntent().getStringArrayListExtra("data");
        listCities = new ArrayList<>();

        van = (CheckBox) findViewById(R.id.chkVan);
        burn = (CheckBox) findViewById(R.id.chkBurn);
        sur = (CheckBox) findViewById(R.id.chkSur);
        newWest = (CheckBox) findViewById(R.id.chkNewWest);
        coq = (CheckBox) findViewById(R.id.chkCoq);
        northShore = (CheckBox) findViewById(R.id.chkNorthShore);
        rich = (CheckBox) findViewById(R.id.chkRich);
        delta = (CheckBox) findViewById(R.id.chkDelta);
        listCities.add(van);
        listCities.add(burn);
        listCities.add(sur);
        listCities.add(newWest);
        listCities.add(coq);
        listCities.add(northShore);
        listCities.add(rich);
        listCities.add(delta);

        cities = "\"cities\":[";
    }

    public void next(View v) {
        cities = cities.substring(0, 10);
        int citiesChecked = 0;
        for (int i = 0; i < listCities.size(); i++) {
            CheckBox box = listCities.get(i);
            if (box.isChecked()) {
                cities += "\"" + box.getText().toString() + "\",";
                citiesChecked++;
            }
        }
        if (citiesChecked == 0) {
            Toast.makeText(getApplicationContext(), "Please select at least one city and day", Toast.LENGTH_SHORT).show();
            return;
        }
        cities = cities.substring(0, cities.length() - 1);
        cities += "]";

        Log.i("WorkerCities", cities);

        Intent notificationIntent = new Intent(this, WorkerNotificationActivity.class);
        data.add(2, cities);
        notificationIntent.putStringArrayListExtra("data", data);
        startActivity(notificationIntent);
    }
}
