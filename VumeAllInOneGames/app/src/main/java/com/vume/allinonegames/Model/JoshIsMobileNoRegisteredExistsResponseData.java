package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshIsMobileNoRegisteredExistsResponseData {

    @SerializedName("username")
    private String mUsername;
    @SerializedName("oauthuserId")
    private String mOAuthUserId;
    @SerializedName("userId")
    private int mUserId;

    public JoshIsMobileNoRegisteredExistsResponseData(String username, String oauthuserid, int userid) {
        mUsername = username;
        mOAuthUserId = oauthuserid;
        mUserId = userid;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmOAuthUserId() {
        return mOAuthUserId;
    }

    public void setmOAuthUserId(String mOAuthUserId) {
        this.mOAuthUserId = mOAuthUserId;
    }

    public int getmUserId() {
        return mUserId;
    }

    public void setmUserId(int mUserId) {
        this.mUserId = mUserId;
    }
}
