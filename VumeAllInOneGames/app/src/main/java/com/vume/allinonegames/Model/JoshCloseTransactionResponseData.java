package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshCloseTransactionResponseData {

    @SerializedName("orderId")
    private String mOrderId;
    @SerializedName("amount")
    private float mTransactionAmount;
    @SerializedName("balance")
    private float mBalance;

    public JoshCloseTransactionResponseData(String mOrderId, float mTransactionAmount, float mBalance) {
        this.mOrderId = mOrderId;
        this.mTransactionAmount = mTransactionAmount;
        this.mBalance = mBalance;
    }

    public String getmOrderId() {
        return mOrderId;
    }

    public float getmTransactionAmount() {
        return mTransactionAmount;
    }

    public float getmBalance() {
        return mBalance;
    }
}
