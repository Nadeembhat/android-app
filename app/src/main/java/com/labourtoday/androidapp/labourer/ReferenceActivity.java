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
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReferenceActivity extends AppCompatActivity {
    private EditText company_one, name_one, title_one, phone_one,
                        company_two, name_two, title_two, phone_two;
    private SharedPreferences settings;
    private ProgressDialog progress;
    private HashMap<Integer, String> data;
    private String references, action;
    private Drawer result;

    private JSONObject requestObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        progress = new ProgressDialog(ReferenceActivity.this);
        data = (HashMap<Integer, String>) getIntent().getSerializableExtra("data");
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        company_one = (EditText) findViewById(R.id.edit_company_name);
        name_one = (EditText) findViewById(R.id.edit_name);
        title_one = (EditText) findViewById(R.id.edit_title);
        phone_one = (EditText) findViewById(R.id.edit_phone_number);
        company_two = (EditText) findViewById(R.id.edit_company_name2);
        name_two = (EditText) findViewById(R.id.edit_name2);
        title_two = (EditText) findViewById(R.id.edit_title2);
        phone_two = (EditText) findViewById(R.id.edit_phone_number2);

        references = "\"references\":[";
        try {
            action = getIntent().getAction();
        } catch (Exception e) {
            action = "";
        }

        if (action == null) {
            action = "";
        }

        if (action.equals(Constants.ACTION_UPDATE_IMMEDIATE)) {
            progress.setMessage("Fetching references");
            progress.show();
            String url = Constants.URLS.REFERENCES.string;
            JsonArrayRequest workerRequest = new JsonArrayRequest(Request.Method.GET, url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject one = (JSONObject) response.get(0);
                                company_one.setText((String) one.get("company"));
                                name_one.setText((String) one.get("name"));
                                title_one.setText((String) one.get("title"));
                                phone_one.setText((String) one.get("phone_number"));
                                JSONObject two = (JSONObject) response.get(1);
                                company_two.setText((String) two.get("company"));
                                name_two.setText((String) two.get("name"));
                                title_two.setText((String) two.get("title"));
                                phone_two.setText((String) two.get("phone_number"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progress.dismiss();
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

        progress.setMessage("Updating your profile...");
    }

    public void next(View v) {
        if (isEmpty(company_one) || isEmpty(title_one) || isEmpty(name_one) || isEmpty(phone_one)) {
            Toast.makeText(this, "Please fill out all fields for reference #1", Toast.LENGTH_LONG).show();
            return;
        }
        if (isEmpty(company_two) || isEmpty(title_two) || isEmpty(name_two) || isEmpty(phone_two)) {
            Toast.makeText(this, "Please fill out all fields for reference #2", Toast.LENGTH_LONG).show();
            return;
        }
        progress.show();

        references = references.substring(0, 14);
        references += "{\"company\":\"";
        references += company_one.getText().toString();
        references += "\",\"title\":\"";
        references += title_one.getText().toString();
        references += "\",\"name\":\"";
        references += name_one.getText().toString();
        references += "\",\"phone_number\":";
        references += phone_one.getText().toString();
        references += "},";

        references += "{\"company\":\"";
        references += company_two.getText().toString();
        references += "\",\"title\":\"";
        references += title_two.getText().toString();
        references += "\",\"name\":\"";
        references += name_two.getText().toString();
        references += "\",\"phone_number\":";
        references += phone_two.getText().toString();
        references += "}]";

        if (action.equals("")) {
            data.put(7, references);
            String jsonString = "{";
            for (int i = 0; i < data.size(); i++) {
                jsonString += data.get(i) + ",";
                Log.i("FINAL " + Integer.toString(i), data.get(i));
            }
            jsonString = jsonString.substring(0, jsonString.length() - 1);
            jsonString += "}";

            try {
                requestObj = new JSONObject(jsonString);
            } catch (JSONException e) {
                Toast.makeText(this, "Request error. Please try again", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                return;
            }

            String url = Constants.URLS.WORKER_DETAIL.string;
            JsonObjectRequest workerRequest = new JsonObjectRequest(Request.Method.PUT, url, requestObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Intent i = new Intent(getApplicationContext(), WorkerProfileActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            progress.dismiss();
                            startActivity(i);
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
                    headers.put("Authorization", "Token " + settings.getString(Constants.AUTH_TOKEN, ""));
                    return headers;
                }
            };

            Volley.newRequestQueue(getApplicationContext()).add(workerRequest);
        } else {
            progress.show();
            String url = Constants.URLS.WORKER_DETAIL.string;
            JsonObjectRequest workerRequest;
            references = "{" + references;
            references += "}";
            try {
                workerRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(references),
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

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }


}
