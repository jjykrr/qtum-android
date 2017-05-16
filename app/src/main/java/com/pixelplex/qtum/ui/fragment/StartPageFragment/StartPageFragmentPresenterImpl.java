package com.pixelplex.qtum.ui.fragment.StartPageFragment;

import android.content.Context;

import com.pixelplex.qtum.ui.fragment.BaseFragment.BaseFragmentPresenterImpl;
import com.pixelplex.qtum.ui.fragment.CreateWalletNameFragment.CreateWalletNameFragment;
import com.pixelplex.qtum.ui.fragment.ImportWalletFragment.ImportWalletFragment;


class StartPageFragmentPresenterImpl extends BaseFragmentPresenterImpl implements StartPageFragmentPresenter {

    private StartPageFragmentView mStartPageFragmentView;


    StartPageFragmentPresenterImpl(StartPageFragmentView startPageFragmentView) {
        mStartPageFragmentView = startPageFragmentView;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
    }

    @Override
    public StartPageFragmentView getView() {
        return mStartPageFragmentView;
    }

    @Override
    public void createNewWallet() {
        CreateWalletNameFragment createWalletNameFragment = CreateWalletNameFragment.newInstance(true);
        getView().openFragment(createWalletNameFragment);
    }

    @Override
    public void importWallet() {
        ImportWalletFragment importWalletFragment = ImportWalletFragment.newInstance();
        getView().openFragment(importWalletFragment);
    }
}
