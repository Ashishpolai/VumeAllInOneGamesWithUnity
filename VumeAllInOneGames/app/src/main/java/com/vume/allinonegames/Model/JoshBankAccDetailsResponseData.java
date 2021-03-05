package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshBankAccDetailsResponseData {

    @SerializedName("accountHolderName")
    private String mAcccountHolderName;
    @SerializedName("bankCode")
    private String mBankCode;
    @SerializedName("accountNumber")
    private long mAccountNumber;
    @SerializedName("branch")
    private String mBranch;
    @SerializedName("ifsc")
    private String mIfsc;

    public JoshBankAccDetailsResponseData(String accHolderName,String bankCode,long accNumber, String branch, String ifsc){
        mAcccountHolderName = accHolderName;
        mBankCode = bankCode;
        mAccountNumber = accNumber;
        mBranch = branch;
        mIfsc = ifsc;
    }

    public String getmAcccountHolderName() {
        return mAcccountHolderName;
    }

    public void setmAcccountHolderName(String mAcccountHolderName) {
        this.mAcccountHolderName = mAcccountHolderName;
    }

    public String getmBankCode() {
        return mBankCode;
    }

    public void setmBankCode(String mBankCode) {
        this.mBankCode = mBankCode;
    }

    public long getmAccountNumber() {
        return mAccountNumber;
    }

    public void setmAccountNumber(long mAccountNumber) {
        this.mAccountNumber = mAccountNumber;
    }

    public String getmBranch() {
        return mBranch;
    }

    public void setmBranch(String mBranch) {
        this.mBranch = mBranch;
    }

    public String getmIfsc() {
        return mIfsc;
    }

    public void setmIfsc(String mIfsc) {
        this.mIfsc = mIfsc;
    }
}
