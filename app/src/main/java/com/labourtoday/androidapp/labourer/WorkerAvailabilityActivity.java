package com.labourtoday.androidapp.labourer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkerAvailabilityActivity extends AppCompatActivity {
    private Drawer result;
    private SharedPreferences settings;
    private CheckBox mon,tues,wed,thurs,fri,sat,sun;

    private String availability;
    private HashMap<Integer, String> data;
    private ArrayList<CheckBox> days;

    private ProgressDialog progress;
    private String action, profileData;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_availability);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        result = new DrawerBuilder().withActivity(this).withToolbar(toolbar)
                .withSavedInstance(savedInstanceState).build();
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        data = (HashMap<Integer, String>) getIntent().getSerializableExtra("data");
        days = new ArrayList<>();
        mon = (CheckBox) findViewById(R.id.chkMon);
        tues = (CheckBox) findViewById(R.id.chkTues);
        wed = (CheckBox) findViewById(R.id.chkWed);
        thurs = (CheckBox) findViewById(R.id.chkThurs);
        fri = (CheckBox) findViewById(R.id.chkFri);
        sat = (CheckBox) findViewById(R.id.chkSat);
        sun = (CheckBox) findViewById(R.id.chkSun);
        days.add(mon);
        days.add(tues);
        days.add(wed);
        days.add(thurs);
        days.add(fri);
        days.add(sat);
        days.add(sun);

        availability = "\"availability\":[";

        try {
            action = getIntent().getAction();
            profileData = getIntent().getStringExtra("availability");
            jsonArray = new JSONArray(profileData);
            for (int i = 0; i < jsonArray.length(); i++) {
                days.get(jsonArray.getInt(i) - 1).setChecked(true);
            }
        } catch (Exception e) {
            action = "";
            profileData = "";
        }

        progress = new ProgressDialog(this);
        progress.setMessage("Updating profile");
    }

    public void next(View v) {
        availability = availability.substring(0, 16);
        int daysChecked = 0;
        for (int i = 0; i < days.size(); i++) {
            CheckBox box = days.get(i);
            if (box.isChecked()) {
                availability += "\"" + box.getText().toString() + "\",";
                daysChecked++;
            }
        }
        if (daysChecked == 0) {
            Toast.makeText(getApplicationContext(), "Please select at least one city and day", Toast.LENGTH_SHORT).show();
            return;
        }
        availability = availability.substring(0, availability.length() - 1);
        availability += "]";

        // Log.i("WorkerDays", availability);
        if (action.equals("")) {
            Intent citiesIntent = new Intent(this, WorkerCitiesActivity.class);
            data.put(1, availability);
            citiesIntent.putExtra("data", data);
            startActivity(citiesIntent);
        } else {
            progress.show();
            String url = Constants.URLS.WORKER_DETAIL.string;
            JsonObjectRequest workerRequest;
            availability = "{" + availability;
            availability += "}";
            try {
                workerRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(availability),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progress.dismiss();
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Request error. Please try again", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization","Token " + settings.getString(Constants.AUTH_TOKEN, ""));
                        return headers;
                    }
                };
            } catch (JSONException e) {
                Toast.makeText(this, "Request error. Please try again", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                return;
            }

            Volley.newRequestQueue(getApplicationContext()).add(workerRequest);
        }
    }
}
