package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FantasyAllMatchesResponseData {

    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("title")
    private String mTitle;
//    @SerializedName("start_date_timestamp")
//    private long mStartDateTimestamp;
    @SerializedName("start_date")
    private long mStartDate;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("winner_team")
    private String mWinnerItem;
    @SerializedName("teams")
    private List<FantasyTeam> mTeams;

    public FantasyAllMatchesResponseData(int mId, String mName, String mTitle, long mStartDate, String mStatus, String mWinnerItem, List<FantasyTeam> mWinnerTeams) {
        this.mId = mId;
        this.mName = mName;
        this.mTitle = mTitle;
        this.mStartDate = mStartDate;
        this.mStatus = mStatus;
        this.mWinnerItem = mWinnerItem;
        this.mTeams = mWinnerTeams;
    }

    public int getmId() {
        return mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmName() {
        return mName;
    }

    public long getmStartDateTimestampInSeconds() {
        return mStartDate;
    }

    public String getmStatus() {
        return mStatus;
    }

    public String getmWinnerItem() {
        return mWinnerItem;
    }

    public List<FantasyTeam> getTeams() {
        return mTeams;
    }

    public class FantasyTeam{
        @SerializedName("name")
        private String mTeamName;
        @SerializedName("short_name")
        private String mTeamShortName;
        @SerializedName("icon_url")
        private String mTeamIconUrl;

        public String getmTeamName() {
            return mTeamName;
        }

        public String getmTeamShortName() {
            return mTeamShortName;
        }

        public String getmTeamIconUrl() {
            return mTeamIconUrl;
        }
    }
}
