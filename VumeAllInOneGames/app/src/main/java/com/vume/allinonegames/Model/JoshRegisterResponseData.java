package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshRegisterResponseData {

    @SerializedName("username")
    private String mUsername;
    @SerializedName("oauthuserId")
    private int mOAuthUserId;
    @SerializedName("deviceId")
    private int mDeviceId;
    @SerializedName("userId")
    private int mUserId;

    public JoshRegisterResponseData(String username, int oauthuserid, int deviceid, int userid) {
        mUsername = username;
        mOAuthUserId = oauthuserid;
        mDeviceId = deviceid;
        mUserId = userid;
    }


    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public int getmOAuthUserId() {
        return mOAuthUserId;
    }

    public void setmOAuthUserId(int mOAuthUserId) {
        this.mOAuthUserId = mOAuthUserId;
    }

    public int getmDeviceId() {
        return mDeviceId;
    }

    public void setmDeviceId(int mDeviceId) {
        this.mDeviceId = mDeviceId;
    }

    public int getmUserId() {
        return mUserId;
    }

    public void setmUserId(int mUserId) {
        this.mUserId = mUserId;
    }

}
