package com.pixelplex.qtum.ui.fragment.RestoreContractsFragment.Dark;

import android.support.v4.content.ContextCompat;
import android.view.View;

import com.pixelplex.qtum.R;
import com.pixelplex.qtum.ui.fragment.RestoreContractsFragment.RestoreContractsFragment;

/**
 * Created by kirillvolkov on 25.07.17.
 */

public class RestoreContractsFragmentDark extends RestoreContractsFragment {

    @Override
    protected int getLayout() {
        return R.layout.fragment_restore_contracts;
    }

    @Override
    public void setFile(String name, String size) {
        mTextViewFileName.setText(name);
        mTextViewFileSize.setVisibility(View.VISIBLE);
        mTextViewFileSize.setText(size);
        mImageViewRestoreIcon.setClickable(true);
        mFrameLayoutBackUpFile.setClickable(false);
        mImageViewRestoreIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_delete));
    }

    @Override
    public void deleteFile() {
        mTextViewFileName.setText(R.string.select_back_up_file);
        mTextViewFileSize.setVisibility(View.GONE);
        mImageViewRestoreIcon.setClickable(false);
        mFrameLayoutBackUpFile.setClickable(true);
        mImageViewRestoreIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_attach));
    }
}
