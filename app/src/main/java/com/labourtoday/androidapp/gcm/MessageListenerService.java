package com.labourtoday.androidapp.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.labourtoday.androidapp.labourer.LabourerGridActivity;

public class MessageListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");

        if (data.getString("notification_type").equals("new_labourer_job")) {
            Intent updateLabourerJobs = new Intent(Constants.UPDATE_LABOURER_JOBS);
            /*
            updateLabourerJobs.putExtra(Constants.JOB_ADDRESS, data.getString(Constants.JOB_ADDRESS));
            updateLabourerJobs.putExtra(Constants.START_TIME, data.getString(Constants.START_TIME));
            updateLabourerJobs.putExtra(Constants.START_DATE, data.getString(Constants.START_DATE));
            updateLabourerJobs.putExtra(Constants.WAGE, data.getString(Constants.WAGE));
            updateLabourerJobs.putExtra(Constants.JOB_CODE, data.getString(Constants.JOB_CODE));
            LocalBroadcastManager.getInstance(this).sendBroadcast(updateLabourerJobs);
            */
            sendLabourerNotification(message);
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendLabourerNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, LabourerGridActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentTitle("New Notification")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        notificationManager.notify(0, notificationBuilder.build());
    }
}

