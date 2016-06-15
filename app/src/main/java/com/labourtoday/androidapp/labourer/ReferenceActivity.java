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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReferenceActivity extends AppCompatActivity {
    private EditText company_one, name_one, title_one, phone_one,
                        company_two, name_two, title_two, phone_two;
    private SharedPreferences settings;
    private ProgressDialog progress;
    private ArrayList<String> data;
    private String references;
    private Drawer result;

    private JSONObject requestObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference);
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

        data = getIntent().getStringArrayListExtra("data");
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

        progress = new ProgressDialog(ReferenceActivity.this);
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
        references += "\",\"title\":";
        references += title_one.getText().toString();
        references += "\",\"name\":";
        references += name_one.getText().toString();
        references += "\",\"phone_number\":";
        references += phone_one.getText().toString();
        references += "},";

        references += "{\"company\":\"";
        references += company_two.getText().toString();
        references += "\",\"title\":";
        references += title_two.getText().toString();
        references += "\",\"name\":";
        references += name_two.getText().toString();
        references += "\",\"phone_number\":";
        references += phone_two.getText().toString();
        references += "}]";
        data.add(7, references);

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
                headers.put("Authorization","Token " + settings.getString(Constants.AUTH_TOKEN, ""));
                return headers;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(workerRequest);
        /*
        String url = Constants.URLS.EMAIL_LABOURER.string;
        StringRequest workerRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        startActivity(new Intent(getApplicationContext(), LabourerMessageActivity.class));
                        progress.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), "Unable to send request",
                                Toast.LENGTH_LONG).show();
                        Log.i("WHTATEFE", error.toString());
                    }
                }
        ) {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Get the registration info from input fields and add them to the body of the request
                String param = "";
                for (int i = 0; i < finalData.size(); i++) {
                    param += finalData.get(i);
                    param += ",";
                }
                param += "\n";
                params.put("data", param);
                params.put("email", settings.getString("labourerEmail", ""));
                params.put("Content-Type", "application/json");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(workerRequest);
        */
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }


}
