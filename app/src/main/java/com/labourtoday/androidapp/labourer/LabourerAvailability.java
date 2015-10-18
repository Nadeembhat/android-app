package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.labourtoday.androidapp.contractor.ContractorLoginActivity;
import com.labourtoday.androidapp.contractor.ContractorProfileActivity;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class LabourerAvailability extends AppCompatActivity {
    private Drawer result;
    private final int PROFILE = 0;
    private final int LOG_OUT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_availability);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Labour Today");

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Profile"),
                        new SecondaryDrawerItem().withName("Log out")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case PROFILE:
                                Intent hireIntent = new Intent(LabourerAvailability.this, ContractorProfileActivity.class);
                                startActivity(hireIntent);
                                return true;
                            case LOG_OUT:
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                // Return to the welcome page
                                Intent welcomeIntent = new Intent(LabourerAvailability.this, ContractorLoginActivity.class);
                                startActivity(welcomeIntent);
                                finish();
                                return true;
                            default:
                                return false;
                        }
                    }
                })
                .build();
    }

    public void updateAvailability(View v) {
        Toast.makeText(getApplicationContext(), "Preference recorded.", Toast.LENGTH_LONG).show();
        finish();
    }


}