package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FantasyCreateTeamRequestData {

    @SerializedName("match_id")
    private int mMatchId;
    @SerializedName("name")
    private String mName;
    @SerializedName("team")
    private FantasyTeam mFantasyTeam;

    public FantasyCreateTeamRequestData(int mMatchId, String mName, FantasyTeam mFantasyTeam) {
        this.mMatchId = mMatchId;
        this.mName = mName;
        this.mFantasyTeam = mFantasyTeam;
    }

    public int getmMatchId() {
        return mMatchId;
    }

    public String getmName() {
        return mName;
    }

    public FantasyTeam getmFantasyTeams() {
        return mFantasyTeam;
    }

    public static class FantasyTeam {

        @SerializedName("players")
        private List<Integer> mPlayersList;
        @SerializedName("captain")
        private int mCaptain;
        @SerializedName("vice_captain")
        private int mViceCaptain;
        @SerializedName("keeper")
        private int mKeeper;

        public FantasyTeam(List<Integer> mPlayersList, int mCaptain, int mViceCaptain, int mKeeper) {
            this.mPlayersList = mPlayersList;
            this.mCaptain = mCaptain;
            this.mViceCaptain = mViceCaptain;
            this.mKeeper = mKeeper;
        }

        public List<Integer> getmPlayersList() {
            return mPlayersList;
        }

        public int getmCaptain() {
            return mCaptain;
        }

        public int getmViceCaptain() {
            return mViceCaptain;
        }

        public int getmKeeper() {
            return mKeeper;
        }
    }
}
