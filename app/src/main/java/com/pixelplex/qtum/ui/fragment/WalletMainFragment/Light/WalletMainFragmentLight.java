package com.pixelplex.qtum.ui.fragment.WalletMainFragment.Light;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.pixelplex.qtum.R;
import com.pixelplex.qtum.ui.fragment.WalletMainFragment.WalletMainFragment;

import butterknife.BindView;

/**
 * Created by kirillvolkov on 06.07.17.
 */

public class WalletMainFragmentLight extends WalletMainFragment implements ViewPager.OnPageChangeListener {

    @BindView(R.id.wallet_indicator) ImageView walletIndicator;
    @BindView(R.id.other_tokens_indicator) ImageView otherTokensIndicator;
    @BindView(R.id.page_indicator) View pageIndicator;

    @Override
    protected int getLayout() {
        return R.layout.fragment_wallet_main_light;
    }

    @Override
    public void showOtherTokens(boolean isShow) {
        if(pager.getAdapter() != null) {
            ((FragmentAdapter) pager.getAdapter()).showOtherTokens(isShow);
        }
        pageIndicator.setVisibility((isShow)? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void initializeViews() {
        super.initializeViews();
        pager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageSelected(int position) {
        walletIndicator.setImageResource((position == 0)? R.drawable.tab_indicator_selected_light : R.drawable.tab_indicator_default_light);
        otherTokensIndicator.setImageResource((position == 0)? R.drawable.tab_indicator_default_light : R.drawable.tab_indicator_selected_light);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}
