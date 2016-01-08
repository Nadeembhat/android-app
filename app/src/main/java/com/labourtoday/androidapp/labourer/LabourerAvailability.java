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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.labourtoday.androidapp.contractor.ContractorLoginActivity;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class LabourerAvailability extends AppCompatActivity {
    private Drawer result;
    private final int PROFILE = 0;
    private final int LOG_OUT = 1;
    private RadioGroup hat, vest, tool;
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

        hat = (RadioGroup) findViewById(R.id.hat_radio);
        vest = (RadioGroup) findViewById(R.id.vest_radio);
        tool = (RadioGroup) findViewById(R.id.tool_radio);

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
        Intent prev = getIntent();
        Intent i = new Intent(LabourerAvailability.this, LabourerRegistrationActivity.class);
        RadioButton hatButton = (RadioButton) findViewById(hat.getCheckedRadioButtonId());
        RadioButton vestButton = (RadioButton) findViewById(vest.getCheckedRadioButtonId());
        RadioButton toolButton = (RadioButton) findViewById(tool.getCheckedRadioButtonId());
        i.putExtra(Constants.AVAILABILITY, getAvailabilityParam());
        i.putExtra("general_labour", prev.getStringExtra("general_labour"));
        i.putExtra("carpentry", prev.getStringExtra("carpentry"));
        i.putExtra("concrete", prev.getStringExtra("concrete"));
        i.putExtra("landscaping", prev.getStringExtra("landscaping"));
        i.putExtra("painting", prev.getStringExtra("painting"));
        i.putExtra("drywalling", prev.getStringExtra("drywalling"));
        i.putExtra("roofing", prev.getStringExtra("roofing"));
        i.putExtra("machine_operation", prev.getStringExtra("machine_operation"));
        i.putExtra("plumbing", prev.getStringExtra("plumbing"));
        i.putExtra("electrical", prev.getStringExtra("electrical"));
        i.putExtra("hat", yesNo(hatButton.getText().toString()));
        i.putExtra("vest", yesNo(vestButton.getText().toString()));
        i.putExtra("tool", yesNo(toolButton.getText().toString()));
        startActivity(i);
        /*
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
                Log.d("LabourerAvailability", getAvailabilityParam());
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
        */
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
        return boolToString(sharedPreferences.getBoolean(Constants.NOW, false)) +
                boolToString(sharedPreferences.getBoolean(Constants.MON, false)) +
                boolToString(sharedPreferences.getBoolean(Constants.TUES, false)) +
                boolToString(sharedPreferences.getBoolean(Constants.WED, false)) +
                boolToString(sharedPreferences.getBoolean(Constants.THURS, false)) +
                boolToString(sharedPreferences.getBoolean(Constants.FRI, false)) +
                boolToString(sharedPreferences.getBoolean(Constants.SAT, false)) +
                boolToString(sharedPreferences.getBoolean(Constants.SUN, false));
    }

    private String boolToString(boolean generalLabour) {
        if (generalLabour)
            return "T";
        else
            return "F";
    }

    private String yesNo(String param) {
        if (param.equals("Yes")) {
            return "1";
        } else {
            return "0";
        }
    }



}