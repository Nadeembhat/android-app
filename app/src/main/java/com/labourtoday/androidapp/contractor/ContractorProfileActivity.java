package com.labourtoday.androidapp.contractor;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContractorProfileActivity extends AppCompatActivity {
    private ProgressDialog progress;
    //Input fields for creating new User
    private EditText company, username, phoneNumber;
    private EditText password, passwordConfirm;
    private EditText first_name, last_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_profile);
        progress = new ProgressDialog(ContractorProfileActivity.this);
        progress.setMessage("Updating your profile...");

        //Assign input fields to instance variables
        company = (EditText) findViewById(R.id.edit_company_name);
        username = (EditText) findViewById(R.id.edit_email);
        password = (EditText) findViewById(R.id.edit_password);
        first_name = (EditText) findViewById(R.id.edit_first_name);
        last_name = (EditText) findViewById(R.id.edit_last_name);
        phoneNumber = (EditText) findViewById(R.id.edit_phone_number);
        get();
    }

    public void get() {
        String url = Constants.URLS.CONTRACTOR_LIST.string;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            company.setText(response.getString("company_name"));
                            username.setText(response.getString("username"));
                            first_name.setText(response.getString("first_name"));
                            last_name.setText(response.getString("last_name"));
                            phoneNumber.setText(response.getString("phone_number"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        Volley.newRequestQueue(this).add(getRequest);
    }

    public void set() {
        String url = Constants.URLS.CONTRACTOR_DETAIL.string;
        final String password_input = password.getText().toString();
        final String password_confirm = passwordConfirm.getText().toString();

        if (!password_input.equals(password_confirm)) {
            Toast.makeText(this, "Passwords do not match. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }

        progress.show();
        StringRequest postRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Automatic login after registration using registered username and password
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), "Successfully updated information", Toast.LENGTH_LONG);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        )
        {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                // Get the registration info from input fields and add them to the body of the request
                params.put(Constants.COMPANY_NAME, company.getText().toString());
                params.put("username", username.getText().toString());
                params.put(Constants.PASSWORD, password_input);
                params.put(Constants.FIRST_NAME, first_name.getText().toString());
                params.put(Constants.LAST_NAME, last_name.getText().toString());
                params.put(Constants.PHONE_NUMBER, formatPhoneNumber(phoneNumber.getText().toString()));
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
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    public String formatPhoneNumber(String phoneNumber) {
        char[] array = phoneNumber.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (!Character.isDigit(array[i])) {
                array[i] = ' ';
            }
        }
        String formatted = new String(array);
        return "+1" + formatted.replaceAll("\\s+", "");
    }
}


