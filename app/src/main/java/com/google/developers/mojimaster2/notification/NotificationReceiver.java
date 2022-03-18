package com.google.developers.mojimaster2.notification;

import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * Created by Prajwal Maruti Waingankar on 12-03-2022, 21:10
 * Copyright (c) 2021 . All rights reserved.
 * Email: prajwalwaingankar@gmail.com
 * Github: prajwalmw
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createNotification(intent);
    }
}
