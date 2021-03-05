package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshErrorResponse {

    @SerializedName("errorCode")
    private String mErrorCode;
    @SerializedName("errorString")
    private String mErrorString;
    @SerializedName("message")
    private String mMessage;

    public JoshErrorResponse(String errorcode, String errorstring, String message) {
        mErrorCode = errorcode;
        mErrorString = errorstring;
        mMessage = message;
    }

    public String getmErrorCode() {
        return mErrorCode;
    }

    public void setmErrorCode(String mErrorCode) {
        this.mErrorCode = mErrorCode;
    }

    public String getmErrorString() {
        return mErrorString;
    }

    public void setmErrorString(String mErrorString) {
        this.mErrorString = mErrorString;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}
