package com.pixelplex.qtum.ui.fragment.SetYourTokenFragment;

import com.pixelplex.qtum.model.contract.ContractMethodParameter;
import com.pixelplex.qtum.ui.fragment.BaseFragment.BaseFragmentView;

import java.util.List;


public interface SetYourTokenFragmentView extends BaseFragmentView {

    void onContractConstructorPrepared(List<ContractMethodParameter> params);

}
