package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class FantasyCreateTeamResponseData {

    @SerializedName("status")
    private String mStatus;
    @SerializedName("team_id")
    private int mTeamId;

    public FantasyCreateTeamResponseData(String mStatus, int mTeamId) {
        this.mStatus = mStatus;
        this.mTeamId = mTeamId;
    }

    public String getmStatus() {
        return mStatus;
    }

    public int getmTeamId() {
        return mTeamId;
    }
}
