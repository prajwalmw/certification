package com.google.developers.mojimaster2.game;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.developers.mojimaster2.R;
import com.google.developers.mojimaster2.data.Smiley;

import java.util.List;

public class AnswersView extends RadioGroup implements RadioGroup.OnCheckedChangeListener {

    private OnAnswerListener mAnswerListener;

    public AnswersView(Context context) {
        super(context);
        setOnCheckedChangeListener(this);
    }

    public AnswersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnCheckedChangeListener(this);
    }

    /**
     * Loads RadioButtons from the list of Smileys
     *
     * @param smileys list of {@see Smiley}
     */
    public void loadAnswers(List<Smiley> smileys) {
        if (smileys == null) {
            return;
        }
        removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (Smiley smiley : smileys) {
            RadioButton button = (RadioButton) inflater.inflate(R.layout.answer_item, this, false);
            button.setText(smiley.getName());
            button.setTag(R.string.answer_tag, smiley.getCode());
            button.setChecked(false);
            addView(button);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton button = group.findViewById(checkedId);
        mAnswerListener.onAnswerSelected((String) button.getTag(R.string.answer_tag));
    }

    /**
     * Set enabled state of child views and parent view
     *
     * @param enabled True if child views are enabled?
     */
    @Override
    public void setEnabled(boolean enabled) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }

    /**
     * <p>Clears the selection. When the selection is cleared, no radio button
     * in this group is selected and {@link #getCheckedRadioButtonId()} returns
     * null.</p>
     *
     * @see #check(int)
     * @see #getCheckedRadioButtonId()
     */
    @Override
    public void clearCheck() {
        super.clearCheck();
    }

    public interface OnAnswerListener {
        void onAnswerSelected(String answer);
    }

    public void setOnAnswerListener(OnAnswerListener listener) {
        mAnswerListener = listener;
    }

}
