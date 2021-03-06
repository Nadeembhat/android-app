package com.labourtoday.androidapp.contractor;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.labourtoday.androidapp.gcm.TokenRegistrationService;

import java.util.HashMap;
import java.util.Map;

public class ContractorRegistrationActivity extends AppCompatActivity {
    private static final String TAG = "ConRegActivity";
    private BroadcastReceiver tokenBroadcastReceiver; //listens for REGISTRATION_COMPLETE message from IDRegistrationService
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //Input fields for creating new User
    private EditText company, username, phone_number;
    private EditText password, passwordConfirm;
    private EditText first_name, last_name;
    private ProgressDialog progress;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_registration);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        company = (EditText) findViewById(R.id.edit_company_name);
        username = (EditText) findViewById(R.id.edit_email);
        password = (EditText) findViewById(R.id.edit_password);
        passwordConfirm = (EditText) findViewById(R.id.confirm_password);
        first_name = (EditText) findViewById(R.id.edit_first_name);
        last_name = (EditText) findViewById(R.id.edit_last_name);
        phone_number = (EditText) findViewById(R.id.edit_phone_number);

        progress = new ProgressDialog(this);
        progress.setMessage("Registering...");
    }

    /*
     *Submit the fields filled in by the user to the server to create a new user
     */
    public void registerUser(View view) {
        final String password_input = password.getText().toString();
        final String password_confirm = passwordConfirm.getText().toString();

        if (!password_input.equals(password_confirm)) {
            Toast.makeText(this, "Passwords do not match. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }

        if (isEmpty(username) || isEmpty(first_name) || isEmpty(last_name) || isEmpty(phone_number)
                || isEmpty(company)) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isValidEmail(username.getText().toString())) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
            return;
        }

        tokenBroadcastReceiver = new BroadcastReceiver() { // Wait for TokenRegistrationService to send you the RegistrationId from GCM
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                    return;
                }

                LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
                final String registrationId = intent.getExtras().getString(Constants.REGISTRATION_ID); // Device id obtained from GCM

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Constants.REGISTRATION_ID, registrationId);
                editor.putString(Constants.LAST_LOGIN, "contractor");
                editor.apply();

                String url = Constants.URLS.CONTRACTORS.string;

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Automatic login after registration using registered username and password
                                Intent loginIntent = new Intent(ContractorRegistrationActivity.this, ContractorLoginActivity.class);
                                loginIntent.setAction(Constants.ACTION_LOGIN);
                                loginIntent.putExtra(Constants.USERNAME, username.getText().toString());
                                loginIntent.putExtra(Constants.PASSWORD, password.getText().toString());
                                progress.dismiss();
                                startActivity(loginIntent);
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progress.dismiss();
                                Toast.makeText(getApplicationContext(), "Error registering. Please try again.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                )
                {
                    @Override
                    //Create the body of the request
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<>();
                        params.put(Constants.USERNAME, username.getText().toString());
                        params.put(Constants.PASSWORD, password_input);
                        // params.put(Constants.REGISTRATION_ID, registrationId);
                        params.put(Constants.FIRST_NAME, first_name.getText().toString());
                        params.put(Constants.LAST_NAME, last_name.getText().toString());
                        params.put(Constants.PHONE_NUMBER, formatPhoneNumber(phone_number.getText().toString()));
                        params.put(Constants.COMPANY_NAME, company.getText().toString());
                        params.put("Content-Type", "application/json");
                        return params;
                    }
                };
                Volley.newRequestQueue(getApplicationContext()).add(postRequest);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(tokenBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE)); //Set this receiver to look for the REGISTRATION_COMPLETE broadcast
        startIDRegistrationService(); //Start the service to obtain the device id
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(tokenBroadcastReceiver); //unregister this receiver if activity destroyed.
    }

    /**
     * Starts the (GCM) TokenRegistrationService if device has Google Play Services APK
     */
    public void startIDRegistrationService(){
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM and obtain deviceID
            Intent intent = new Intent(this, TokenRegistrationService.class);
            intent.setAction(Constants.ACTION_CREATE_USER);
            startService(intent);
        }
    }

    /**
     * Checks the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void signin(View view) {
        startActivity(new Intent(this, ContractorLoginActivity.class));
        finish();
    }

}

