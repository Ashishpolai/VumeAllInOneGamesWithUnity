package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshFetchBalanceResponseData {

    @SerializedName("cash")
    private float mCurrrentBalance;
    @SerializedName("balance")
    private float mCurrentBalanceBonusSum;
    @SerializedName("bonus")
    private float mCurrentBonus;
    @SerializedName("status")
    private String mStatus;

    public JoshFetchBalanceResponseData(final float currentbalance, final float currentbalancebonussum,
                                        final float currentbonus, final String status) {
        mCurrrentBalance = currentbalance;
        mCurrentBalanceBonusSum = currentbalancebonussum;
        mCurrentBonus = currentbonus;
        mStatus = status;
    }

    public float getmCurrrentBalance() {
        return mCurrrentBalance;
    }

    public float getmCurrentBalanceBonusSum() {
        return mCurrentBalanceBonusSum;
    }

    public float getmCurrentBonus() {
        return mCurrentBonus;
    }

    public String getmStatus() {
        return mStatus;
    }
}
