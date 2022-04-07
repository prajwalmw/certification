package com.google.developers.mojimaster2.data;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles data sources and makes sure to execute on the correct thread.
 */
public class DataRepository {

    private final SmileyDao mDao;
    private final ExecutorService mIoExecutor;
    private static volatile DataRepository sInstance = null;
    public static LiveData<List<Smiley>> listLiveData;

    public static DataRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    SmileyDatabase database = SmileyDatabase.getInstance(application);
                    sInstance = new DataRepository(database.smileyDao(),
                            Executors.newSingleThreadExecutor());


                }
            }
        }
        return sInstance;
    }

    public DataRepository(SmileyDao dao, ExecutorService executor) {
        mIoExecutor = executor;
        mDao = dao;
    }

    public DataSource.Factory<Integer, Smiley> getSmileys() {
        return mDao.getAllSmileys();
    }  // Paging Library Data source

    public LiveData<List<Smiley>> getRandomSmileys(int limit) {
        return mDao.getRandom(limit);
    }

    public void delete(Smiley smiley) {
        mIoExecutor.execute(() -> mDao.delete(smiley));
    }

    public void insert(Smiley smiley) {
        mIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDao.insert(smiley);
            }
        });
    }

    public Smiley getSmiley() {
        try {
            return mIoExecutor.submit(mDao::getSmiley).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

}
