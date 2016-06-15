package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.labourtoday.androidapp.R;

import java.util.ArrayList;

public class LabourerInfoActivity extends AppCompatActivity {
    private CheckBox mon,tues,wed,thurs,fri,sat,sun;
    private CheckBox email,text,call;
    private CheckBox van,burn,sur,newWest,coq,northShore,rich,delta;
    private EditText city;
    private SharedPreferences settings;
    private String cities, availability;
    private ArrayList<String> data;
    private ArrayList<CheckBox> days;
    private ArrayList<CheckBox> citys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_info);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        data = getIntent().getStringArrayListExtra("data");
        days = new ArrayList<>();
        citys = new ArrayList<>();

        van = (CheckBox) findViewById(R.id.chkVan);
        burn = (CheckBox) findViewById(R.id.chkBurn);
        sur = (CheckBox) findViewById(R.id.chkSur);
        newWest = (CheckBox) findViewById(R.id.chkNewWest);
        coq = (CheckBox) findViewById(R.id.chkCoq);
        northShore = (CheckBox) findViewById(R.id.chkNorthShore);
        rich = (CheckBox) findViewById(R.id.chkRich);
        delta = (CheckBox) findViewById(R.id.chkDelta);
        citys.add(van);
        citys.add(burn);
        citys.add(sur);
        citys.add(newWest);
        citys.add(coq);
        citys.add(northShore);
        citys.add(rich);
        citys.add(delta);

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

        email = (CheckBox) findViewById(R.id.chkEmail);
        text = (CheckBox) findViewById(R.id.chkText);
        call = (CheckBox) findViewById(R.id.chkCall);

        city = (EditText) findViewById(R.id.edit_city);

        cities = "\"cities\":[";
        availability = "\"availability\":[";
    }

    public void next(View v) {
        if (city.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter where you live", Toast.LENGTH_SHORT).show();
            return;
        }

        cities = cities.substring(0, 10);
        int citiesChecked = 0;
        for (int i = 0; i < citys.size(); i++) {
            CheckBox box = citys.get(i);
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

        //Log.i("CITIES", cities);
        //Log.i("AVAIL", availability);

        String pref_phone = "\"phone_preference\":" + call.isChecked();
        String pref_email = "\"email_preference\":" + email.isChecked();
        String pref_sms = "\"sms_preference\":" + text.isChecked();
        String home_city = "\"city\":" + "\"" + city.getText().toString() + "\"";

        //Log.i("P", pref_phone);
        //Log.i("EMAIL", pref_email);
        //Log.i("S", pref_sms);
        //Log.i("CITY", home_city);


        Intent equipmentIntent = new Intent(this, LabourerEquipActivity.class);
        data.add(1, cities);
        data.add(2, availability);
        data.add(3, pref_email);
        data.add(4, pref_phone);
        data.add(5, pref_sms);
        data.add(6, home_city);
        equipmentIntent.putStringArrayListExtra("data", data);
        startActivity(equipmentIntent);
    }
}
