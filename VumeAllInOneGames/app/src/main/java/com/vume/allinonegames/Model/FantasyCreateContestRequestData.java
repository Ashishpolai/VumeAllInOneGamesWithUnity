package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FantasyCreateContestRequestData {

    @SerializedName("match_id")
    private int mMatchId;
    @SerializedName("max_teams")
    private String mMaxTeams;
    @SerializedName("entry_fee")
    private int mEntryFees;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("note")
    private String mNote;
    @SerializedName("prizes")
    private List<FantasyContestPrizes> mFantasyPrizesList;

    public FantasyCreateContestRequestData(int mMatchId, String mMaxTeams, int mEntryFees, String mTitle, String mNote, List<FantasyContestPrizes> mFantasyPrizesList) {
        this.mMatchId = mMatchId;
        this.mMaxTeams = mMaxTeams;
        this.mEntryFees = mEntryFees;
        this.mTitle = mTitle;
        this.mNote = mNote;
        this.mFantasyPrizesList = mFantasyPrizesList;
    }

    public int getmMatchId() {
        return mMatchId;
    }

    public String getmMaxTeams() {
        return mMaxTeams;
    }

    public int getmEntryFees() {
        return mEntryFees;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmNote() {
        return mNote;
    }

    public List<FantasyContestPrizes> getmFantasyPrizesList() {
        return mFantasyPrizesList;
    }

    public class FantasyContestPrizes {

        @SerializedName("min_rank")
        private int mMinRank;
        @SerializedName("max_rank")
        private int mMaxRank;
        @SerializedName("prize_amt_pct")
        private int mPrizeAmountPerContest;

        public FantasyContestPrizes(int mMinRank, int mMaxRank, int mPrizeAmountPerContest) {
            this.mMinRank = mMinRank;
            this.mMaxRank = mMaxRank;
            this.mPrizeAmountPerContest = mPrizeAmountPerContest;
        }

        public int getmMinRank() {
            return mMinRank;
        }

        public int getmMaxRank() {
            return mMaxRank;
        }

        public int getmPrizeAmountPerContest() {
            return mPrizeAmountPerContest;
        }
    }
}
