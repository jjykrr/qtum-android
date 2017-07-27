package com.pixelplex.qtum.ui.fragment.AddressesFragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pixelplex.qtum.R;
import com.pixelplex.qtum.datastorage.KeyStorage;
import com.pixelplex.qtum.ui.FragmentFactory.Factory;
import com.pixelplex.qtum.ui.fragment.AddressListFragment.AddressesWithBalanceAdapter;
import com.pixelplex.qtum.ui.fragment.BaseFragment.BaseFragment;
import com.pixelplex.qtum.ui.fragment.ReceiveFragment.ReceiveFragment;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class AddressesFragment extends BaseFragment implements AddressesFragmentView, OnAddressClickListener {

    private AddressesFragmentPresenterImpl mAddressesFragmentPresenter;
    protected AddressesAdapter mAddressAdapter;

    @BindView(R.id.recycler_view)
    protected
    RecyclerView mRecyclerView;


    @OnClick({R.id.ibt_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibt_back:
                getActivity().onBackPressed();
                break;
        }
    }

    public static BaseFragment newInstance(Context context) {
        Bundle args = new Bundle();
        BaseFragment fragment = Factory.instantiateFragment(context, AddressesFragment.class);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void createPresenter() {
        mAddressesFragmentPresenter = new AddressesFragmentPresenterImpl(this);
    }

    @Override
    protected AddressesFragmentPresenterImpl getPresenter() {
        return mAddressesFragmentPresenter;
    }

    @Override
    public void initializeViews() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void setAdapterNull() {
        mAddressAdapter = null;
    }

    @Override
    public void onAddressClick(int adapterPosition) {
        int oldPosition = KeyStorage.getInstance().getCurrentKeyPosition();
        KeyStorage.getInstance().setCurrentKeyPosition(adapterPosition);
        mAddressAdapter.notifyItemChanged(oldPosition);
        mAddressAdapter.notifyItemChanged(adapterPosition);
        ((ReceiveFragment) getTargetFragment()).onChangeAddress();
    }
}