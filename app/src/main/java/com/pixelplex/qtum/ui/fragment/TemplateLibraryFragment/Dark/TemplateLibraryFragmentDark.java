package com.pixelplex.qtum.ui.fragment.TemplateLibraryFragment.Dark;


import com.pixelplex.qtum.R;
import com.pixelplex.qtum.model.ContractTemplate;
import com.pixelplex.qtum.ui.fragment.TemplateLibraryFragment.TemplateLibraryFragment;

import java.util.List;

public class TemplateLibraryFragmentDark extends TemplateLibraryFragment{

    @Override
    protected int getLayout() {
        return R.layout.fragment_template_library;
    }

    @Override
    public void setUpTemplateList(List<ContractTemplate> contractTemplateList) {
        initializeRecyclerView(contractTemplateList, R.layout.lyt_contract_list_item);
    }
}
