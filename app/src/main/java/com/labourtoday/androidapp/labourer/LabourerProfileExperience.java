package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.labourtoday.androidapp.contractor.ContractorLoginActivity;
import com.labourtoday.androidapp.contractor.ContractorProfileActivity;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LabourerProfileExperience extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    private Switch mySwitch;
    private RelativeLayout profileExperience;

    private Drawer result;
    private final int PROFILE = 0;
    private final int LOG_OUT = 1;

    private RadioGroup carpentryRadioGroup;
    private RadioGroup concreteRadioGroup;


    String[] experienceList = new String[]{"No", "Yes 1-3 months", "Ye > 6 months", "Red Seal"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_profile_experience);

        carpentryRadioGroup = (RadioGroup) findViewById(R.id.carpentry_radio);
        concreteRadioGroup = (RadioGroup) findViewById(R.id.concrete_radio);

        // setting switch
        mySwitch = (Switch) findViewById(R.id.gen_labour_text);
        mySwitch.setOnCheckedChangeListener(this);

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
                                Intent hireIntent = new Intent(LabourerProfileExperience.this, ContractorProfileActivity.class);
                                startActivity(hireIntent);
                                return true;
                            case LOG_OUT:
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                // Return to the welcome page
                                Intent welcomeIntent = new Intent(LabourerProfileExperience.this, ContractorLoginActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_labourer, menu);
        return true;
    }

    public void updateProfile(View view) {
        String url = Constants.URLS.LABOURER_DETAIL.string;
        final RadioButton selectedCarpentry = (RadioButton) findViewById(carpentryRadioGroup.getCheckedRadioButtonId());
        final RadioButton selectedConcrete = (RadioButton) findViewById(concreteRadioGroup.getCheckedRadioButtonId());
        StringRequest updateLabourerRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Successfully update profile", Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error updating profile",
                                Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                Log.d("HiringActivityCarp", Integer.toString(Arrays.asList(experienceList).indexOf(selectedCarpentry.getText())));
                Log.d("HiringActivityConc", Integer.toString(Arrays.asList(experienceList).indexOf(selectedConcrete.getText())));

                params.put("carpentry", Integer.toString(Arrays.asList(experienceList).indexOf(selectedCarpentry.getText())));
                params.put("concrete_forming", Integer.toString(Arrays.asList(experienceList).indexOf(selectedConcrete.getText())));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                params.put("Authorization", "Token " + settings.getString(Constants.AUTH_TOKEN, "noToken"));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(updateLabourerRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_availability) {
            startActivity(new Intent(LabourerProfileExperience.this, LabourerAvailability.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectCarpentry(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        Log.d("LabourerProfileExpCar",Boolean.toString(checked));
        switch (view.getId()) {
            case R.id.car_no:
            case R.id.car_yesthree:
            case R.id.car_yessix:
            case R.id.car_redseal:
                break;
        }

    }

    public void selectConcrete(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        Log.d("LabourerProfileExpCon", Boolean.toString(checked));
        switch (view.getId()) {
            case R.id.con_no:
            case R.id.con_yesthree:
            case R.id.con_yessix:
            case R.id.con_redseal:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {

        }

    }

    @Override
    public void onClick(View v) {

    }
}