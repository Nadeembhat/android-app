package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.labourtoday.androidapp.R;

import java.util.HashSet;
import java.util.Set;

public class LabourerEquipActivity extends AppCompatActivity {
    private RadioGroup license, car, belt, boots, hat, vest;
    private CheckBox sfa, swfa, cprc;
    private SharedPreferences settings;
    private final int EMPTY = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_equip);
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
    }

    public void next(View v) {
        Set<String> data = new HashSet<>();
        if (checkEmpty()) {
            Toast.makeText(getApplicationContext(), "Please answer all questions", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton lbutton = (RadioButton) findViewById(license.getCheckedRadioButtonId());
        RadioButton cbutton = (RadioButton) findViewById(car.getCheckedRadioButtonId());
        RadioButton bbutton = (RadioButton) findViewById(belt.getCheckedRadioButtonId());
        RadioButton bsbutton = (RadioButton) findViewById(boots.getCheckedRadioButtonId());
        RadioButton hbutton = (RadioButton) findViewById(hat.getCheckedRadioButtonId());
        RadioButton vbutton = (RadioButton) findViewById(vest.getCheckedRadioButtonId());
        data.add("Driver's License: " + lbutton.getText());
        data.add("Vehicle: " + cbutton.getText());
        data.add("Tool belt: " + bbutton.getText());
        data.add("Steel toe boots: " + bsbutton.getText());
        data.add("Hardhat: " + hbutton.getText());
        data.add("Safety vest: " + vbutton.getText());
        data.add("Standard first aid: " + Boolean.toString(sfa.isChecked()));
        data.add("Standard workplace first aid: " + Boolean.toString(swfa.isChecked()));
        data.add("CPR C: " + Boolean.toString(cprc.isChecked()));
        settings.edit().putStringSet("equipment", data).apply();
        Intent i = new Intent(this, ReferenceActivity.class);
        i.putStringArrayListExtra("data", getIntent().getStringArrayListExtra("data"));
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
}
