package com.google.developers.mojimaster2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.developers.mojimaster2.data.Smiley;
import com.google.developers.mojimaster2.game.AnswersView;
import com.google.developers.mojimaster2.game.GameViewModel;
import com.google.developers.mojimaster2.game.GameViewModelFactory;
import com.google.developers.mojimaster2.game.Result;
import com.google.developers.mojimaster2.jobscheduler.NotificationJobService;
import com.google.developers.mojimaster2.alarmanager_broadcastreceiver_notification.NotificationReceiver;
import com.google.developers.mojimaster2.paging.SmileyViewModel;
import com.google.developers.mojimaster2.paging.SmileyViewModelFactory;
import com.google.developers.mojimaster2.workmanager_notification.NotificationWorkManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements AnswersView.OnAnswerListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        NavigationView.OnNavigationItemSelectedListener{

    private EmojiAppCompatTextView mQuestionView;
    private AnswersView mAnswersView;
    private TextView mResult;
    private GameViewModel mGameViewModel;
    private SmileyViewModel smileyViewModel;
    private LiveData<List<Smiley>> smileylist;
    LinearLayout linearLayout;
    Calendar calendar;
    SharedPreferences sp;
    private static final long ONE_DAY_INTERVAL = 24 * 60 * 60 * 1000L; // 1 Day
    private static final long ONE_WEEK_INTERVAL = 7 * 24 * 60 * 60 * 1000L; // 1 Week
    Data.Builder data;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.getString(getString(R.string.pref_key_answers), "4");
        sp.registerOnSharedPreferenceChangeListener(this);


        GameViewModelFactory viewModelFactory = GameViewModelFactory.createFactory(this);
        mGameViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(GameViewModel.class);

        SmileyViewModelFactory smileyViewModelFactory = SmileyViewModelFactory.createFactory(this);
        smileyViewModel = ViewModelProviders.of(this, smileyViewModelFactory)
                .get(SmileyViewModel.class);

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(toggle);
        }
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }


        mAnswersView = findViewById(R.id.ans_view);
        mQuestionView = findViewById(R.id.question);
        mResult = findViewById(R.id.result);

        mAnswersView.setOnAnswerListener(this::onAnswersChange);
        mGameViewModel.getCurrentAnswer().observe(this, this::updateContent);
        mGameViewModel.getResults().observe(this, this::showResults);
        mGameViewModel.setUpGame().observe(this, new Observer<List<Smiley>>() {
            @Override
            public void onChanged(List<Smiley> smilies) {
                loadRound(smilies);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::loadNewGame);

        smileyViewModel.getRandomSmiley().observe(this, new Observer<List<Smiley>>() {
            @Override
            public void onChanged(List<Smiley> smilies) {
              //  myAlarm(smilies); // Calling the alarm function... *** AlarmManager + BroadcastReceiver ***
               // NotificationJobService.schedule(MainActivity.this, 900000, smilies); // *** Jobscheduler ***

             /*   data = new Data.Builder();
                data.putString("title", getResources().getString(R.string.notification_title,
                        smilies.get(0).getEmoji(), smilies.get(0).getCode()));
                data.putString("text", smilies.get(0).getName()); // Step 2 *** Data to be sent to WorkManager.

                PeriodicWorkRequest uploadWorkRequest =
                        new PeriodicWorkRequest.Builder(NotificationWorkManager.class,
                                20,
                                TimeUnit.MINUTES)
                                .setInputData(data.build())
                                .build();  // WorkRequest added. Step 3 ****

                // Mostly, in case of Periodic works duplicate works could happen.
                // So use this so that only one worker is enqueued at a time...
                WorkManager.getInstance(MainActivity.this)
                        .enqueueUniquePeriodicWork("uniqueWorkName",
                                ExistingPeriodicWorkPolicy.KEEP,
                                uploadWorkRequest);  // Step 4 *** WorkManager instance is added.*/
            }
        });

    }

    /**
     * Listener on answer selection.
     */
    private void onAnswersChange(String answer) {
        mGameViewModel.updateResult(answer);
    }

    /**
     * Loads new game and updates observer.
     */
    private void loadNewGame(@Nullable View view) {
        mResult.setText(null);
        mGameViewModel.resetGame();
    }

    /**
     * Load answers for the next round.
     */
    private void loadRound(List<Smiley> smileys) {

        mAnswersView.loadAnswers(smileys);
        mGameViewModel.startNewGameRound();
    }

    /**
     * Show results of each round.
     */
    private void showResults(Result result) {
        if (result == null) {
            return;
        }

        mResult.setTextColor(getColor(result.getColor()));
        mResult.setText(getString(result.getResult()));

        if (!result.getEnableAnswersView()) {
            mAnswersView.setEnabled(false);
        }
    }

    private void updateContent(Smiley smiley) {
        if (smiley == null) {
            return;
        }

        mQuestionView.setText(smiley.getEmoji());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_list:
                Intent listIntent = new Intent(this, SmileyListActivity.class);
                startActivity(listIntent);
                return true;
            case R.id.action_add:
                Intent addIntent = new Intent(this, AddSmileyActivity.class);
                startActivity(addIntent);
                return true;
            case R.id.action_settings:
                Intent settingIntent = new Intent(this, SettingActivity.class);
                startActivity(settingIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onAnswerSelected(String answer) {
        Log.d("PRAJL","PRAJL: "+answer);
    }

    public void myAlarm(List<Smiley> smilies) {
      //  smileylist = smileyViewModel.getRandomSmiley();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23); //11:00 PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("smiley_face", smilies.get(0).getEmoji());
        intent.putExtra("smiley_unicode", smilies.get(0).getCode());
        intent.putExtra("smiley_name", smilies.get(0).getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key1) {
        if(key1.equals(getString(R.string.pref_key_answers))) {
            int limit = Integer.parseInt(sp.getString(key1, "4"));
            mGameViewModel.loadSmileys(limit).observe(this, new Observer<List<Smiley>>() {
                @Override
                public void onChanged(List<Smiley> smilies) {
                    loadRound(smilies);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
      //  DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Handle navigation view item clicks here.
        switch (menuItem.getItemId()) {
            case R.id.nav_add: {
                // Handle the camera import action (for now display a toast).
                drawerLayout.closeDrawer(GravityCompat.START);
                menuItem.setCheckable(false);
                Intent i = new Intent(MainActivity.this, AddSmileyActivity.class);
                startActivity(i);
                return true;
            }
            case R.id.nav_list:
                // Handle the gallery action (for now display a toast).
                drawerLayout.closeDrawer(GravityCompat.START);
                menuItem.setCheckable(false);
                Intent i = new Intent(MainActivity.this, SmileyListActivity.class);
                startActivity(i);
                return true;
            default:
                return false;
        }
    }

    /**
     * Handles the Back button: closes the nav drawer.
     */
    @Override
    public void onBackPressed() {
       // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }
}
