package com.google.developers.mojimaster2.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.google.developers.mojimaster2.MainActivity;
import com.google.developers.mojimaster2.R;

/**
 * Created by Prajwal Maruti Waingankar on 12-03-2022, 21:12
 * Copyright (c) 2021 . All rights reserved.
 * Email: prajwalwaingankar@gmail.com
 * Github: prajwalmw
 */

public class NotificationHelper {

    private Context mContext;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    NotificationHelper(Context context) {
        mContext = context;
    }

    void createNotification(Intent intentPassed) {

        String smiley_face = intentPassed.getStringExtra("smiley_face");
        String smiley_unicode = intentPassed.getStringExtra("smiley_unicode");
        String smiley_name = intentPassed.getStringExtra("smiley_name");

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_mood);
        mBuilder.setContentTitle(mContext.getResources().getString(R.string.notification_title,
                smiley_face, smiley_unicode))
                .setContentText(smiley_name)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0, mBuilder.build());
    }
}
