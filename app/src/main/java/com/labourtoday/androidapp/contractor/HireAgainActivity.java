package com.labourtoday.androidapp.contractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.labourtoday.androidapp.R;

import java.util.ArrayList;
import java.util.HashSet;

public class HireAgainActivity extends AppCompatActivity {
    private SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_again);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        for (String s : settings.getStringSet("contractorDetail", new HashSet<String>())) {
            Log.i("HireAgain", s);
        }
        ArrayList<String> list = getIntent().getStringArrayListExtra("data");
        for (int i = 0; i < list.size(); i++) {
            Log.i("HireAgain", list.get(i));
        }
        for (String s : settings.getStringSet("jobDetail", new HashSet<String>())) {
            Log.i("HireAgain", s);
        }
    }

    public void next(View v) {
        Intent i = new Intent(HireAgainActivity.this, HiringGridActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setAction("Rehire");
        startActivity(i);
        finish();
    }
}
