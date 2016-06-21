package com.labourtoday.androidapp.labourer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class WorkerCitiesActivity extends AppCompatActivity {
    private Drawer result;
    private SharedPreferences settings;

    private String cities;
    private HashMap<Integer, String> data;
    private CheckBox van,burn,sur,newWest,coq,northShore,rich,delta;
    private ArrayList<CheckBox> listCities;

    private ProgressDialog progress;
    private String action, profileData;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_cities);

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
        listCities = new ArrayList<>();

        van = (CheckBox) findViewById(R.id.chkVan);
        burn = (CheckBox) findViewById(R.id.chkBurn);
        sur = (CheckBox) findViewById(R.id.chkSur);
        newWest = (CheckBox) findViewById(R.id.chkNewWest);
        coq = (CheckBox) findViewById(R.id.chkCoq);
        northShore = (CheckBox) findViewById(R.id.chkNorthShore);
        rich = (CheckBox) findViewById(R.id.chkRich);
        delta = (CheckBox) findViewById(R.id.chkDelta);
        listCities.add(van);
        listCities.add(burn);
        listCities.add(sur);
        listCities.add(newWest);
        listCities.add(coq);
        listCities.add(northShore);
        listCities.add(rich);
        listCities.add(delta);

        cities = "\"cities\":[";

        try {
            action = getIntent().getAction();
            profileData = getIntent().getStringExtra("cities");
            jsonArray = new JSONArray(profileData);
            for (int i = 0; i < jsonArray.length(); i++) {
                listCities.get(jsonArray.getInt(i) - 1).setChecked(true);
            }
        } catch (Exception e) {
            action = "";
            profileData = "";
        }

        progress = new ProgressDialog(this);
        progress.setMessage("Updating profile");
    }

    public void next(View v) {
        cities = cities.substring(0, 10);
        int citiesChecked = 0;
        for (int i = 0; i < listCities.size(); i++) {
            CheckBox box = listCities.get(i);
            if (box.isChecked()) {
                cities += "\"" + box.getText().toString() + "\",";
                citiesChecked++;
            }
        }
        if (citiesChecked == 0) {
            Toast.makeText(getApplicationContext(), "Please select at least one city and day", Toast.LENGTH_SHORT).show();
            return;
        }
        cities = cities.substring(0, cities.length() - 1);
        cities += "]";

        Log.i("WorkerCities", cities);
        if (action.equals("")) {
            Intent notificationIntent = new Intent(this, WorkerNotificationActivity.class);
            data.put(2, cities);
            notificationIntent.putExtra("data", data);
            startActivity(notificationIntent);
        } else {
            progress.show();
            String url = Constants.URLS.WORKER_DETAIL.string;
            JsonObjectRequest workerRequest;
            cities = "{" + cities;
            cities += "}";
            try {
                workerRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(cities),
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
