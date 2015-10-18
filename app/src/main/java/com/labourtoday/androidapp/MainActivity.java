package com.labourtoday.androidapp;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.labourtoday.androidapp.contractor.ContractorLoginActivity;
import com.labourtoday.androidapp.labourer.LabourerLoginActivity;

public class MainActivity extends AppCompatActivity {
    private int backButtonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(Constants.LAST_LOGIN, "");
    }

    @Override
    public void onResume() {
        super.onResume();
        backButtonCount = 0;
    }

    /**
     * Handler for the find work button
     * @param view
     */
    public void launchLabourerMain(View view) {
        //Launch the signup page
        Intent signupIntent = new Intent(this, LabourerLoginActivity.class);
        startActivity(signupIntent);
    }


    /**
     * Handler for the hire button
     * @param view
     */
    public void launchContractorMain(View view) {
        Intent signinIntent = new Intent(this, ContractorLoginActivity.class);
        startActivity(signinIntent);
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
