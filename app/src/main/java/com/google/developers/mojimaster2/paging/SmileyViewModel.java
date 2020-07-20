package com.google.developers.mojimaster2.paging;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

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
    public LiveData<List<Smiley>> listLiveData;

    public SmileyViewModel(DataRepository repository)
    {
        mRepository = repository;
        listLiveData = mRepository.getALL_smilies();
    }

    public void save(Smiley smiley) {

    }

    public void delete(Smiley smiley) {
        mRepository.delete(smiley);
    }

    public LiveData<List<Smiley>> getListLiveData() {
        return listLiveData;
    }

}
