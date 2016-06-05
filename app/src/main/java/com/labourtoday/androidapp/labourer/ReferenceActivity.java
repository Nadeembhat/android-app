package com.labourtoday.androidapp.labourer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.labourtoday.androidapp.R;

import java.util.ArrayList;

public class ReferenceActivity extends AppCompatActivity {
    private EditText company_one, name_one, title_one, phone_one,
                        company_two, name_two, title_two, phone_two;
    private SharedPreferences settings;
    private ProgressDialog progress;
    private ArrayList<String> finalData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        company_one = (EditText) findViewById(R.id.edit_company_name);
        name_one = (EditText) findViewById(R.id.edit_name);
        title_one = (EditText) findViewById(R.id.edit_title);
        phone_one = (EditText) findViewById(R.id.edit_phone_number);
        company_two = (EditText) findViewById(R.id.edit_company_name2);
        name_two = (EditText) findViewById(R.id.edit_name2);
        title_two = (EditText) findViewById(R.id.edit_title2);
        phone_two = (EditText) findViewById(R.id.edit_phone_number2);

        progress = new ProgressDialog(ReferenceActivity.this);
        progress.setMessage("Processing Request...");
    }

    public void next(View v) {
        /*
        progress.show();
        finalData = new ArrayList<>();
        finalData.add("Your Information:");
        finalData.addAll(settings.getStringSet("labourerDetail", new HashSet<String>()));
        finalData.add("Your Experiences:");
        finalData.addAll(getIntent().getStringArrayListExtra("data"));
        finalData.add("Availability Information:");
        finalData.addAll(settings.getStringSet("availability", new HashSet<String>()));
        finalData.add("Equipment/Certification Information:");
        finalData.addAll(settings.getStringSet("equipment", new HashSet<String>()));
        finalData.add("Notification Preference:");
        finalData.addAll(settings.getStringSet("notificationPreference", new HashSet<String>()));

        finalData.add("Your References:");
        finalData.add("Reference 1 Company Name: " + company_one.getText().toString());
        finalData.add("Reference 1 Name: " + name_one.getText().toString());
        finalData.add("Reference 1 Title: " + title_one.getText().toString());
        finalData.add("Reference 1 Phone: " + phone_one.getText().toString());
        finalData.add("Reference 2 Company Name: " + company_two.getText().toString());
        finalData.add("Reference 2 Name: " + name_two.getText().toString());
        finalData.add("Reference 2 Title: " + title_two.getText().toString());
        finalData.add("Reference 2 Phone: " + phone_two.getText().toString());
        */
        startActivity(new Intent(this, LabourerMessageActivity.class));
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
}
