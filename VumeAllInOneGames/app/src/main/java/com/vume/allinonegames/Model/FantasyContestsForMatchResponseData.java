package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class FantasyContestsForMatchResponseData {

    @SerializedName("max_teams")
    private int mMaxTeams;
    @SerializedName("prizes")
    private List<FantasyprizesData> mPrizesList;
    @SerializedName("is_private")
    private int mIsPrivate;
    @SerializedName("num_teams")
    private int mNumTeams;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("note")
    private String mNote;
    @SerializedName("id")
    private int mId;
    @SerializedName("match_id")
    private String mMatchid;
    @SerializedName("is_mns")
    private int mIsMns;
    @SerializedName("entry_fee")
    private int mEntryFees;

    private double mTotalWinnings = 0.0;

    public FantasyContestsForMatchResponseData(int mMaxTeams, List<FantasyprizesData> mPrizesList, int mIsPrivate,
                                               int mNumTeams, String mTitle, String mNote, int mId, String mMatchid,
                                               int mIsMns, int mEntryFees) {
        this.mMaxTeams = mMaxTeams;
        this.mPrizesList = mPrizesList;
        this.mIsPrivate = mIsPrivate;
        this.mNumTeams = mNumTeams;
        this.mTitle = mTitle;
        this.mNote = mNote;
        this.mId = mId;
        this.mMatchid = mMatchid;
        this.mIsMns = mIsMns;
        this.mEntryFees = mEntryFees;
    }

    public BigDecimal getmTotalWinnings(){return new BigDecimal(mTotalWinnings);}

    public void setmTotalWinnings(double mTotalWinnings){ this.mTotalWinnings = mTotalWinnings;}

    public int getmMaxTeams() {
        return mMaxTeams;
    }

    public List<FantasyprizesData> getmPrizesList() {
        return mPrizesList;
    }

    public int getmIsPrivate() {
        return mIsPrivate;
    }

    public boolean isPrivate(){
        return mIsPrivate == 1? true:false;
    }

    public int getmNumTeams() {
        return mNumTeams;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmNote() {
        return mNote;
    }

    public int getmId() {
        return mId;
    }

    public String getmMatchid() {
        return mMatchid;
    }

    public int getmIsMns() {
        return mIsMns;
    }

    public boolean isMns(){
        return mIsMns == 1? true:false;
    }

    public int getmEntryFees() {
        return mEntryFees;
    }

    public class FantasyprizesData {

        @SerializedName("min_rank")
        private int mMinRank;
        @SerializedName("max_rank")
        private int mMaxrank;
        @SerializedName("extra_prize")
        private String mExtraPrize;
        @SerializedName("prize_amt")
        private double mPrizeAmount;

        public FantasyprizesData(int mMinRank, int mMaxrank, String mExtraPrize, double mPrizeAmount) {
            this.mMinRank = mMinRank;
            this.mMaxrank = mMaxrank;
            this.mExtraPrize = mExtraPrize;
            this.mPrizeAmount = mPrizeAmount;
        }

        public int getmMinRank() {
            return mMinRank;
        }

        public int getmMaxrank() {
            return mMaxrank;
        }

        public String getmExtraPrize() {
            return mExtraPrize;
        }

        public double getmPrizeAmount() {
            return mPrizeAmount;
        }
    }
}
