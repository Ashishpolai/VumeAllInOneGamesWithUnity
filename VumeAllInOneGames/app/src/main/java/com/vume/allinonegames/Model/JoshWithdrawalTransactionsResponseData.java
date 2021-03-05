package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class JoshWithdrawalTransactionsResponseData {

    @SerializedName("withdrawals")
    private List<JoshAllWithdrawalTransactionsResponseData> mWithdrawalTxns = new ArrayList<>();
    @SerializedName("withdrawable")
    private int mCurrentWithdrawBalance;
    @SerializedName("minWithdrawable")
    private int mMinimumWithdrwableBalance;
    @SerializedName("isKycVerified")
    private boolean mIsKycVerified;
    @SerializedName("isBankAcctVerified")
    private boolean mIsBankAcctVerified;

    public JoshWithdrawalTransactionsResponseData(List<JoshAllWithdrawalTransactionsResponseData> mWithdrawalTxns, int mCurrentWithdrawBalance, int mMinimumWithdrwableBalance, boolean mIsKycVerified, boolean mIsBankAcctVerified) {
        this.mWithdrawalTxns = mWithdrawalTxns;
        this.mCurrentWithdrawBalance = mCurrentWithdrawBalance;
        this.mMinimumWithdrwableBalance = mMinimumWithdrwableBalance;
        this.mIsKycVerified = mIsKycVerified;
        this.mIsBankAcctVerified = mIsBankAcctVerified;
    }

    public List<JoshAllWithdrawalTransactionsResponseData> getmWithdrawalTxns() {
        return mWithdrawalTxns;
    }

    public int getmCurrentWithdrawBalance() {
        return mCurrentWithdrawBalance;
    }

    public int getmMinimumWithdrwableBalance() {
        return mMinimumWithdrwableBalance;
    }

    public boolean ismIsKycVerified() {
        return mIsKycVerified;
    }

    public boolean ismIsBankAcctVerified() {
        return mIsBankAcctVerified;
    }
}
