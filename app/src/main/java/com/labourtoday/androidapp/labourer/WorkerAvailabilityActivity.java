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

public class WorkerAvailabilityActivity extends AppCompatActivity {
    private Drawer result;
    private SharedPreferences settings;
    private CheckBox mon,tues,wed,thurs,fri,sat,sun;

    private String availability;
    private ArrayList<String> data;
    private ArrayList<CheckBox> days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_availability);

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
        days = new ArrayList<>();
        mon = (CheckBox) findViewById(R.id.chkMon);
        tues = (CheckBox) findViewById(R.id.chkTues);
        wed = (CheckBox) findViewById(R.id.chkWed);
        thurs = (CheckBox) findViewById(R.id.chkThurs);
        fri = (CheckBox) findViewById(R.id.chkFri);
        sat = (CheckBox) findViewById(R.id.chkSat);
        sun = (CheckBox) findViewById(R.id.chkSun);
        days.add(mon);
        days.add(tues);
        days.add(wed);
        days.add(thurs);
        days.add(fri);
        days.add(sat);
        days.add(sun);

        availability = "\"availability\":[";
    }

    public void next(View v) {
        availability = availability.substring(0, 16);
        int daysChecked = 0;
        for (int i = 0; i < days.size(); i++) {
            CheckBox box = days.get(i);
            if (box.isChecked()) {
                availability += "\"" + box.getText().toString() + "\",";
                daysChecked++;
            }
        }
        if (daysChecked == 0) {
            Toast.makeText(getApplicationContext(), "Please select at least one city and day", Toast.LENGTH_SHORT).show();
            return;
        }
        availability = availability.substring(0, availability.length() - 1);
        availability += "]";

        Log.i("WorkerDays", availability);

        Intent citiesIntent = new Intent(this, WorkerCitiesActivity.class);
        data.add(1, availability);
        citiesIntent.putStringArrayListExtra("data", data);
        startActivity(citiesIntent);
    }
}
