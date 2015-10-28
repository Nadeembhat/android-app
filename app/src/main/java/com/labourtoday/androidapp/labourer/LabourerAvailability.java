package com.labourtoday.androidapp.labourer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.labourtoday.androidapp.contractor.ContractorLoginActivity;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.HashMap;
import java.util.Map;

public class LabourerAvailability extends AppCompatActivity {
    private Drawer result;
    private final int PROFILE = 0;
    private final int LOG_OUT = 1;
    private ProgressDialog progress;
    private Switch mon, tues, wed, thurs, fri, sat, sun, now;
    SharedPreferences sharedPreferences;
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
                                Intent hireIntent = new Intent(LabourerAvailability.this, LabourerEditActivity.class);
                                startActivity(hireIntent);
                                return true;
                            case LOG_OUT:
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                settings.edit().remove(Constants.LAST_LOGIN).apply();
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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mon = (Switch) findViewById(R.id.mon);
        tues = (Switch) findViewById(R.id.tues);
        wed = (Switch) findViewById(R.id.wed);
        thurs = (Switch) findViewById(R.id.thurs);
        fri = (Switch) findViewById(R.id.fri);
        sat = (Switch) findViewById(R.id.sat);
        sun = (Switch) findViewById(R.id.sun);
        now = (Switch) findViewById(R.id.now);

        progress = new ProgressDialog(LabourerAvailability.this);
        progress.setMessage("Updating your profile...");

        mon.setChecked(sharedPreferences.getBoolean(Constants.MON, false));
        tues.setChecked(sharedPreferences.getBoolean(Constants.TUES, false));
        wed.setChecked(sharedPreferences.getBoolean(Constants.WED, false));
        thurs.setChecked(sharedPreferences.getBoolean(Constants.THURS, false));
        fri.setChecked(sharedPreferences.getBoolean(Constants.FRI, false));
        sat.setChecked(sharedPreferences.getBoolean(Constants.SAT, false));
        sun.setChecked(sharedPreferences.getBoolean(Constants.SUN, false));
        now.setChecked(sharedPreferences.getBoolean(Constants.NOW, false));

        autoSaveSwitchState(mon, Constants.MON);
        autoSaveSwitchState(tues, Constants.TUES);
        autoSaveSwitchState(wed, Constants.WED);
        autoSaveSwitchState(thurs, Constants.THURS);
        autoSaveSwitchState(fri, Constants.FRI);
        autoSaveSwitchState(sat, Constants.SAT);
        autoSaveSwitchState(sun, Constants.SUN);
        autoSaveSwitchState(now, Constants.NOW);
    }

    public void updateAvailability(View v) {
        String url = Constants.URLS.LABOURER_DETAIL.string;
        progress.show();
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Automatic login after registration using registered username and password
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), "Successfully updated availability preference", Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                    }
                }
        )
        {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                // Get the registration info from input fields and add them to the body of the request
                params.put(Constants.AVAILABILITY, getAvailabilityParam());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " +
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.AUTH_TOKEN, "noTokenFound"));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(putRequest);
    }

    private void autoSaveSwitchState(Switch s, final String day) {
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(day, isChecked).apply();
            }
        });
    }

    private String getAvailabilityParam() {
        return String.valueOf(sharedPreferences.getBoolean(Constants.MON, false)) + " " +
                String.valueOf(sharedPreferences.getBoolean(Constants.TUES, false)) + " " +
                String.valueOf(sharedPreferences.getBoolean(Constants.WED, false)) + " " +
                String.valueOf(sharedPreferences.getBoolean(Constants.THURS, false)) + " " +
                String.valueOf(sharedPreferences.getBoolean(Constants.FRI, false)) + " " +
                String.valueOf(sharedPreferences.getBoolean(Constants.SAT, false)) + " " +
                String.valueOf(sharedPreferences.getBoolean(Constants.SUN, false)) + " " +
                String.valueOf(sharedPreferences.getBoolean(Constants.NOW, false));
    }



}