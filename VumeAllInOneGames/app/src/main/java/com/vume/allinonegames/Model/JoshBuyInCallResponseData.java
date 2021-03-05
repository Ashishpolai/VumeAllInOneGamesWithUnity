package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshBuyInCallResponseData {

    @SerializedName("wagerId")
    private String mWagerId;
    @SerializedName("tableId")
    private String mTableId;
    @SerializedName("buyIn")
    private float mBuyIn;

    public JoshBuyInCallResponseData(String wagerId, String tableId, float buyIn) {
        mWagerId = wagerId;
        mTableId = tableId;
        mBuyIn = buyIn;
    }

    public String getmWagerId() {
        return mWagerId;
    }

    public String getmTableId() {
        return mTableId;
    }

    public float getmBuyIn() {
        return mBuyIn;
    }
}
