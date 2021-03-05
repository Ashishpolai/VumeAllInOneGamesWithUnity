package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshOpenGamesResponseData {

    @SerializedName("ipPort")
    private String mIpPort;
    @SerializedName("tableId")
    private String mTableId;
    @SerializedName("siteId")
    private String mSiteId;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("playerId")
    private String mPlayeId;
    @SerializedName("wagerId")
    private String mWagerId;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("playerIp")
    private String mPlayeIp;
    @SerializedName("ticketId")
    private String mTicketId;
    @SerializedName("gameId")
    private String mGameId;
    @SerializedName("tableType")
    private String mtableType;

    public JoshOpenGamesResponseData(String mIpPort, String mTableId, String mSiteId, String mSessionId, String mPlayeId, String mWagerId, String mStatus, String mPlayeIp, String mTicketId, String mGameId, String mtableType) {
        this.mIpPort = mIpPort;
        this.mTableId = mTableId;
        this.mSiteId = mSiteId;
        this.mSessionId = mSessionId;
        this.mPlayeId = mPlayeId;
        this.mWagerId = mWagerId;
        this.mStatus = mStatus;
        this.mPlayeIp = mPlayeIp;
        this.mTicketId = mTicketId;
        this.mGameId = mGameId;
        this.mtableType = mtableType;
    }

    public String getmIpPort() {
        return mIpPort;
    }

    public String getmTableId() {
        return mTableId;
    }

    public String getmSiteId() {
        return mSiteId;
    }

    public String getmSessionId() {
        return mSessionId;
    }

    public String getmPlayeId() {
        return mPlayeId;
    }

    public String getmWagerId() {
        return mWagerId;
    }

    public String getmStatus() {
        return mStatus;
    }

    public String getmPlayeIp() {
        return mPlayeIp;
    }

    public String getmTicketId() {
        return mTicketId;
    }

    public String getmGameId() {
        return mGameId;
    }

    public String getMtableType() {
        return mtableType;
    }
}
