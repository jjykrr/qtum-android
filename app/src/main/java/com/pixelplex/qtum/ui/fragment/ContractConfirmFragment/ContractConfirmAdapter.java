package com.pixelplex.qtum.ui.fragment.ContractConfirmFragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mikhaellopez.hfrecyclerview.HFRecyclerView;
import com.pixelplex.qtum.R;
import com.pixelplex.qtum.model.contract.ContractMethodParameter;

import java.util.List;


public abstract class ContractConfirmAdapter extends HFRecyclerView<ContractMethodParameter> {

    protected String mineAddress;
    protected String fee;

    protected OnValueClick clickListener;

    public ContractConfirmAdapter(List<ContractMethodParameter> params, String mineAddress, String fee, OnValueClick clickListener){
        super(params,false, true);
        this.mineAddress = mineAddress;
        this.fee = fee;
        this.clickListener = clickListener;
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return null; //not use
    }
}
