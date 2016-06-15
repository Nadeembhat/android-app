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

import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.util.ArrayList;

public class WorkerNotificationActivity extends AppCompatActivity {
    private CheckBox email,text,call;
    private ArrayList<String> data;
    private SharedPreferences settings;
    private Drawer result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_notification);

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

        email = (CheckBox) findViewById(R.id.chkEmail);
        text = (CheckBox) findViewById(R.id.chkText);
        call = (CheckBox) findViewById(R.id.chkCall);
        data = getIntent().getStringArrayListExtra("data");
    }

    public void next(View v) {
        String pref_phone = "\"phone_preference\":" + call.isChecked();
        String pref_email = "\"email_preference\":" + email.isChecked();
        String pref_sms = "\"sms_preference\":" + text.isChecked();

        Log.i("WorkerPhone", pref_phone);
        Log.i("WorkerEmail", pref_email);
        Log.i("WorkerSms", pref_sms);


        Intent equipmentIntent = new Intent(this, LabourerEquipActivity.class);
        data.add(3, pref_email);
        data.add(4, pref_phone);
        data.add(5, pref_sms);
        equipmentIntent.putStringArrayListExtra("data", data);
        startActivity(equipmentIntent);
    }
}
