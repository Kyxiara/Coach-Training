package com.example.kellynarboux.coach_training.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.kellynarboux.coach_training.activities.MainActivity;
import com.example.kellynarboux.coach_training.R;

public class TrainingTimeReceiver extends BroadcastReceiver{
    private static final String CHANNEL_ID = "coach_training";

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    public void showNotification(Context context) {
        int reqCode = 0;
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, reqCode, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Training time :) !")
                .setContentText("C'est l'heure de votre séance");
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(mNotificationManager != null)
            mNotificationManager.notify(reqCode, mBuilder.build());

    }
}
