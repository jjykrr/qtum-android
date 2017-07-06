package com.pixelplex.qtum.ui.fragment.ContractConfirmFragment;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.pixelplex.qtum.R;
import com.pixelplex.qtum.dataprovider.restAPI.QtumService;
import com.pixelplex.qtum.model.contract.ContractMethodParameter;
import com.pixelplex.qtum.model.contract.Contract;
import com.pixelplex.qtum.model.contract.Token;
import com.pixelplex.qtum.model.gson.SendRawTransactionRequest;
import com.pixelplex.qtum.model.gson.SendRawTransactionResponse;
import com.pixelplex.qtum.model.gson.UnspentOutput;
import com.pixelplex.qtum.datastorage.KeyStorage;
import com.pixelplex.qtum.model.ContractTemplate;
import com.pixelplex.qtum.ui.fragment.BaseFragment.BaseFragment;
import com.pixelplex.qtum.ui.fragment.BaseFragment.BaseFragmentPresenterImpl;
import com.pixelplex.qtum.utils.ContractBuilder;
import com.pixelplex.qtum.datastorage.TinyDB;

import org.bitcoinj.script.Script;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ContractConfirmPresenterImpl extends BaseFragmentPresenterImpl implements ContractConfirmPresenter {

    ContractConfirmView view;
    ContractConfirmInteractorImpl interactor;
    Context mContext;


    private long mContractTemplateUiid;


    private List<ContractMethodParameter> mContractMethodParameterList;

    public void setContractMethodParameterList(List<ContractMethodParameter> contractMethodParameterList) {
        this.mContractMethodParameterList = contractMethodParameterList;
    }

    public List<ContractMethodParameter> getContractMethodParameterList() {
        return mContractMethodParameterList;
    }

    public ContractConfirmPresenterImpl(ContractConfirmView view) {
        this.view = view;
        mContext = getView().getContext();
        interactor = new ContractConfirmInteractorImpl();
    }


    public void confirmContract(final long uiid) {
        getView().setProgressDialog();
        mContractTemplateUiid = uiid;
        ContractBuilder contractBuilder = new ContractBuilder();
        contractBuilder.createAbiConstructParams(mContractMethodParameterList, uiid ,mContext)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().dismissProgressDialog();
                        getView().setAlertDialog(mContext.getString(R.string.error), e.getMessage(),"Ok", BaseFragment.PopUpType.error);
                    }

                    @Override
                    public void onNext(String s) {
                        createTx(s);
                    }
                });
    }


    public void createTx(final String abiParams) {
        QtumService.newInstance().getUnspentOutputsForSeveralAddresses(KeyStorage.getInstance().getAddresses())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<UnspentOutput>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().setAlertDialog(mContext.getString(R.string.error),e.getMessage(),"Ok", BaseFragment.PopUpType.error);
                    }
                    @Override
                    public void onNext(List<UnspentOutput> unspentOutputs) {

                        for(Iterator<UnspentOutput> iterator = unspentOutputs.iterator(); iterator.hasNext();){
                            UnspentOutput unspentOutput = iterator.next();
                            if(unspentOutput.getConfirmations()==0){
                                iterator.remove();
                            }
                        }
                        Collections.sort(unspentOutputs, new Comparator<UnspentOutput>() {
                            @Override
                            public int compare(UnspentOutput unspentOutput, UnspentOutput t1) {
                                return unspentOutput.getAmount().doubleValue() > t1.getAmount().doubleValue() ? 1 : unspentOutput.getAmount().doubleValue() < t1.getAmount().doubleValue() ? -1 : 0;
                            }
                        });
                        ContractBuilder contractBuilder = new ContractBuilder();
                        Script script = contractBuilder.createConstructScript(abiParams);
                        sendTx(contractBuilder.createTransactionHash(script,unspentOutputs),"asdasd");
                    }
                });
    }

    public void sendTx(final String code, final String senderAddress) {
        QtumService.newInstance().sendRawTransaction(new SendRawTransactionRequest(code, 1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SendRawTransactionResponse>() {
                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().setAlertDialog(mContext.getString(R.string.contract_created_successfully), "", "OK", BaseFragment.PopUpType.confirm, new BaseFragment.AlertDialogCallBack() {
                            @Override
                            public void onOkClick() {
                                FragmentManager fm = getView().getFragment().getFragmentManager();
                                int count = fm.getBackStackEntryCount()-2;
                                for(int i = 0; i < count; ++i) {
                                    fm.popBackStack();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().dismissProgressDialog();
                        getView().setAlertDialog(mContext.getString(R.string.error),e.getMessage(),"OK", BaseFragment.PopUpType.error);
                    }

                    @Override
                    public void onNext(SendRawTransactionResponse sendRawTransactionResponse) {
                        String s = sendRawTransactionResponse.getResult();
                        getView().getApplication().setContractAwait(true);
                        String name = "";
                        for(ContractMethodParameter contractMethodParameter : mContractMethodParameterList){
                            if(contractMethodParameter.getName().equals("_name")){
                                name = contractMethodParameter.getValue();
                            }
                        }
                        TinyDB tinyDB = new TinyDB(mContext);
                        for(ContractTemplate contractTemplate : tinyDB.getContractTemplateList()){
                            if(contractTemplate.getUiid() == mContractTemplateUiid){
                                if(contractTemplate.getContractType().equals("token")){
                                    Token token = new Token(null, mContractTemplateUiid, false, null, senderAddress, name);
                                    List<Token> tokenList = tinyDB.getTokenList();
                                    tokenList.add(token);
                                    tinyDB.putTokenList(tokenList);
                                }else{
                                    Contract contract = new Contract(null, mContractTemplateUiid, false, null, senderAddress, "test_name");
                                    List<Contract> contractList = tinyDB.getContractListWithoutToken();
                                    contractList.add(contract);
                                    tinyDB.putContractListWithoutToken(contractList);
                                }
                            }
                        }

                    }
                });
    }

    @Override
    public ContractConfirmView getView() {
        return view;
    }
}
