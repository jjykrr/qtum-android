package com.pixelplex.qtum.ui.fragment.WalletFragment.Light;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pixelplex.qtum.R;
import com.pixelplex.qtum.model.gson.history.History;
import com.pixelplex.qtum.ui.fragment.WalletFragment.TransactionClickListener;
import com.pixelplex.qtum.utils.DateCalculator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kirillvolkov on 05.07.17.
 */

public class TransactionHolderLight extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_value)
    TextView mTextViewValue;
    @BindView(R.id.tv_date)
    TextView mTextViewDate;
    @BindView(R.id.tv_id)
    TextView mTextViewID;
    @BindView(R.id.iv_icon)
    ImageView mImageViewIcon;
    @BindView(R.id.ll_transaction)
    LinearLayout mLinearLayoutTransaction;

    TransactionHolderLight(View itemView, final TransactionClickListener listener) {
        super(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTransactionClick(getAdapterPosition());
            }
        });
        ButterKnife.bind(this, itemView);
    }

    void bindTransactionData(History history) {

        mLinearLayoutTransaction.setBackgroundResource(android.R.color.transparent);

        if (history.getChangeInBalance().doubleValue() > 0) {
            mImageViewIcon.setImageResource(R.drawable.ic_received_light);
        } else {
            mImageViewIcon.setImageResource(R.drawable.ic_sended_light);
        }

        if(history.getBlockTime() != null) {
            mTextViewDate.setText(DateCalculator.getShortDate(history.getBlockTime()*1000L));
        } else {
            mImageViewIcon.setImageResource(R.drawable.ic_confirmation_loader);
            mTextViewDate.setText(R.string.confirmation);
            mLinearLayoutTransaction.setBackgroundResource(R.color.bottom_nav_bar_color_light);
        }

        mTextViewID.setText(history.getTxHash());
        mTextViewValue.setText(history.getChangeInBalance().toString() + " QTUM");
    }
}
