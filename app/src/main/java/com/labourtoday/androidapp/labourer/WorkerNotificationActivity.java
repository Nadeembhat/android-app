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

public class WorkerNotificationActivity extends AppCompatActivity {
    private CheckBox email,text,call;
    private ArrayList<String> data;
    private SharedPreferences settings;
    private Drawer result;

    private ProgressDialog progress;
    private String action;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_notification);

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

        email = (CheckBox) findViewById(R.id.chkEmail);
        text = (CheckBox) findViewById(R.id.chkText);
        call = (CheckBox) findViewById(R.id.chkCall);
        data = getIntent().getStringArrayListExtra("data");

        try {
            action = getIntent().getAction();
            email.setChecked(Boolean.parseBoolean(getIntent().getStringExtra("email")));
            text.setChecked(Boolean.parseBoolean(getIntent().getStringExtra("sms")));
            call.setChecked(Boolean.parseBoolean(getIntent().getStringExtra("phone")));
        } catch (Exception e) {
            action = "";
        }

        if (action == null)
            action = "";

        progress = new ProgressDialog(this);
        progress.setMessage("Updating profile");
    }

    public void next(View v) {
        String pref_phone = "\"phone_preference\":" + call.isChecked();
        String pref_email = "\"email_preference\":" + email.isChecked();
        String pref_sms = "\"sms_preference\":" + text.isChecked();

        Log.i("WorkerPhone", pref_phone);
        Log.i("WorkerEmail", pref_email);
        Log.i("WorkerSms", pref_sms);

        if (action.equals("")) {
            Intent equipmentIntent = new Intent(this, LabourerEquipActivity.class);
            data.add(3, pref_email);
            data.add(4, pref_phone);
            data.add(5, pref_sms);
            equipmentIntent.putStringArrayListExtra("data", data);
            startActivity(equipmentIntent);
        } else {
            progress.show();
            String url = Constants.URLS.WORKER_DETAIL.string;
            JsonObjectRequest workerRequest;
            StringBuilder req = new StringBuilder();
            req.append("{");
            req.append(pref_phone);
            req.append(",");
            req.append(pref_email);
            req.append(",");
            req.append(pref_sms);
            req.append("}");
            try {
                workerRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(req.toString()),
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
