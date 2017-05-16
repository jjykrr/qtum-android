package com.pixelplex.qtum.datastorage;


public class QtumToken {
    private static QtumToken sQtumToken;
    private String mTokenName;
    private String mTokenSymbol;
    private boolean mIsFreezingOfAssets;
    private boolean mAutomaticSellingAndBuying;
    private boolean mIsAutorefill;
    private boolean mIsProofOfWork;
    private int mInitialSupply;
    private int mDecimalUnits;

    public static QtumToken getInstance() {
        if (sQtumToken == null) {
            sQtumToken = new QtumToken();
        }
        return sQtumToken;
    }

    private QtumToken() {

    }

    public String getTokenName() {
        return mTokenName;
    }

    public void setTokenName(String tokenName) {
        mTokenName = tokenName;
    }

    public String getTokenSymbol() {
        return mTokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        mTokenSymbol = tokenSymbol;
    }

    public boolean isFreezingOfAssets() {
        return mIsFreezingOfAssets;
    }

    public void setFreezingOfAssets(boolean freezingOfAssets) {
        mIsFreezingOfAssets = freezingOfAssets;
    }

    public boolean isAutomaticSellingAndBuying() {
        return mAutomaticSellingAndBuying;
    }

    public void setAutomaticSellingAndBuying(boolean automaticSellingAndBuying) {
        mAutomaticSellingAndBuying = automaticSellingAndBuying;
    }

    public boolean isAutorefill() {
        return mIsAutorefill;
    }

    public void setAutorefill(boolean autorefill) {
        mIsAutorefill = autorefill;
    }

    public boolean isProofOfWork() {
        return mIsProofOfWork;
    }

    public void setProofOfWork(boolean proofOfWork) {
        mIsProofOfWork = proofOfWork;
    }

    public int getInitialSupply() {
        return mInitialSupply;
    }

    public void setInitialSupply(int initialSupply) {
        mInitialSupply = initialSupply;
    }

    public int getDecimalUnits() {
        return mDecimalUnits;
    }

    public void setDecimalUnits(int decimalUnits) {
        mDecimalUnits = decimalUnits;
    }

    public void clearToken(){
        sQtumToken = null;
    }
}
