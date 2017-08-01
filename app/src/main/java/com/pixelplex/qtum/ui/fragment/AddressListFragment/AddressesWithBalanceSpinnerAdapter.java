package com.pixelplex.qtum.ui.fragment.AddressListFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;

import com.pixelplex.qtum.R;
import com.pixelplex.qtum.model.DeterministicKeyWithBalance;
import com.pixelplex.qtum.model.gson.UnspentOutput;
import com.pixelplex.qtum.utils.FontTextView;

import java.math.BigDecimal;
import java.util.List;


public class AddressesWithBalanceSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context mContext;
    private List<DeterministicKeyWithBalance> mKeyWithBalanceList;

    public AddressesWithBalanceSpinnerAdapter(@NonNull Context context, List<DeterministicKeyWithBalance> keyWithBalanceList) {
        mContext = context;
        mKeyWithBalanceList = keyWithBalanceList;
    }

    @Override
    public int getCount() {
        return mKeyWithBalanceList.size();
    }

    @Override
    public Object getItem(int i) {
        return mKeyWithBalanceList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, R.layout.item_address_spinner, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, R.layout.item_address_spinner_dropdown, parent);
    }

    public View getCustomView(int position, @Nullable int resId, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(resId,parent,false);
        FontTextView textViewAddress = (FontTextView) view.findViewById(R.id.address_name);

        final FontTextView textViewBalance = (FontTextView) view.findViewById(R.id.address_balance);
        final FontTextView textViewSymbol = (FontTextView) view.findViewById(R.id.address_symbol);

        BigDecimal balance = new BigDecimal("0");
        BigDecimal amount;
        for(UnspentOutput unspentOutput : mKeyWithBalanceList.get(position).getUnspentOutputList()){
            amount = new BigDecimal(String.valueOf(unspentOutput.getAmount()));
            balance = balance.add(amount);
        }

        textViewSymbol.setText(" QTUM");
        textViewBalance.setText(balance.toString());

        textViewAddress.setText(mKeyWithBalanceList.get(position).getAddress());

        return view;
    }
}
