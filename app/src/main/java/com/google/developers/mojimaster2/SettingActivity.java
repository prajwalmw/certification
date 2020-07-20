package com.google.developers.mojimaster2;

import android.app.job.JobScheduler;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

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

        public static final int JOB_ID = 28;

        @Override
        public void onCreatePreferences(Bundle bundle, String string) {
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            final String notifyKey = getString(R.string.pref_key_notification);
            if (preference.getKey().equals(notifyKey)) {
                // Check if notification setting is no then schedule notification
                boolean on = ((SwitchPreferenceCompat) preference).isChecked();
                JobScheduler jobScheduler = getContext().getSystemService(JobScheduler.class);

            }

            return super.onPreferenceTreeClick(preference);
        }
    }

}
