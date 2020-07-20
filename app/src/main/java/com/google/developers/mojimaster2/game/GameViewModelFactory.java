package com.google.developers.mojimaster2.game;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.developers.mojimaster2.R;
import com.google.developers.mojimaster2.data.DataRepository;

import java.lang.reflect.InvocationTargetException;

/**
 * Factory for GameViewModel.
 */
public class GameViewModelFactory implements ViewModelProvider.Factory {

    private final DataRepository mRepository;
    private final int mAnswerLimit;
    private static final String DEFAULT_ANSWER_LIMIT = "4";

    public static GameViewModelFactory createFactory(Activity activity) {
        Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }

        SharedPreferences defaultPreferences = PreferenceManager
                .getDefaultSharedPreferences(application.getApplicationContext());

        String key = application.getString(R.string.pref_key_answers);
        int limit = Integer.parseInt(defaultPreferences.getString(key, DEFAULT_ANSWER_LIMIT));

        return new GameViewModelFactory(DataRepository.getInstance(application), limit);
    }

    public GameViewModelFactory(DataRepository repository, int limit) {
        mRepository = repository;
        mAnswerLimit = limit;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(DataRepository.class, int.class)
                    .newInstance(mRepository, mAnswerLimit);
        } catch (NoSuchMethodException | IllegalAccessException |
                InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}
