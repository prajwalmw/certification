package com.google.developers.mojimaster2.service;

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
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.developers.mojimaster2.MainActivity;
import com.google.developers.mojimaster2.R;
import com.google.developers.mojimaster2.data.DataRepository;
import com.google.developers.mojimaster2.data.Smiley;


public class NotificationJobService extends JobService {
    private static final int JOB_ID = 1;
    public static final long ONE_DAY_INTERVAL = 24 * 60 * 60 * 1000L; // 1 Day
    private static final long ONE_WEEK_INTERVAL = 7 * 24 * 60 * 60 * 1000L; // 1 Week
    public static final int NOTIFICATION_ID = 18;
    public static final String CHANNEL_ID = "notify-smiley";

    @Override
    public boolean onStartJob(JobParameters params) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        DataRepository repository = DataRepository.getInstance(getApplication());
        Smiley smiley = repository.getSmiley();

        if (notificationManager == null | smiley == null) {
            Log.i(NotificationJobService.class.getName(), "Failed to create notification");
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = getString(R.string.notification_channel_name);
            String channelDescription = getString(R.string.notification_channel_description);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(this.getApplicationContext(), "default").setContentTitle("Smiley Of The Day")
                .setSmallIcon(R.drawable.ic_mood)
                .setContentText("Smiling Faces");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        b.setContentIntent(contentIntent);

        notificationManager.notify(0, b.build());

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


    public static void schedule(Context context, long intervalMillis) {

        JobScheduler jobScheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName =
                new ComponentName(context, NotificationJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPeriodic(intervalMillis);
        jobScheduler.schedule(builder.build());
    }


}
