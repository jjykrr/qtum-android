package com.pixelplex.qtum.ui.fragment.AddressListFragment.Dark;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.pixelplex.qtum.R;
import com.pixelplex.qtum.model.DeterministicKeyWithBalance;
import com.pixelplex.qtum.ui.fragment.AddressListFragment.AddressListFragment;
import com.pixelplex.qtum.ui.fragment.AddressListFragment.AddressListFragmentPresenter;
import com.pixelplex.qtum.ui.fragment.AddressListFragment.AddressesWithBalanceAdapter;
import com.pixelplex.qtum.ui.fragment.AddressListFragment.AddressesWithBalanceSpinnerAdapter;
import com.pixelplex.qtum.ui.fragment.AddressListFragment.OnAddressClickListener;
import com.pixelplex.qtum.ui.fragment.BaseFragment.BaseFragment;
import com.pixelplex.qtum.utils.CurrentNetParams;
import com.pixelplex.qtum.utils.FontTextView;

import java.util.ArrayList;
import java.util.List;

public class AddressListFragmentDark extends AddressListFragment{

    @Override
    protected int getLayout() {
        return R.layout.fragment_address_list;
    }

    @Override
    public void updateAddressList(List<DeterministicKeyWithBalance> deterministicKeyWithBalance) {
        mAddressesWithBalanceAdapter = new AddressesWithBalanceAdapter(deterministicKeyWithBalance, this,R.layout.item_address);
        mRecyclerView.setAdapter(mAddressesWithBalanceAdapter);
    }

    @Override
    public void onItemClick(DeterministicKeyWithBalance deterministicKeyWithBalance) {
        List<DeterministicKeyWithBalance> deterministicKeyWithBalances = new ArrayList<>(getPresenter().mKeyWithBalanceList);
                                        deterministicKeyWithBalances.remove(deterministicKeyWithBalance);
        showTransferDialogFragment(deterministicKeyWithBalance, deterministicKeyWithBalances);
    }

    protected void showTransferDialogFragment(final DeterministicKeyWithBalance keyWithBalanceTo, List<DeterministicKeyWithBalance> keyWithBalanceList){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_transfer_balance_fragment, null);

        final TextInputEditText mEditTextAmount = (TextInputEditText)view.findViewById(R.id.et_amount);
        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner_transfer);
        FontTextView mEditTextAddressTo = (FontTextView)view.findViewById(R.id.tv_address_to);

        mEditTextAddressTo.setText(keyWithBalanceTo.getKey().toAddress(CurrentNetParams.getNetParams()).toString());

        AddressesWithBalanceSpinnerAdapter spinnerAdapter = new AddressesWithBalanceSpinnerAdapterDark(getContext(),keyWithBalanceList);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getPresenter().keyWithBalanceFrom = (DeterministicKeyWithBalance) spinner.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        view.findViewById(R.id.bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTransferDialog.dismiss();
            }
        });

        view.findViewById(R.id.bt_transfer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressDialog();
                getPresenter().transfer(keyWithBalanceTo, getPresenter().keyWithBalanceFrom, mEditTextAmount.getText().toString(), new AddressListFragmentPresenter.TransferListener(){
                    @Override
                    public void onError(String errorText) {
                        setAlertDialog(getContext().getString(R.string.error),errorText,getContext().getString(R.string.ok), BaseFragment.PopUpType.error);
                    }

                    @Override
                    public void onSuccess() {
                        dismissProgressDialog();
                        mTransferDialog.dismiss();
                        setAlertDialog(getContext().getString(R.string.complete),
                                getContext().getString(R.string.payment_completed_successfully),
                                getContext().getString(R.string.ok), BaseFragment.PopUpType.confirm,new BaseFragment.AlertDialogCallBack(){
                            @Override
                            public void onOkClick() {
                                getMainActivity().onBackPressed();
                            }
                        });
                    }
                });
            }
        });

        mTransferDialog = new AlertDialog
                .Builder(getContext())
                .setView(view)
                .create();

        if(mTransferDialog.getWindow() != null) {
            mTransferDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        mTransferDialog.setCanceledOnTouchOutside(false);
        mTransferDialog.show();
    }
}
