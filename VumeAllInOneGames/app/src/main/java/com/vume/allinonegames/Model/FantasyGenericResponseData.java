package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class FantasyGenericResponseData {

    @SerializedName("status")
    private String mStatus;
    @SerializedName("contest_id")
    private int mContestId;

    public FantasyGenericResponseData(String mStatus, int mContestId) {
        this.mStatus = mStatus;
        this.mContestId = mContestId;
    }

    public String getmStatus() {
        return mStatus;
    }

    public int getmContestId() {
        return mContestId;
    }
}
