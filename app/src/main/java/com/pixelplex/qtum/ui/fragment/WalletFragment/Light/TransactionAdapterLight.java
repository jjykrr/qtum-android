package com.pixelplex.qtum.ui.fragment.WalletFragment.Light;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixelplex.qtum.R;
import com.pixelplex.qtum.model.gson.history.History;
import com.pixelplex.qtum.ui.fragment.WalletFragment.ProgressBarHolder;
import com.pixelplex.qtum.ui.fragment.WalletFragment.TransactionAdapter;
import com.pixelplex.qtum.ui.fragment.WalletFragment.TransactionClickListener;

import java.util.List;

/**
 * Created by kirillvolkov on 05.07.17.
 */

public class TransactionAdapterLight extends TransactionAdapter {

    public TransactionAdapterLight(List<History> historyList, TransactionClickListener listener) {
        super(historyList, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_TRANSACTION) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.lyt_transaction_light, parent, false);
            return new TransactionHolderLight(view, listener);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_progress_bar, parent, false);
            return new ProgressBarHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ProgressBarHolder){
            ((ProgressBarHolder)holder).bindProgressBar(false);
        } else {
            mHistory = mHistoryList.get(position);
            ((TransactionHolderLight) holder).bindTransactionData(mHistory);
        }
    }

}
