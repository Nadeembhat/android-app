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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.MainActivity;
import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WorkerProfileActivity extends AppCompatActivity {
    private SharedPreferences settings;
    private Drawer result;
    private ProgressDialog progress;
    private JSONObject worker;
    private TextView nameWorker, cityWorker;
    private ListView listWorker;
    private final String[] workerArray = {"Experience", "Equipment & qualifications",
            "Availability", "Areas I can work", "Notifications", "References"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        nameWorker = (TextView) findViewById(R.id.name_worker);
        cityWorker = (TextView) findViewById(R.id.city_worker);
        listWorker = (ListView) findViewById(R.id.list_worker);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_worker, workerArray);
        listWorker.setAdapter(adapter);
        listWorker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:
                        Intent i0 = new Intent(getApplicationContext(), LabourerGridActivity.class);
                        i0.setAction(Constants.ACTION_UPDATE_IMMEDIATE);
                        try {
                            i0.putExtra("skills", worker.get("skills").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(i0);
                        break;
                    case 1:
                        Intent i1 = new Intent(getApplicationContext(), LabourerEquipActivity.class);
                        i1.setAction(Constants.ACTION_UPDATE_IMMEDIATE);
                        startActivity(i1);
                        break;
                    case 2:
                        Intent i2 = new Intent(getApplicationContext(), WorkerAvailabilityActivity.class);
                        i2.setAction(Constants.ACTION_UPDATE_IMMEDIATE);
                        startActivity(i2);
                        break;
                    case 3:
                        Intent i3 = new Intent(getApplicationContext(), WorkerCitiesActivity.class);
                        i3.setAction(Constants.ACTION_UPDATE_IMMEDIATE);
                        startActivity(i3);
                        break;

                    case 4:
                        Intent i4 = new Intent(getApplicationContext(), WorkerNotificationActivity.class);
                        i4.setAction(Constants.ACTION_UPDATE_IMMEDIATE);
                        startActivity(i4);
                        break;
                    case 5:
                        Intent i5 = new Intent(getApplicationContext(), ReferenceActivity.class);
                        i5.setAction(Constants.ACTION_UPDATE_IMMEDIATE);
                        startActivity(i5);
                        break;
                }
            }
        });

        progress = new ProgressDialog(this);
        progress.setMessage("Loading profile...");
        progress.show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (!settings.getString(Constants.AUTH_TOKEN, "").equals("") &&
                settings.getString(Constants.LAST_LOGIN, "").equals(Constants.WORKER)) {
            result = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withSavedInstance(savedInstanceState)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName("Sign out")
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            switch (position) {
                                case 0:
                                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                    settings.edit().remove(Constants.LAST_LOGIN).apply();
                                    Intent mainIntent = new Intent(WorkerProfileActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    })
                    .build();
        }

        String url = Constants.URLS.WORKERS.string;
        JsonObjectRequest workerRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        worker = response;
                        Log.i("Worker", worker.toString());
                        try {
                            nameWorker.setText(worker.getString("first_name") + " "
                                    + worker.getString("last_name"));
                            cityWorker.setText(worker.getString("city"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

        Volley.newRequestQueue(getApplicationContext()).add(workerRequest);
    }
}
