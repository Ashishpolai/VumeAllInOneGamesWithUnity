package com.vume.allinonegames.Model;

import com.vume.allinonegames.Util.JoshApplication;

public class JoshBuyinRequestData {

    public final String tableId;
    public final float buyIn;
    private final String gameType;

    public JoshBuyinRequestData(String oTableId, float oBuyIn) {
        tableId = oTableId;
        buyIn = oBuyIn;
        gameType = JoshApplication.getCurrentGameType();
    }

    public String getTableId() {
        return tableId;
    }

    public float getBuyIn() {
        return buyIn;
    }

    public String getGameType() {
        return gameType;
    }
}
