package com.pixelplex.qtum.ui.fragment.CreateWalletNameFragment;

import android.content.Context;

import com.pixelplex.qtum.datastorage.QtumSharedPreference;


class CreateWalletNameFragmentInteractorImpl implements CreateWalletNameFragmentInteractor {

    private Context mContext;

    CreateWalletNameFragmentInteractorImpl(Context context){
        mContext = context;
    }

    @Override
    public void saveWalletName(String name) {
        QtumSharedPreference.getInstance().saveWalletName(mContext,name);
    }
}
