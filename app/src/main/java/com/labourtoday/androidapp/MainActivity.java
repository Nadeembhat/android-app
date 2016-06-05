package com.labourtoday.androidapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.labourtoday.androidapp.contractor.ContractorRegistrationActivity;
import com.labourtoday.androidapp.contractor.HiringGridActivity;
import com.labourtoday.androidapp.labourer.LabourerRegistrationActivity;

import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    private int backButtonCount;
    private SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String action = "";
        try {
            action = getIntent().getAction();
        } catch(NullPointerException e) {
            Log.e("MainActivity", e.getMessage());
        }
        if (action.equals("Exit")) {
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        backButtonCount = 0;
    }

    /**
     * Handler for the hire button
     * @param view
     *          Reference to the button
     */
    public void launchContractorMain(View view) {
        if (settings.getStringSet("contractorDetail", new HashSet<String>()).size() >= 4) {
            Intent i = new Intent(this, HiringGridActivity.class);
            i.setAction("Rehire");
            startActivity(i);
        } else {
            startActivity(new Intent(this, ContractorRegistrationActivity.class));
        }
    }

    /**
     * Handler for the find work button.
     * @param view
     *          Reference to the button
     */
    public void launchLabourerMain(View view) {
        startActivity(new Intent(this, LabourerRegistrationActivity.class));
    }

    /**
     * Press back twice to exit application
     */
    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Press back again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
}
