package com.pixelplex.qtum.ui.fragment.LanguageFragment.Dark;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pixelplex.qtum.R;
import com.pixelplex.qtum.ui.fragment.LanguageFragment.LanguageAdapter;
import com.pixelplex.qtum.ui.fragment.LanguageFragment.OnLanguageIntemClickListener;
import java.util.List;

/**
 * Created by kirillvolkov on 07.07.17.
 */

public class LanguageAdapterDark extends LanguageAdapter {

    LanguageAdapterDark(List<Pair<String, String>> languagesList, OnLanguageIntemClickListener listener) {
        super(languagesList, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_single_checkable, parent, false);
        return new LanguageHolderDark(view, listener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mLanguage = mLanguagesList.get(position);
        ((LanguageHolderDark)holder).bindLanguage(mLanguage);
    }
}
