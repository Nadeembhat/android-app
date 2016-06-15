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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.util.ArrayList;

public class LabourerEquipActivity extends AppCompatActivity {
    private RadioGroup license, car, belt, boots, hat, vest;
    private CheckBox sfa, swfa, cprc;
    private SharedPreferences settings;
    private ArrayList<String> data;
    private final int EMPTY = -1;
    private String equipment;
    private Drawer result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_equip);
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
        license = (RadioGroup) findViewById(R.id.radio_drivers);
        car = (RadioGroup) findViewById(R.id.radio_car);
        belt = (RadioGroup) findViewById(R.id.radio_belt);
        boots = (RadioGroup) findViewById(R.id.radio_boots);
        hat = (RadioGroup) findViewById(R.id.radio_hat);
        vest = (RadioGroup) findViewById(R.id.radio_vest);

        sfa = (CheckBox) findViewById(R.id.chkSFA);
        swfa = (CheckBox) findViewById(R.id.chkSWFA);
        cprc = (CheckBox) findViewById(R.id.chkCPRC);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        equipment = "\"equipment\":[";
    }

    public void next(View v) {
        if (checkEmpty()) {
            Toast.makeText(getApplicationContext(), "Please answer all questions", Toast.LENGTH_SHORT).show();
            return;
        }
        equipment = equipment.substring(0, 13);
        RadioButton lbutton = (RadioButton) findViewById(license.getCheckedRadioButtonId());
        RadioButton cbutton = (RadioButton) findViewById(car.getCheckedRadioButtonId());
        RadioButton bbutton = (RadioButton) findViewById(belt.getCheckedRadioButtonId());
        RadioButton bsbutton = (RadioButton) findViewById(boots.getCheckedRadioButtonId());
        RadioButton hbutton = (RadioButton) findViewById(hat.getCheckedRadioButtonId());
        RadioButton vbutton = (RadioButton) findViewById(vest.getCheckedRadioButtonId());

        add(lbutton, "Driver's Licence");
        add(cbutton, "Vehicle");
        add(bbutton, "Tool Belt with Basic Tools");
        add(bsbutton, "Steel Toe Boots");
        add(hbutton, "Hard Hat");
        add(vbutton, "Safety Vest");

        if (sfa.isChecked()) {
            equipment += "\"" + "Standard First Aid" + "\",";
        }

        if (swfa.isChecked()) {
            equipment += "\"" + "Standard Workplace First Aid" + "\",";
        }

        if (cprc.isChecked()) {
            equipment += "\"" + "CPR C & AED" + "\",";
        }

        equipment = equipment.substring(0, equipment.length() - 1);
        equipment += "]";

        Log.i("EQUIPMENT", equipment);
        data.add(6, equipment);
        Intent i = new Intent(this, ReferenceActivity.class);
        i.putStringArrayListExtra("data", data);
        startActivity(i);
    }

    private boolean checkEmpty() {
        if (license.getCheckedRadioButtonId() == EMPTY ||
                car.getCheckedRadioButtonId() == EMPTY ||
                belt.getCheckedRadioButtonId() == EMPTY ||
                boots.getCheckedRadioButtonId() == EMPTY ||
                hat.getCheckedRadioButtonId() == EMPTY ||
                vest.getCheckedRadioButtonId() == EMPTY) {
            return true;
        }
        return false;
    }

    private void add(RadioButton btn, String equip) {
        if (btn.getText().toString().equals("Yes")) {
            equipment += "\"" + equip + "\",";
        }
    }
}
