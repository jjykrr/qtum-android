package com.pixelplex.qtum.ui.fragment.ReceiveFragment;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import com.pixelplex.qtum.ui.fragment.BaseFragment.BaseFragmentView;


interface ReceiveFragmentView extends BaseFragmentView {
    void setQrCode(Bitmap bitmap);
    void showSpinner();
    void hideSpinner();
    void setUpAddress(String s);
    void updateBalance(String balance, String unconfirmedBalance);
    void showToast();
    void openFragmentForResult(Fragment fragment);
    Bitmap getQrCode();
}
