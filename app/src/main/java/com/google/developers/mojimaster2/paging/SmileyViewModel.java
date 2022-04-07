package com.google.developers.mojimaster2.paging;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.google.developers.mojimaster2.data.Smiley;
import com.google.developers.mojimaster2.data.DataRepository;

import java.util.List;

/**
 * Store and manage data for SmileyListActivity.
 */
public class SmileyViewModel extends ViewModel {

    private final DataRepository mRepository;
    public static int PAGE_SIZE = 30;
    public static boolean PLACEHOLDERS = true;
    public final LiveData<PagedList<Smiley>> smileyPagedList;
    private PagedList.Config config;

    public SmileyViewModel(DataRepository repository)
    {
        mRepository = repository;

        config = new PagedList.Config.Builder()
                .setEnablePlaceholders(PLACEHOLDERS) //Nothing happens
                .setPageSize(PAGE_SIZE)
                .build();

        smileyPagedList = new LivePagedListBuilder<>(
                repository.getSmileys(), config)
                .build();
    }

    public void save(Smiley smiley) {
        mRepository.insert(smiley);
    }

    public void delete(Smiley smiley) {
        mRepository.delete(smiley);
    }

    public LiveData<List<Smiley>> getRandomSmiley() {
        return mRepository.getRandomSmileys(1);
    }

    public LiveData<PagedList<Smiley>> getSmileyPagedList() {
        return smileyPagedList;
    }
}
