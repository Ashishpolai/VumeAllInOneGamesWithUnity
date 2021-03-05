package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshAllTransactionsResponseData {

    @SerializedName("txnType")
    private int mTxnType;
    @SerializedName("txnId")
    private int mTxnid;
    @SerializedName("when")
    private long mTxnTimestamp;
    @SerializedName("desc")
    private String mTxnDesc;
    @SerializedName("title")
    private String mTxnTitle;
    @SerializedName("amount")
    private float mTxnAmount;

    public JoshAllTransactionsResponseData(int mTxnType, int mTxnid, long mTxnTimestamp, String mTxnDesc, String mTxnTitle, float mTxnAmount) {
        this.mTxnType = mTxnType;
        this.mTxnid = mTxnid;
        this.mTxnTimestamp = mTxnTimestamp;
        this.mTxnDesc = mTxnDesc;
        this.mTxnTitle = mTxnTitle;
        this.mTxnAmount = mTxnAmount;
    }

    public int getmTxnType() {
        return mTxnType;
    }

    public int getmTxnid() {
        return mTxnid;
    }

    public long getmTxnTimestamp() {
        return mTxnTimestamp;
    }

    public String getmTxnDesc() {
        return mTxnDesc;
    }

    public float getmTxnAmount() {
        return mTxnAmount;
    }

    public String getmTxnTitle() {
        return mTxnTitle;
    }
}
