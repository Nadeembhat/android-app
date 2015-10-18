package com.labourtoday.androidapp.labourer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LabourerLoginActivity extends AppCompatActivity {

    private final String TAG = "LabourRegFragment";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    //Input fields for creating new User
    private EditText username, password;

    private String registrationId = Constants.NO_DEVICE; //If regular login (not from RegistrationActivity), used to check if user is on a different device. No device by default
    private BroadcastReceiver tokenBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_login);

        username = (EditText) findViewById(R.id.edit_email);
        password = (EditText) findViewById(R.id.edit_password);

        Intent intent = getIntent();
        // If LoginActivity was started from RegistrationActivity, retrieve passed username/password to login automatically
        if (intent.getAction() != null && intent.getAction().equals(Constants.ACTION_LOGIN)) {
            String username_input = intent.getExtras().getString("username");
            String password_input = intent.getExtras().getString(Constants.PASSWORD);
            registrationId = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.REGISTRATION_ID, Constants.NO_DEVICE); //Assign old ID to pass the check for different device, since this code occurs right after RegistrationActivity
            authenticate(username_input, password_input, false);
        }
        // Manual login. Must retrieve new device ID from GCM in case user has switched devices.
        else {
        /*Obtain device ID from GCM so we can check it against the stored ID in preferences and see if user is on a different device.*/
            tokenBroadCastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                        return;
                    }
                    LocalBroadcastManager.getInstance(context).unregisterReceiver(this); // unregister this receiver once the deviceID has been obtained
                    registrationId = intent.getExtras().getString(Constants.REGISTRATION_ID); // Device id obtained from GCM
                }
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(tokenBroadCastReceiver,
                    new IntentFilter(Constants.REGISTRATION_COMPLETE)); // Set this receiver to look for the REGISTRATION_COMPLETE broadcast
            startIDRegistrationService(); //start service to obtain the device id from GCM
        }
    }
    /**
     * Login button handler method
     * @param view
     *          the login button
     */
    public void labourerLogin(View view) {
        authenticate(username.getText().toString(), password.getText().toString(), true);
    }

    /**
     * Registration button handler method
     */
    public void labourerRegister(View v) {
        startActivity(new Intent(getApplicationContext(), LabourerRegistrationActivity.class));
    }

    /**
     * Authenticates the user
     * @param username
     *          the username of the account
     * @param password
     *          password of the account
     * @param update
     *          indication of whether the device registration ID must be updated
     */
    public void authenticate(final String username, final String password, final boolean update) {
        /*In the case that obtaining device id somehow failed, do not attempt to login*/
        if (registrationId.equals(Constants.NO_DEVICE)){
            Toast.makeText(getApplicationContext(), "Error: Failed to obtain device id. Please check your connection", Toast.LENGTH_LONG).show();
            recreate(); //Restart the activity so we can try to get device id again.
        }
        else {
            String url = Constants.URLS.TOKEN_AUTH.string;
            Log.d(TAG, username);
            Log.d(TAG, password);
            Log.d(TAG, url);
            StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                String token = jsonResponse.getString(Constants.AUTH_TOKEN);

                                // Store the token in SharedPreferences
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(Constants.AUTH_TOKEN, token);
                                editor.putString(Constants.LAST_LOGIN, Constants.LABOURER);
                                editor.apply();

                                Intent homeIntent = new Intent(getApplicationContext(), LabourerProfileExperience.class); //Prepare intent to go to Home Screen

                                /*On regular login (not from registration), need to update device ID on server*/
                                if (update) {
                                    updateDeviceID(homeIntent); //Update device ID on server, and start HomeActivity after its successful
                                }
                                /*Arrived here from RegistrationActivity, no need to update device id*/
                                else {
                                    startActivity(homeIntent);
                                    finish();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "Failed to login");
                        }
                    }
            ) {
                @Override
                //Create the body of the request
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // Get the registration info from input fields and add them to the body of the request
                    params.put("username", username);
                    params.put(Constants.PASSWORD, password);
                    return params;
                }
            };

            Volley.newRequestQueue(getApplicationContext()).add(loginRequest);
        }
    }

    /**
     * Updates the user's device id on the server, and starts the UserProfileActivity on success.
     */
    private void updateDeviceID(final Intent homeIntent) {
    /*Send a user-detail request to update userprofile with new device ID, and store in preferences*/
        String url = Constants.URLS.LABOURER_DETAIL.string;
        StringRequest updateDeviceRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Updated device ID" );
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Constants.REGISTRATION_ID, registrationId);
                        editor.apply();

                        startActivity(homeIntent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,"error updating registration ID");
                    }
                }
        )
        {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.REGISTRATION_ID, registrationId);
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(tokenBroadCastReceiver); //unregister this receiver if activity destroyed.
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
}
