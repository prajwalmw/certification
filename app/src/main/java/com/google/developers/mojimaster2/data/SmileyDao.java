package com.google.developers.mojimaster2.data;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Room data access object.
 */
@Dao
public interface SmileyDao {

    /**
     * Returns all data in table for Paging.
     */
    @Query("SELECT * FROM smiley ORDER BY name ASC")
    DataSource.Factory<Integer, Smiley> getAllSmileys();

    /**
     * Returns LiveData of random Smileys.
     *
     * @param limit number of return
     */
    @Query("SELECT * FROM smiley ORDER BY RANDOM() LIMIT :limit")
    LiveData<List<Smiley>> getRandom(int limit);

    /**
     * Returns a random Smiley.
     */
    @Query("SELECT * FROM smiley ORDER BY RANDOM()")
    Smiley getSmiley();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Smiley... smiley);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Smiley smiley);

    @Delete
    void delete(Smiley smiley);

}
