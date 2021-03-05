package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshInstallResponseData {

    @SerializedName("id")
    private int mInstallId;
    @SerializedName("installKey")
    private String mInstallKey;

    public JoshInstallResponseData(int install_id, String install_key) {
        mInstallId = install_id;
        mInstallKey = install_key;
    }


    public int getmInstallId() {
        return mInstallId;
    }

    public void setmInstallId(int mInstallId) {
        this.mInstallId = mInstallId;
    }

    public String getmInstallKey() {
        return mInstallKey;
    }

    public void setmInstallKey(String mInstallKey) {
        this.mInstallKey = mInstallKey;
    }
}
