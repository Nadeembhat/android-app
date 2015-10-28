package com.labourtoday.androidapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.labourtoday.androidapp.contractor.ContractorLoginActivity;
import com.labourtoday.androidapp.contractor.HiringActivity;
import com.labourtoday.androidapp.labourer.LabourerLoginActivity;
import com.labourtoday.androidapp.labourer.LabourerProfileActivity;

public class MainActivity extends AppCompatActivity {
    private int backButtonCount;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getString(Constants.LAST_LOGIN, null) == null) {
            sharedPreferences.edit().putString(Constants.LAST_LOGIN, "").apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        backButtonCount = 0;
    }

    /**
     * Handler for the find work button.
     * @param view
     *          Reference to the button
     */
    public void launchLabourerMain(View view) {
        if (sharedPreferences.getString(Constants.LAST_LOGIN, "").equals(Constants.LABOURER)) {
            startActivity(new Intent(this, LabourerProfileActivity.class));
        } else {
            startActivity(new Intent(this, LabourerLoginActivity.class));
        }
    }


    /**
     * Handler for the hire button
     * @param view
     *          Reference to the button
     */
    public void launchContractorMain(View view) {
        if (sharedPreferences.getString(Constants.LAST_LOGIN, "").equals(Constants.CONTRACTOR)) {
            startActivity(new Intent(this, HiringActivity.class));
        } else {
            startActivity(new Intent(this, ContractorLoginActivity.class));
        }
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
