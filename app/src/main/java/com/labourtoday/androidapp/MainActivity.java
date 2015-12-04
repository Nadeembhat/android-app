package com.labourtoday.androidapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.labourtoday.androidapp.contractor.ContractorLoginActivity;
import com.labourtoday.androidapp.contractor.ContractorMainActivity;

public class MainActivity extends AppCompatActivity {
    private int backButtonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!settings.getString(Constants.AUTH_TOKEN, "").equals("")) {
            startActivity(new Intent(this, ContractorMainActivity.class));
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
        startActivity(new Intent(this, ContractorMainActivity.class));
    }

    /**
     * Handler for the login button
     * @param view
     *          Reference to the button
     */
    public void launchContractorLogin(View view) {
        startActivity(new Intent(this, ContractorLoginActivity.class));
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
