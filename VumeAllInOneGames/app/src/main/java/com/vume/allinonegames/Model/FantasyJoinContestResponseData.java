package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class FantasyJoinContestResponseData {

    @SerializedName("status")
    private String mStatus;
    @SerializedName("team_id")
    private int mTeamId;
    @SerializedName("contest_id")
    private int mContestId;

    public FantasyJoinContestResponseData(String mStatus, int mTeamId, int mContestId) {
        this.mStatus = mStatus;
        this.mTeamId = mTeamId;
        this.mContestId = mContestId;
    }

    public String getmStatus() {
        return mStatus;
    }

    public int getmTeamId() {
        return mTeamId;
    }

    public int getmContestId() {
        return mContestId;
    }
}
