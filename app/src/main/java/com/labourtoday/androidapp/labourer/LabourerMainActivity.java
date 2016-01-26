package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;

public class LabourerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Labour Today");

        Button login = (Button) findViewById(R.id.button_login_labourer);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!settings.getString(Constants.AUTH_TOKEN, "").equals("")
                && settings.getString(Constants.LAST_LOGIN, "").equals(Constants.LABOURER)) {
            login.setAlpha(.5f);
            login.setClickable(false);
            login.setEnabled(false);
        }
    }

    /**
     * Handler for the start button
     * @param view
     *          Reference to the button
     */
    public void launchStart(View view) {
        startActivity(new Intent(this, LabourerGridActivity.class));
    }

    /**
     * Handler for the Login button
     * @param view
     *          Reference to the button
     */
    public void launchLabourerLogin(View view) {
        startActivity(new Intent(this, LabourerLoginActivity.class));
    }
}
