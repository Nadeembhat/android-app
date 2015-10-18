package com.labourtoday.androidapp.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;


public class TokenRegistrationService extends IntentService {

    private static final String TAG = "TokenRegistrationService";

    public TokenRegistrationService() {
        super("TokenRegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String regId = null;
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                regId = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send token to server,
                sharedPreferences.edit().putBoolean(Constants.GCM_REGISTRATION_TOKEN_SENT, true).apply();
            }
        } catch(Exception e) {
            sharedPreferences.edit().putBoolean(Constants.GCM_REGISTRATION_TOKEN_SENT, false).apply();
        }

        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        registrationComplete.putExtra(Constants.REGISTRATION_ID, regId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }
}
