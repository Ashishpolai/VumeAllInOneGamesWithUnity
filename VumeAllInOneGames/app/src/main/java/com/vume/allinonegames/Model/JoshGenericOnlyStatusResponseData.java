package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshGenericOnlyStatusResponseData {

    public static final String OK_STATUS = "ok";

    @SerializedName("status")
    private String mStatus;

    public JoshGenericOnlyStatusResponseData(String status) {
        mStatus = status;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }
}
