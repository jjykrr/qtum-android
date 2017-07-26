package com.pixelplex.qtum.ui.fragment.TokenFragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pixelplex.qtum.R;
import com.pixelplex.qtum.model.contract.Contract;
import com.pixelplex.qtum.model.contract.Token;
import com.pixelplex.qtum.ui.FragmentFactory.Factory;
import com.pixelplex.qtum.ui.fragment.BaseFragment.BaseFragment;
import com.pixelplex.qtum.ui.fragment.BaseFragment.BaseFragmentPresenterImpl;
import com.pixelplex.qtum.ui.fragment.QStore.StoreContract.Dialogs.ViewSourceCodeDialogFragment;
import com.pixelplex.qtum.ui.fragment.TokenFragment.Dialogs.ShareDialogFragment;
import com.pixelplex.qtum.utils.ContractManagementHelper;
import com.pixelplex.qtum.utils.FontTextView;
import com.pixelplex.qtum.utils.ResizeWidthAnimation;
import com.pixelplex.qtum.utils.StackCollapseLinearLayout;

import butterknife.BindView;
import butterknife.OnClick;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


public abstract class TokenFragment extends BaseFragment implements TokenFragmentView {

    private static final String tokenKey = "tokenInfo";

    public static final String totalSupply = "totalSupply";
    public static final String decimals = "decimals";
    public static final String symbol = "symbol";

    public static BaseFragment newInstance(Context context, Contract token) {
        Bundle args = new Bundle();
        BaseFragment fragment = Factory.instantiateFragment(context, TokenFragment.class);
        args.putSerializable(tokenKey,token);
        fragment.setArguments(args);
        return fragment;
    }

    private TokenFragmentPresenter presenter;

    @OnClick(R.id.bt_back)
    public void onBackClick(){
        getActivity().onBackPressed();
    }

    //HEADER
    @BindView(R.id.ll_balance)
    protected
    LinearLayout mLinearLayoutBalance;
    @BindView(R.id.tv_balance)
    protected
    FontTextView mTextViewBalance;
    @BindView(R.id.tv_currency)
    protected
    FontTextView mTextViewCurrency;
    @BindView(R.id.available_balance_title)
    protected
    FontTextView balanceTitle;

    @BindView(R.id.tv_unconfirmed_balance)
    protected
    FontTextView uncomfirmedBalanceValue;
    @BindView(R.id.unconfirmed_balance_title)
    protected
    FontTextView uncomfirmedBalanceTitle;
    //HEADER

    @BindView(R.id.balance_view)
    protected
    FrameLayout balanceView;

    @BindView(R.id.fade_divider_root)
    RelativeLayout fadeDividerRoot;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)
    protected
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.app_bar)
    protected
    AppBarLayout mAppBarLayout;

    @BindView(R.id.collapse_layout)
    protected
    StackCollapseLinearLayout collapseLinearLayout;

    @BindView(R.id.tv_token_address)
    FontTextView tokenAddress;

    @BindView(R.id.tv_token_name)
    FontTextView mTextViewTokenName;

    @BindView(R.id.contract_address_value)
    FontTextView contractAddress;

    @BindView(R.id.initial_supply_value)
    protected
    FontTextView totalSupplyValue;

    @BindView(R.id.decimal_units_value)
    protected
    FontTextView decimalsValue;
//
//    @BindView(R.id.sender_address_value)
//    FontTextView senderAddrValue;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    ShareDialogFragment shareDialog;

    @OnClick(R.id.bt_share)
    public void onShareClick(){
        shareDialog = ShareDialogFragment.newInstance(presenter.getToken().getContractAddress(),presenter.getAbi());
        shareDialog.show(getFragmentManager(), shareDialog.getClass().getCanonicalName());
    }

    @OnClick(R.id.token_addr_btn)
    public void onTokenAddrClick(){
        presenter.onReceiveClick();
    }

    @Override
    protected void createPresenter() {
        presenter = new TokenFragmentPresenter(this);
    }

    @Override
    protected BaseFragmentPresenterImpl getPresenter() {
        return presenter;
    }

    protected float headerPAdding = 0;
    protected float percents = 1;
    protected float prevPercents = 1;

    @Override
    public void initializeViews() {
        super.initializeViews();

        uncomfirmedBalanceValue.setVisibility(View.GONE);
        uncomfirmedBalanceTitle.setVisibility(View.GONE);

        Token token = (Token) getArguments().getSerializable(tokenKey);
        presenter.setToken(token);

        if (token != null) {
            mTextViewTokenName.setText(token.getContractName());
        }

        collapseLinearLayout.requestLayout();
        headerPAdding = convertDpToPixel(16,getContext());

//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == SCROLL_STATE_IDLE){
//                    autodetectAppbar();
//                }
//            }
//        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    protected boolean expanded = false;

    private void autodetectAppbar(){
        if(percents >=.5f){
            mAppBarLayout.setExpanded(true, true);
        } else {
            mAppBarLayout.setExpanded(false, true);
        }
    }

    private static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    protected int getTotalRange() {
        return mAppBarLayout.getTotalScrollRange();
    }

    protected void animateText(float percents, View view, float fringe) {
        if(percents > fringe) {
            view.setScaleX(percents);
            view.setScaleY(percents);
        } else {
            view.setScaleX(fringe);
            view.setScaleY(fringe);
        }
    }

    @Override
    public void setTokenAddress(String address) {
        if(!TextUtils.isEmpty(address)) {
            tokenAddress.setText(address);
            contractAddress.setText(address);
        }
    }

    @Override
    public void setSenderAddress(String address) {
        //if(!TextUtils.isEmpty(address)) {
            //senderAddrValue.setText(address);
        //}
    }
}