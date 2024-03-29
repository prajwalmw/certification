package com.google.developers.mojimaster2.paging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.developers.mojimaster2.R;
import com.google.developers.mojimaster2.data.Smiley;

import java.util.ArrayList;
import java.util.List;

public class SmileyAdapter extends PagedListAdapter<Smiley, SmileyAdapter.SmileyViewHolder> {
    // List<Smiley> smileyList = new ArrayList<>();
    Context mContext;

    public SmileyAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.mContext = context;
    }

    @NonNull
    @Override
    public SmileyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.smiley_list_item, parent, false);
        return new SmileyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SmileyViewHolder holder, int position) {
        Smiley item = getItem(position);
        if (item != null) {
            holder.bindTo(item);
        } else {
            Toast.makeText(mContext, "Item is null", Toast.LENGTH_LONG).show();
        }
    }

   /* public void setData(List<Smiley> smilies) {
        smileyList.clear();
        smileyList.addAll(smilies);
    }*/

    public class SmileyViewHolder extends RecyclerView.ViewHolder {

        private EmojiAppCompatTextView mEmoji;
        private TextView mName;
        private TextView mUnicode;
        private Smiley mSmiley;

        SmileyViewHolder(View itemView) {
            super(itemView);
            mEmoji = itemView.findViewById(R.id.emoji_id);
            mName = itemView.findViewById(R.id.id_text);
            mUnicode = itemView.findViewById(R.id.id_unicode);
        }

        public Smiley getSmiley() {
            return mSmiley;
        }

        void bindTo(Smiley smiley) {
            mSmiley = smiley;
            mEmoji.setText(smiley.getEmoji());
            mName.setText(smiley.getName());
            mUnicode.setText(smiley.getCode());
        }
    }

    private static final DiffUtil.ItemCallback<Smiley> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Smiley>() {
                @Override
                public boolean areItemsTheSame(@NonNull Smiley oldItem, @NonNull Smiley newItem) {
                    return oldItem.getName() == newItem.getName();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Smiley oldItem,
                                                  @NonNull Smiley newItem) {
                    return newItem.equals(oldItem);
                }
            };

//    @Override
//    public int getItemCount() {
//        return smileyList.size();
//    }
}
