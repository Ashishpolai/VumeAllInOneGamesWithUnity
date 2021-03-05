package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshInitiateTransactionResponseData {

    @SerializedName("txnToken")
    private String mTxnToken;

    public JoshInitiateTransactionResponseData(String txntoken) {
        mTxnToken = txntoken;
    }

    public String getmTxnToken() {
        return mTxnToken;
    }

    public void setmTxnToken(String mTxnToken) {
        this.mTxnToken = mTxnToken;
    }
}
