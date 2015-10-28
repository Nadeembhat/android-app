package com.labourtoday.androidapp.contractor;

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

public class HiringActivity extends AppCompatActivity {
    private final int PROFILE = 0;
    private final int LOG_OUT = 1;

    private Drawer result;
    String[] experienceList = new String[]{"No", "Yes 1-3 months", "Yes > 6 months", "Red Seal"};

    private RadioGroup carpentryRadioGroup;
    private RadioGroup concreteRadioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiring);

        carpentryRadioGroup = (RadioGroup) findViewById(R.id.carpentry_radio);
        concreteRadioGroup = (RadioGroup) findViewById(R.id.concrete_radio);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Labour Today");

        // setting switch
        Switch mySwitch = (Switch) findViewById(R.id.gen_labour_text);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

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
                                Intent hireIntent = new Intent(HiringActivity.this, ContractorProfileActivity.class);
                                startActivity(hireIntent);
                                return true;
                            case LOG_OUT:
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                settings.edit().remove(Constants.LAST_LOGIN).apply();
                                // Return to the welcome page
                                Intent welcomeIntent = new Intent(HiringActivity.this, ContractorLoginActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_hiring, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_pay) {
            startActivity(new Intent(HiringActivity.this, PaymentActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public String generateJobCode() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent i = getIntent();
        String jobCode = sharedPreferences.getString(Constants.CONTRACTOR, null).substring(0, 4);
        jobCode += i.getStringExtra("start_date");
        jobCode += i.getStringExtra("start_time");
        jobCode = jobCode.replaceAll("\\s+","");
        Log.d("generateJobCode", jobCode);
        sharedPreferences.edit().putString("job_code", jobCode).apply();
        return jobCode;
    }

    public void requestWorker(View view) {
        String url = Constants.URLS.LABOURER_SEARCH.string;
        final RadioButton selectedCarpentry = (RadioButton) findViewById(carpentryRadioGroup.getCheckedRadioButtonId());
        final RadioButton selectedConcrete = (RadioButton) findViewById(concreteRadioGroup.getCheckedRadioButtonId());
        StringRequest updateDeviceRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Worker requested. We will get back to you soon.",
                                Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(HiringActivity.this, ContractorProfileActivity.class));
                        //finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "There are no workers available at the moment. Please try again soon.",
                                Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                Intent i = getIntent();

                params.put("carpentry", Integer.toString(Arrays.asList(experienceList).indexOf(selectedCarpentry.getText())));
                params.put("concrete_forming", Integer.toString(Arrays.asList(experienceList).indexOf(selectedConcrete.getText())));
                params.put("start_date", i.getStringExtra("start_date"));
                params.put("start_time", i.getStringExtra("start_time"));
                params.put("job_address", i.getStringExtra("job_address"));
                params.put("job_code", generateJobCode());

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
        Volley.newRequestQueue(getApplicationContext()).add(updateDeviceRequest);
    }

}



