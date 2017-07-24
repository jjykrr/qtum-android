package com.pixelplex.qtum.ui.fragment.LanguageFragment.Dark;

import android.util.Pair;

import com.pixelplex.qtum.R;
import com.pixelplex.qtum.ui.fragment.LanguageFragment.LanguageAdapter;
import com.pixelplex.qtum.ui.fragment.LanguageFragment.LanguageFragment;

import java.util.List;

/**
 * Created by kirillvolkov on 07.07.17.
 */

public class LanguageFragmentDark extends LanguageFragment {
    @Override
    protected int getLayout() {
        return R.layout.fragment_language;
    }

    @Override
    public void setUpLanguagesList(List<Pair<String,String>> languagesList) {
        mLanguagesList = languagesList;
        mLanguageAdapter = new LanguageAdapterDark(languagesList, this);
        mRecyclerView.setAdapter(mLanguageAdapter);
    }
}
