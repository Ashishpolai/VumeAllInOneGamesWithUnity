package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshLobbyCardListResponseData {

    @SerializedName("tableId")
    private String mTableId;
    @SerializedName("tableType")
    private String mTableType;
    @SerializedName("tableName")
    private String mTableName;
    @SerializedName("tableLimit")
    private int  mTableLimit;
    @SerializedName("minBuyin")
    private Float mMinBuyIn;
    @SerializedName("maxBuyin")
    private Float mMaxBuyIn;
    @SerializedName("betValue")
    private Float mBetValue;
    @SerializedName("numPlayers")
    private int mNumOfPlayers;
    @SerializedName("category")
    private String mCategory;

    public JoshLobbyCardListResponseData(String tableId, String tableType, String tableName, int tableLimit, Float betValue, int numOfPlayers, Float minBuyIn, Float maxBuyIn, String category) {
        mTableId = tableId;
        mTableType = tableType;
        mTableName= tableName;
        mTableLimit = tableLimit;
        mBetValue = betValue;
        mMinBuyIn = minBuyIn;
        mMaxBuyIn = maxBuyIn;
        mNumOfPlayers = numOfPlayers;
        mCategory = category;
    }


    public String getmTableId() {
        return mTableId;
    }

    public String getmTableType() {
        return mTableType;
    }

    public String getmTableName() {
        return mTableName;
    }

    public int getmTableLimit() {
        return mTableLimit;
    }

    public Float getmMinBuyIn() {
        return mMinBuyIn;
    }

    public Float getmMaxBuyIn() {
        return mMaxBuyIn;
    }

    public Float getmBetValue() {
        return mBetValue;
    }

    public int getmNumOfPlayers() {
        return mNumOfPlayers;
    }

    public String getmCategory() {
        return mCategory;
    }
}
