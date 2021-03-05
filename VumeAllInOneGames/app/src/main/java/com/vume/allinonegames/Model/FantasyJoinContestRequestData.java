package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class FantasyJoinContestRequestData {

    @SerializedName("team_id")
    private int mTeamId;

    public FantasyJoinContestRequestData(int mTeamId) {
        this.mTeamId = mTeamId;
    }

    public int getmTeamId() {
        return mTeamId;
    }
}
