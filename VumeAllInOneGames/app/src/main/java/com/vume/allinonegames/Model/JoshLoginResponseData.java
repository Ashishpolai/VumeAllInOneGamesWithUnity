package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshLoginResponseData {

    @SerializedName("token")
    private String mLoginToken;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("userId")
    private int mUserId;

    public JoshLoginResponseData(String logintoken, String username, int userid) {
        mLoginToken = logintoken;
        mUsername = username;
        mUserId = userid;
    }

    public String getmLoginToken() {
        return mLoginToken;
    }

    public void setmLoginToken(String mLoginToken) {
        this.mLoginToken = mLoginToken;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public int getmUserId() {
        return mUserId;
    }

    public void setmUserId(int mUserId) {
        this.mUserId = mUserId;
    }
}
