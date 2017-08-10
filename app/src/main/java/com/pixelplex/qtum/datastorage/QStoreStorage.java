package com.pixelplex.qtum.datastorage;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pixelplex.qtum.model.gson.store.QstoreBuyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirillvolkov on 10.08.17.
 */

public class QStoreStorage {

    private static QStoreStorage instance;

    private final static String PURCHASE_LIST = "PURCHASE_LIST";

    TinyDB tDb;

    private List<PurchaseItem> purchaseItems;

    public static QStoreStorage getInstance(Context context){
        if(instance == null){
            instance = new QStoreStorage(context);
        }
        return instance;
    }

    private QStoreStorage(Context context){
        tDb = new TinyDB(context);
        purchaseItems = getPurchaseItems();
    }

    private List<PurchaseItem> getPurchaseItems(){
        Gson gson = new Gson();
        ArrayList<String> listString = tDb.getListString(PURCHASE_LIST);
        List<PurchaseItem> objects = new ArrayList<>();

        for(String jObjString : listString){
            PurchaseItem value  = gson.fromJson(jObjString,  PurchaseItem.class);
            if(value != null) {
                objects.add(value);
            }
        }
        return objects;
    }

    public boolean isContractPurchased(String contractId){

        if(TextUtils.isEmpty(contractId)){
            throw new NullPointerException("Contract id is NULL");
        }

        for (PurchaseItem item : purchaseItems){
            if(contractId.equals(item.contractId)){
                return true;
            }
        }

        return false;
    }

    public PurchaseItem getPurchaseByContractId(String contractId){

        if(TextUtils.isEmpty(contractId)){
            return null;
        }

        for (PurchaseItem item : purchaseItems){
            if(contractId.equals(item.contractId)){
                return item;
            }
        }

        return null;
    }

    public void addPurchasedItem(String contractId, QstoreBuyResponse buyResponse){
        purchaseItems.add(new PurchaseItem(contractId, buyResponse));
        Gson gson = new Gson();
        ArrayList<String> objStrings = new ArrayList<>();
        for(PurchaseItem obj : purchaseItems){
            objStrings.add(gson.toJson(obj));
        }
        tDb.putListString(PURCHASE_LIST, objStrings);
    }

    public class PurchaseItem{

        public PurchaseItem(String contractId, QstoreBuyResponse buyResponse){
            this.contractId = contractId;
            this.accessToken = buyResponse.accessToken;
            this.address = buyResponse.address;
            this.amount = buyResponse.amount;
            this.requestId = buyResponse.requestId;
        }

        @SerializedName("contract_id")
        public String contractId;

        @SerializedName("address")
        public String address;

        @SerializedName("amount")
        public Float amount;

        @SerializedName("access_token")
        public String accessToken;

        @SerializedName("request_id")
        public String requestId;
    }

}
