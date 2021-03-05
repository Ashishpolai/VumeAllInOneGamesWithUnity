package com.vume.allinonegames.Model;

public class JoshLobbyCardSubListData {

    String mTableId;
    String mTableName;
    int mTableLimit;
    int mNumPlayers;
    Float mBetValue;
    Float mMinBuyIn, mMaxBuyIn;


    public JoshLobbyCardSubListData(final String tableId, final String tableName, final int tableLimit, final int numOfPlayers,
                                    final Float betValue, final Float minBuyIn, final Float maxBuyIn) {
        mTableId = tableId;
        mTableName = tableName;
        mTableLimit = tableLimit;
        mNumPlayers = numOfPlayers;
        mBetValue = betValue;
        mMinBuyIn = minBuyIn;
        mMaxBuyIn = maxBuyIn;
    }

    public String getmTableId() {
        return mTableId;
    }

    public String getmTableName() {
        return mTableName;
    }

    public int getmTableLimit() {
        return mTableLimit;
    }

    public int getmNumPlayers() {
        return mNumPlayers;
    }

    public Float getmBetValue() {
        return mBetValue;
    }

    public Float getmMinBuyIn() {
        return mMinBuyIn;
    }

    public Float getmMaxBuyIn() {
        return mMaxBuyIn;
    }
}
