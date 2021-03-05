package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshErrorResponseData {

    @SerializedName("error")
    private String mError;
    @SerializedName("errorCode")
    private String mErrorCode;
    @SerializedName("errorString")
    private String mErrorString;

    public JoshErrorResponseData(String error, String error_code, String error_string) {
        mError = error;
        mErrorCode = error_code;
        mErrorString = error_string;
    }

    public String getmError() {
        return mError;
    }

    public void setmError(String mError) {
        this.mError = mError;
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
}
