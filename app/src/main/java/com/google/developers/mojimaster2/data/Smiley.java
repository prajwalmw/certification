package com.google.developers.mojimaster2.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.google.developers.mojimaster2.data.DataSmileyName.*;

import java.util.HashMap;

/**
 * A Model class that holds information about the emoji.
 * Class defines a table for the Room database with primary key the {@see #mCode}.
 */
@Entity(tableName = TABLE_NAME)
public class Smiley {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = COL_UNICODE)
    @NonNull
    private String mCode;

    @ColumnInfo(name = COL_NAME)
    private String mName;

    @ColumnInfo(name = COL_EMOJI)
    private String mEmoji;

    public Smiley(@NonNull String mCode, String mName, String mEmoji) {
        this.mCode = mCode;
        this.mName = mName;
        this.mEmoji = mEmoji;
    }


    @NonNull
    public String getCode() {
        return mCode;
    }

    public String getName() {
        return mName;
    }

    public String getEmoji() {
        return mEmoji;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
