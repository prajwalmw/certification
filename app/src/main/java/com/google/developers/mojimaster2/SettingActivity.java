package com.google.developers.mojimaster2;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.developers.mojimaster2.data.Smiley;
import com.google.developers.mojimaster2.jobscheduler.NotificationJobService;
import com.google.developers.mojimaster2.paging.SmileyViewModel;
import com.google.developers.mojimaster2.paging.SmileyViewModelFactory;

import java.util.List;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private SmileyViewModel smileyViewModel;
        private List<Smiley> smilies;
        public static final int JOB_ID = 28;

        @Override
        public void onCreatePreferences(Bundle bundle, String string) {
            addPreferencesFromResource(R.xml.preferences);
            SmileyViewModelFactory smileyViewModelFactory = SmileyViewModelFactory.createFactory(this.getActivity());
            smileyViewModel = ViewModelProviders.of(this, smileyViewModelFactory)
                    .get(SmileyViewModel.class);

            smileyViewModel.getRandomSmiley().observe(this, new Observer<List<Smiley>>() {
                @Override
                public void onChanged(List<Smiley> smilies) {
                    setSmilies(smilies);
                }
            });
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            final String notifyKey = getString(R.string.pref_key_notification);
            if (preference.getKey().equals(notifyKey)) {
                // Check if notification setting is no then schedule notification
                boolean on = ((SwitchPreferenceCompat) preference).isChecked();
                PersistableBundle bundle = new PersistableBundle();
                bundle.putString("code", getSmilies().get(0).getCode());
                bundle.putString("emoji", getSmilies().get(0).getEmoji());
                bundle.putString("name", getSmilies().get(0).getName());

                JobScheduler jobScheduler = (JobScheduler)
                        getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                ComponentName componentName =
                        new ComponentName(getContext(), NotificationJobService.class);
                JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
                builder.setPersisted(true); // Remains scheduled through app restarts and device reboots.
                builder.setExtras(bundle); // sends data to the onStartJob() to be passed to Notification.
                builder.setPeriodic(900000, 45000); // 5% of the period also use flex time for > Nougat.

                if(on)
                    jobScheduler.schedule(builder.build());
                else
                    jobScheduler.cancel(JOB_ID);

            }

            return super.onPreferenceTreeClick(preference);
        }

        public List<Smiley> getSmilies() {
            return smilies;
        }

        public void setSmilies(List<Smiley> smilies) {
            this.smilies = smilies;
        }
    }


}
