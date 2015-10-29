package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LabourerProfileActivity extends AppCompatActivity {

    private Switch generalLabourSwitch;

    private Drawer result;
    private final int PROFILE = 0;
    private final int LOG_OUT = 1;

    private RadioGroup carpentryRadioGroup;
    private RadioGroup concreteRadioGroup;

    private SharedPreferences settings;

    String[] experienceList = new String[]{"No", "Yes 1-3 months", "Yes > 6 months", "Red Seal"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_profile);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        carpentryRadioGroup = (RadioGroup) findViewById(R.id.carpentry_radio);
        concreteRadioGroup = (RadioGroup) findViewById(R.id.concrete_radio);

        carpentryRadioGroup.check(settings.getInt(Constants.CARPENTRY, R.id.car_no));
        concreteRadioGroup.check(settings.getInt(Constants.CONCRETE, R.id.con_no));

        // setting switch
        generalLabourSwitch = (Switch) findViewById(R.id.gen_labour_text);
        generalLabourSwitch.setChecked(settings.getBoolean(Constants.GENERAL_LABOUR, false));
        generalLabourSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.edit().putBoolean(Constants.GENERAL_LABOUR, isChecked).apply();
            }
        });

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
                                Intent hireIntent = new Intent(LabourerProfileActivity.this, LabourerEditActivity.class);
                                startActivity(hireIntent);
                                return true;
                            case LOG_OUT:
                                settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                settings.edit().remove(Constants.LAST_LOGIN).apply();
                                Intent welcomeIntent = new Intent(LabourerProfileActivity.this, LabourerLoginActivity.class);
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
                        settings.edit().putInt(Constants.CARPENTRY, carpentryRadioGroup.getCheckedRadioButtonId()).apply();
                        settings.edit().putInt(Constants.CONCRETE, concreteRadioGroup.getCheckedRadioButtonId()).apply();
                        Toast.makeText(getApplicationContext(), "Successfully updated profile", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Please Indicate All Experiences",
                                Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("carpentry", Integer.toString(Arrays.asList(experienceList).indexOf(selectedCarpentry.getText())));
                params.put("concrete_forming", Integer.toString(Arrays.asList(experienceList).indexOf(selectedConcrete.getText())));
                params.put("general_labour", Integer.toString(booleanToInt(settings.getBoolean(Constants.GENERAL_LABOUR, false))));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Token " + settings.getString(Constants.AUTH_TOKEN, "noToken"));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(updateLabourerRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_availability) {
            startActivity(new Intent(LabourerProfileActivity.this, LabourerAvailability.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        }
    }

    private int booleanToInt(boolean generalLabour) {
        if (generalLabour)
            return 1;
        else
            return 0;
    }
}