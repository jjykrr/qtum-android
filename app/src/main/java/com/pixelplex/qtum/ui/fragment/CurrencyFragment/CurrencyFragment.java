package com.pixelplex.qtum.ui.fragment.CurrencyFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pixelplex.qtum.R;
import com.pixelplex.qtum.dataprovider.UpdateService;
import com.pixelplex.qtum.dataprovider.listeners.TokenBalanceChangeListener;
import com.pixelplex.qtum.model.Currency;
import com.pixelplex.qtum.model.contract.Contract;
import com.pixelplex.qtum.model.contract.Token;
import com.pixelplex.qtum.model.gson.tokenBalance.TokenBalance;
import com.pixelplex.qtum.ui.FragmentFactory.Factory;
import com.pixelplex.qtum.ui.fragment.BaseFragment.BaseFragment;
import com.pixelplex.qtum.ui.fragment.SendFragment.SendFragment;
import com.pixelplex.qtum.utils.FontTextView;
import com.pixelplex.qtum.ui.fragment.SendFragment.SendFragment;
import com.pixelplex.qtum.utils.SearchBar;
import com.pixelplex.qtum.utils.SearchBarListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class CurrencyFragment extends BaseFragment implements CurrencyFragmentView, SearchBarListener {

    private CurrencyFragmentPresenterImpl mCurrencyFragmentPresenter;
    protected CurrencyAdapter mCurrencyAdapter;
    private String mSearchString;
    protected List<Currency> mCurrentList;
    private UpdateService mUpdateService;

    @BindView(R.id.recycler_view)
    protected
    RecyclerView mRecyclerView;


    @BindView(R.id.tv_currency_title)
    TextView mTextViewCurrencyTitle;
    @BindView(R.id.ll_currency)
    FrameLayout mFrameLayoutBase;

    @OnClick({R.id.ibt_back})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.ibt_back:
                getActivity().onBackPressed();
                break;
        }
    }

    public static BaseFragment newInstance(Context context) {
        Bundle args = new Bundle();
        BaseFragment fragment = Factory.instantiateFragment(context, CurrencyFragment.class);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void createPresenter() {
        mCurrencyFragmentPresenter = new CurrencyFragmentPresenterImpl(this);
    }

    @Override
    protected CurrencyFragmentPresenterImpl getPresenter() {
        return mCurrencyFragmentPresenter;
    }

    @Override
    public void initializeViews() {
        super.initializeViews();
        mUpdateService = getMainActivity().getUpdateService();
        mTextViewCurrencyTitle.setText(R.string.currency);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFrameLayoutBase.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    hideKeyBoard();
            }
        });
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {
        if(mFrameLayoutBase != null) {
            mFrameLayoutBase.requestFocus();
        }
        hideKeyBoard();
    }

    @Override
    public void onRequestSearch(String filter) {
        if(filter.isEmpty()){
            mCurrencyAdapter.setFilter(mCurrentList);
        } else {
            mSearchString = filter.toLowerCase();
            List<Currency> newList = new ArrayList<>();
            for(Currency currency: mCurrentList){
                if(currency.getName().toLowerCase().contains(mSearchString))
                    newList.add(currency);
            }
            mCurrencyAdapter.setFilter(newList);
        }
    }

    class CurrencyHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.root_layout)
        RelativeLayout rootLayout;

        @BindView(R.id.token_name)
        FontTextView mTextViewCurrencyName;

        @BindView(R.id.token_balance)
        FontTextView mTextViewCurrencyBalance;

        @BindView(R.id.spinner)
        ProgressBar spinner;

        Currency mCurrency;

        CurrencyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SendFragment) getTargetFragment()).onCurrencyChoose(mCurrency.getName());
                    ((SendFragment) getTargetFragment()).onCurrencyChoose(mCurrency.getAddress());
                    getActivity().onBackPressed();
                }
            });

        }

        void bindCurrency(Currency currency){

            if(this.mCurrency != null && mCurrency.isToken()) {
                mUpdateService.removeTokenBalanceChangeListener(mCurrency.getAddress());
            }

            mCurrency = currency;
            mTextViewCurrencyName.setText(currency.getName());
            if(mCurrency.isToken()) {
                mTextViewCurrencyBalance.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                mUpdateService.addTokenBalanceChangeListener(mCurrency.getAddress(), new TokenBalanceChangeListener() {
                    @Override
                    public void onBalanceChange(final TokenBalance tokenBalance) {
                        rootLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                spinner.setVisibility(View.GONE);
                                mTextViewCurrencyBalance.setText(String.format("%f QTUM", tokenBalance.getTotalBalance()));
                                mTextViewCurrencyBalance.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            } else {
                mTextViewCurrencyBalance.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
            }
        }
    }

    protected class CurrencyAdapter extends RecyclerView.Adapter<CurrencyHolder> {

        private int resId;
        private List<Currency> mCurrencyList;

        public CurrencyAdapter(List<Currency> currencyList, int resId) {
            mCurrencyList = currencyList;
            this.resId = resId;
        }

        public Currency get(int adapterPosition) {
            return mCurrencyList.get(adapterPosition);
        }

        @Override
        public CurrencyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CurrencyHolder(LayoutInflater.from(parent.getContext()).inflate(resId, parent, false));
        }

        @Override
        public void onBindViewHolder(CurrencyHolder holder, int position) {
            holder.bindCurrency(mCurrencyList.get(position));
        }

        void setFilter(List<Currency> currencyListNew){
            mCurrencyList = new ArrayList<>();
            mCurrencyList.addAll(currencyListNew);
            notifyDataSetChanged();
        }

        public void setTokenList(List<Currency> currencyList) {
            this.mCurrencyList = currencyList;
        }

        @Override
        public int getItemCount() {
            return mCurrencyList.size();
        }
    }
}