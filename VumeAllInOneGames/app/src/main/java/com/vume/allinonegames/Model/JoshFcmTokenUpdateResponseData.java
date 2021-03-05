package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshFcmTokenUpdateResponseData {

    @SerializedName("id")
    private String mId;
    @SerializedName("install-key")
    private String mInstallKey;

    public JoshFcmTokenUpdateResponseData(final String id, final String installKey) {
        mId = id;
        mInstallKey = installKey;
    }

    public String getmId() {
        return mId;
    }

    public String getmInstallKey() {
        return mInstallKey;
    }
}
