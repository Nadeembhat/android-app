package com.labourtoday.androidapp.contractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.labourtoday.androidapp.R;

public class HireAgainActivity extends AppCompatActivity {
    private SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_again);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public void next(View v) {
        Intent i = new Intent(HireAgainActivity.this, HiringGridActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
