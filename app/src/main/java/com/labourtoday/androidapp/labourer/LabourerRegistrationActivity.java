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
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.labourtoday.androidapp.gcm.TokenRegistrationService;

import java.util.HashSet;
import java.util.Set;

public class LabourerRegistrationActivity extends AppCompatActivity {
    private BroadcastReceiver tokenBroadcastReceiver; //listens for REGISTRATION_COMPLETE message from IDRegistrationService

    private final String TAG = "WorkerRegAct";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    //Input fields for creating new User
    private EditText username;
    // private EditText password, passwordConfirm;
    private EditText first_name, last_name, phone_number;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_registration);

        username = (EditText) findViewById(R.id.edit_email);
        // password = (EditText) findViewById(R.id.edit_password);
        // passwordConfirm = (EditText) findViewById(R.id.confirm_password);
        first_name = (EditText) findViewById(R.id.edit_first_name);
        last_name = (EditText) findViewById(R.id.edit_last_name);
        phone_number = (EditText) findViewById(R.id.edit_phone_number);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void registerUser(View view) {
        /*
        final String password_input = password.getText().toString();
        final String password_confirm = passwordConfirm.getText().toString();

        if (!password_input.equals(password_confirm)) {
            Toast.makeText(this, "Passwords do not match. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }
        */
        if (isEmpty(username) || isEmpty(first_name) || isEmpty(last_name) || isEmpty(phone_number)) {
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
                LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
                final String registrationId = intent.getExtras().getString(Constants.REGISTRATION_ID); // Device id obtained from GCM
                settings.edit().putString(Constants.REGISTRATION_ID, registrationId).apply();

                setActivityData();
                Intent gridIntent = new Intent(LabourerRegistrationActivity.this, LabourerGridActivity.class);
                startActivity(gridIntent);
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
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Formats a Canada/US phone number in the proper format that the server can use
     * @param phoneNumber
     *          Unformatted phone number
     * @return
     *          Formatted phone number
     */
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

    public void setActivityData() {
        Set<String> data = new HashSet<>();
        data.add("Name: " + first_name.getText().toString() + " " + last_name.getText().toString());
        // data.add("Password: " + password.getText().toString());
        data.add("Phone Number: " + formatPhoneNumber(phone_number.getText().toString()));
        settings.edit().putStringSet("labourerDetail", data).apply();
        settings.edit().putString("labourerEmail", username.getText().toString()).apply();
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

}
