package com.google.developers.mojimaster2.jobscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.google.developers.mojimaster2.MainActivity;
import com.google.developers.mojimaster2.R;
import com.google.developers.mojimaster2.data.Smiley;

import java.util.List;

/**
 * Created by Prajwal Maruti Waingankar on 18-03-2022, 21:11
 * Copyright (c) 2021 . All rights reserved.
 * Email: prajwalwaingankar@gmail.com
 * Github: prajwalmw
 */

// Register this service in Manifest file.
// Jobscheduler: Doesnt work when app is killed... works only in Foreground...
public class NotificationJobService extends JobService {
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final int JOB_ID = 28;
   // private static List<Smiley> smilies;
  //  private static boolean switchStatus = false;

/*
    public static void schedule(Context context, long intervalMillis, List<Smiley> smilies,
                                boolean switchStatus) {
        setSmilies(smilies);
        JobScheduler jobScheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName =
                new ComponentName(context, NotificationJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
        builder.setPersisted(true); // Remains scheduled through app restarts and device reboots.
        builder.setPeriodic(intervalMillis, 45000); // 5% of the period also use flex time for > Nougat.

        if(switchStatus)
            jobScheduler.schedule(builder.build());
        else
            cancel(context);
    }
*/

/*
    public static void cancel(Context context) {
        JobScheduler jobScheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID);
    }
*/

   /* public static List<Smiley> getSmilies() {
        return smilies;
    }

    public static void setSmilies(List<Smiley> smilies) {
        NotificationJobService.smilies = smilies;
    }*/

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
      //  List<Smiley> smilies = getSmilies();
        /* executing a task synchronously */
       // if (/* condition for finishing it */) {
            // To finish a periodic JobService,
            // you must cancel it, so it will not be scheduled more.
     //       YourJobService.cancel(this);
     //   }

        //Set up the notification content intent to launch the app when clicked
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 0, new Intent(this, MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.notification_title,
                        jobParameters.getExtras().getString("emoji"),
                        jobParameters.getExtras().getString("code")))
                .setContentText(jobParameters.getExtras().getString("name")) // extras livedata that was passed.
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_mood)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true; // true: to reschedule the job in case it was cancelled.
    }
}
