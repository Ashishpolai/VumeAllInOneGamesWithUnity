package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class TransactionHistoryResponseData {

    @SerializedName("date")
    private String mTransactionDate;
    @SerializedName("transaction_id")
    private String mTransactionID;
    @SerializedName("transaction_accno")
    private String mTransactionAccNo;
    @SerializedName("transaction_type")
    private String mTransactionType;
    @SerializedName("transaction_amount")
    private String mTransactionAmount;
    @SerializedName("is_money_deducted")
    private boolean mIsMoneyDeducted;

    public TransactionHistoryResponseData(String date, String transaction_id, String transaction_accno,
                                          String transaction_type, String transaction_amount, boolean is_money_deducted) {
        mTransactionDate = date;
        mTransactionID = transaction_id;
        mTransactionAccNo = transaction_accno;
        mTransactionType = transaction_type;
        mTransactionAmount = transaction_amount;
        mIsMoneyDeducted = is_money_deducted;
    }


    public String getmTransactionDate() {
        return mTransactionDate;
    }

    public void setmTransactionDate(String mTransactionDate) {
        this.mTransactionDate = mTransactionDate;
    }

    public String getmTransactionID() {
        return mTransactionID;
    }

    public void setmTransactionID(String mTransactionID) {
        this.mTransactionID = mTransactionID;
    }

    public String getmTransactionAccNo() {
        return mTransactionAccNo;
    }

    public void setmTransactionAccNo(String mTransactionAccNo) {
        this.mTransactionAccNo = mTransactionAccNo;
    }

    public String getmTransactionType() {
        return mTransactionType;
    }

    public void setmTransactionType(String mTransactionType) {
        this.mTransactionType = mTransactionType;
    }

    public String getmTransactionAmount() {
        return mTransactionAmount;
    }

    public void setmTransactionAmount(String mTransactionAmount) {
        this.mTransactionAmount = mTransactionAmount;
    }

    public boolean ismIsMoneyDeducted() {
        return mIsMoneyDeducted;
    }

    public void setmIsMoneyDeducted(boolean mIsMoneyDeducted) {
        this.mIsMoneyDeducted = mIsMoneyDeducted;
    }
}
