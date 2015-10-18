package com.labourtoday.androidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String message = currentMessage.getDisplayMessageBody();

                    if (phoneNumber.equals(Constants.TWILIO_NUMBER) && message.contains("StripeAppVerification#4444")) {
                        sharedPreferences.edit().putBoolean(Constants.PAYMENT_CONFIRMED, true).apply();
                    }
                    else if (phoneNumber.equals(Constants.TWILIO_NUMBER) && message.contains("StripeAppVerification#1234")) {
                        sharedPreferences.edit().putBoolean(Constants.PAYMENT_CONFIRMED, false).apply();
                    }
                    // Toast.makeText(context, "senderNum: "+ phoneNumber + ", message: " + message, Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }
}
