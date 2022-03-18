package com.google.developers.mojimaster2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.developers.mojimaster2.data.Smiley;
import com.google.developers.mojimaster2.game.AnswersView;
import com.google.developers.mojimaster2.game.GameViewModel;
import com.google.developers.mojimaster2.game.GameViewModelFactory;
import com.google.developers.mojimaster2.game.Result;
import com.google.developers.mojimaster2.notification.NotificationReceiver;
import com.google.developers.mojimaster2.paging.SmileyViewModel;
import com.google.developers.mojimaster2.paging.SmileyViewModelFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AnswersView.OnAnswerListener{

    private EmojiAppCompatTextView mQuestionView;
    private AnswersView mAnswersView;
    private TextView mResult;
    private GameViewModel mGameViewModel;
    private SmileyViewModel smileyViewModel;
    private LiveData<List<Smiley>> smileylist;
    LinearLayout linearLayout;
    Calendar calendar;
    JobScheduler jobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameViewModelFactory viewModelFactory = GameViewModelFactory.createFactory(this);
        mGameViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(GameViewModel.class);

        SmileyViewModelFactory smileyViewModelFactory = SmileyViewModelFactory.createFactory(this);
        smileyViewModel = ViewModelProviders.of(this, smileyViewModelFactory)
                .get(SmileyViewModel.class);


        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
        setContentView(R.layout.activity_main);
//        mAnswersView = findViewById(R.id.ans_view);
        mAnswersView = new AnswersView(this);
        mAnswersView.setOnAnswerListener(this::onAnswersChange);
        linearLayout = findViewById(R.id.linear_radio);
        linearLayout.addView(mAnswersView, 1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mQuestionView = findViewById(R.id.question);
        mResult = findViewById(R.id.result);

        mGameViewModel.getCurrentAnswer().observe(this, this::updateContent);
        mGameViewModel.getResults().observe(this, this::showResults);

        mGameViewModel.setUpGame().observe(this, this::loadRound);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::loadNewGame);

        smileyViewModel.getRandomSmiley().observe(this, new Observer<List<Smiley>>() {
            @Override
            public void onChanged(List<Smiley> smilies) {
                myAlarm(smilies); // Calling the alarm function...
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
}
