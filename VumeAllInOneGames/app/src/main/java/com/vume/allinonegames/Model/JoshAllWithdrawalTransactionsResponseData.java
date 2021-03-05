package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshAllWithdrawalTransactionsResponseData {

    @SerializedName("id")
    private int mTxnid;
    @SerializedName("timestamp")
    private long mTxnTimestamp;
    @SerializedName("status")
    private int mTxnStatus;
    @SerializedName("amount")
    private float mTxnAmount;

    public JoshAllWithdrawalTransactionsResponseData(int mTxnid, long mTxnTimestamp, int mTxnStatus, float mTxnAmount) {
        this.mTxnid = mTxnid;
        this.mTxnTimestamp = mTxnTimestamp;
        this.mTxnStatus = mTxnStatus;
        this.mTxnAmount = mTxnAmount;
    }

    public int getmTxnid() {
        return mTxnid;
    }

    public long getmTxnTimestamp() {
        return mTxnTimestamp;
    }

    public int getmTxnStatus() {
        return mTxnStatus;
    }

    public float getmTxnAmount() {
        return mTxnAmount;
    }
}
