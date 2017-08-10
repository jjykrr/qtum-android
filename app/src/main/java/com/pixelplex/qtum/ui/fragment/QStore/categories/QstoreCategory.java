package com.pixelplex.qtum.ui.fragment.QStore.categories;


import com.pixelplex.qtum.model.gson.store.QstoreItem;

import java.util.List;

public class QstoreCategory {

    public String title;
    public List<QstoreItem> items;

    public QstoreCategory(String title, List<QstoreItem> items) {
        this.title = title;
        this.items = items;
    }

}
