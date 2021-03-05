package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshWithdrawRaiseOrcancelResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("balance")
    private JoshWithdrawRaiseOrCancelResponseBalanceData mCurrentBalanceData;

    public JoshWithdrawRaiseOrcancelResponse(String status, JoshWithdrawRaiseOrCancelResponseBalanceData mCurrentBalance, int mCash, int mBonus) {
        this.status = status;
        this.mCurrentBalanceData = mCurrentBalance;
    }

    public String getStatus() {
        return status;
    }

    public JoshWithdrawRaiseOrCancelResponseBalanceData getmCurrentBalanceData() {
        return mCurrentBalanceData;
    }

    public class JoshWithdrawRaiseOrCancelResponseBalanceData{
        @SerializedName("cash")
        private int mCash;
        @SerializedName("bonus")
        private int mBonus;
        @SerializedName("balance")
        private int mBalance;

        public JoshWithdrawRaiseOrCancelResponseBalanceData(int mCash, int mBonus, int mBalance) {
            this.mCash = mCash;
            this.mBonus = mBonus;
            this.mBalance = mBalance;
        }

        public int getmCash() {
            return mCash;
        }

        public int getmBonus() {
            return mBonus;
        }

        public int getmBalance() {
            return mBalance;
        }

        public int getmTotalBalance() {
            return getmBonus()+getmCash();
        }
    }
}
