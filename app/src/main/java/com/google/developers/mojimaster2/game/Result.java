package com.google.developers.mojimaster2.game;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

/**
 * Holds information for the UI to display data.
 */
public class Result {

    @ColorRes
    private final int mColor;
    @StringRes
    private final int mResult;
    private final boolean mEnableAnswersView;

    public Result(@ColorRes int color, @StringRes int result) {
        this.mColor = color;
        this.mResult = result;
        mEnableAnswersView = true;
    }

    public Result(@ColorRes int color, @StringRes int result, boolean enable) {
        this.mColor = color;
        this.mResult = result;
        mEnableAnswersView = enable;
    }

    public int getColor() {
        return mColor;
    }

    public int getResult() {
        return mResult;
    }

    public boolean getEnableAnswersView() {
        return  mEnableAnswersView;
    }

}
