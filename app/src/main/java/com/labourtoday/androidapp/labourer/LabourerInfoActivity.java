package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.labourtoday.androidapp.R;

import java.util.ArrayList;

public class LabourerInfoActivity extends AppCompatActivity {
    private ArrayList<String> data;
    private CheckBox mon,tues,wed,thurs,fri,sat,sun,var,email,text,call;
    private EditText city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_info);
        data = getIntent().getStringArrayListExtra("data");
        mon = (CheckBox) findViewById(R.id.chkMon);
        tues = (CheckBox) findViewById(R.id.chkTues);
        wed = (CheckBox) findViewById(R.id.chkWed);
        thurs = (CheckBox) findViewById(R.id.chkThurs);
        fri = (CheckBox) findViewById(R.id.chkFri);
        sat = (CheckBox) findViewById(R.id.chkSat);
        sun = (CheckBox) findViewById(R.id.chkSun);
        var = (CheckBox) findViewById(R.id.chkVary);
        email = (CheckBox) findViewById(R.id.chkEmail);
        text = (CheckBox) findViewById(R.id.chkText);
        call = (CheckBox) findViewById(R.id.chkCall);

        city = (EditText) findViewById(R.id.edit_city);
    }

    public void next(View v) {
        if (city.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter your city", Toast.LENGTH_SHORT).show();
            return;
        }
        data.add("Availability Information:");
        data.add("City: " + city.getText().toString());
        data.add("Monday: " + Boolean.toString(mon.isChecked()));
        data.add("Tuesday: " + Boolean.toString(tues.isChecked()));
        data.add("Wednesday: " + Boolean.toString(wed.isChecked()));
        data.add("Thursday: " + Boolean.toString(thurs.isChecked()));
        data.add("Friday: " + Boolean.toString(fri.isChecked()));
        data.add("Saturday: " + Boolean.toString(sat.isChecked()));
        data.add("Sunday: " + Boolean.toString(sun.isChecked()));
        data.add("Varies: " + Boolean.toString(var.isChecked()));
        data.add("Notification Preference:");
        data.add("Email: " + Boolean.toString(email.isChecked()));
        data.add("Text: " + Boolean.toString(text.isChecked()));
        data.add("Phone Call: " + Boolean.toString(call.isChecked()));
        Intent equipmentIntent = new Intent(this, LabourerEquipActivity.class);
        equipmentIntent.putStringArrayListExtra("data", data);
        startActivity(equipmentIntent);
    }
}
