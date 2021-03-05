package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FantasyGetMatchDetailsResponseData {

    @SerializedName("format")
    private String mMatchFormat;
    @SerializedName("key")
    private String mKey;
    @SerializedName("winner_team")
    private String mWinnerTeam;
    @SerializedName("name")
    private String mName;
    @SerializedName("season_id")
    private int mSeasonId;
    @SerializedName("teams")
    private List<FantasyTeam> mFantasyTeamList;
    @SerializedName("short_name")
    private String mShortName;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("start_date")
    private long mStartDateTimestamp;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("id")
    private int mId;
    @SerializedName("venue")
    private String mVenue;
    @SerializedName("players")
    private List<FantasyPlayer> mFantasyPlayersList;
    @SerializedName("stadium_id")
    private int mStadiumId;

    public FantasyGetMatchDetailsResponseData(String mMatchFormat, String mKey, String mWinnerTeam, String mName, int mSeasonId,
                                              List<FantasyTeam> mFantasyTeamList, String mShortName, String mTitle, long mStartDateTimestamp,
                                              String mStatus, int mId, String mVenue, List<FantasyPlayer> mFantasyPlayersList, int mStadiumId) {
        this.mMatchFormat = mMatchFormat;
        this.mKey = mKey;
        this.mWinnerTeam = mWinnerTeam;
        this.mName = mName;
        this.mSeasonId = mSeasonId;
        this.mFantasyTeamList = mFantasyTeamList;
        this.mShortName = mShortName;
        this.mTitle = mTitle;
        this.mStartDateTimestamp = mStartDateTimestamp;
        this.mStatus = mStatus;
        this.mId = mId;
        this.mVenue = mVenue;
        this.mFantasyPlayersList = mFantasyPlayersList;
        this.mStadiumId = mStadiumId;
    }

    public String getmMatchFormat() {
        return mMatchFormat;
    }

    public String getmKey() {
        return mKey;
    }

    public String getmWinnerTeam() {
        return mWinnerTeam;
    }

    public String getmName() {
        return mName;
    }

    public int getmSeasonId() {
        return mSeasonId;
    }

    public List<FantasyTeam> getmFantasyTeamList() {
        return mFantasyTeamList;
    }

    public String getmShortName() {
        return mShortName;
    }

    public String getmTitle() {
        return mTitle;
    }

    public long getmStartDateTimestamp() {
        return mStartDateTimestamp;
    }

    public String getmStatus() {
        return mStatus;
    }

    public int getmId() {
        return mId;
    }

    public String getmVenue() {
        return mVenue;
    }

    public List<FantasyPlayer> getmFantasyPlayersList() {
        return mFantasyPlayersList;
    }

    public int getmStadiumId() {
        return mStadiumId;
    }

    public class FantasyTeam {

        @SerializedName("name")
        private String mName;
        @SerializedName("short_name")
        private String mShortname;
        @SerializedName("icon_url")
        private String mIconUrl;

        public FantasyTeam(String mName, String mShortname, String mIconUrl) {
            this.mName = mName;
            this.mShortname = mShortname;
            this.mIconUrl = mIconUrl;
        }

        public String getmName() {
            return mName;
        }

        public String getmShortname() {
            return mShortname;
        }

        public String getmIconUrl() {
            return mIconUrl;
        }
    }

    public class FantasyPlayer {

        @SerializedName("rank_in_match")
        private int mRankInMatch;
        @SerializedName("seasonal_role")
        private String mSeasonalRole;
        @SerializedName("team_key")
        private String mTeamKey;
        @SerializedName("key")
        private String mPlayerKey;
        @SerializedName("is_batsman")
        private int mIsBatsman;
        @SerializedName("name")
        private String mName;
        @SerializedName("nationality")
        private String mNationality;
        @SerializedName("fullname")
        private String mFullName;
        @SerializedName("is_keeper")
        private int mIsKeeper;
        @SerializedName("is_bowler")
        private int mIsBowler;
        @SerializedName("credit_value")
        private double mCreditValue;
        @SerializedName("id")
        private int mId;
        @SerializedName("score")
        private double mScore;
        @SerializedName("date_of_birth")
        private String mDateOfBirth;
        @SerializedName("match_id")
        private int mMatchId;
        @SerializedName("gender")
        private String mGender;

        public FantasyPlayer(int mRankInMatch, String mSeasonalRole, String mTeamKey, String mPlayerKey, int mIsBatsman,
                             String mName, String mNationality, String mFullName, int mIsKeeper, int mIsBowler,
                             double mCreditValue, int mId, double mScore, String mDateOfBirth, int mMatchId, String mGender) {
            this.mRankInMatch = mRankInMatch;
            this.mSeasonalRole = mSeasonalRole;
            this.mTeamKey = mTeamKey;
            this.mPlayerKey = mPlayerKey;
            this.mIsBatsman = mIsBatsman;
            this.mName = mName;
            this.mNationality = mNationality;
            this.mFullName = mFullName;
            this.mIsKeeper = mIsKeeper;
            this.mIsBowler = mIsBowler;
            this.mCreditValue = mCreditValue;
            this.mId = mId;
            this.mScore = mScore;
            this.mDateOfBirth = mDateOfBirth;
            this.mMatchId = mMatchId;
            this.mGender = mGender;
        }

        public int getmRankInMatch() {
            return mRankInMatch;
        }

        public String getmSeasonalRole() {
            return mSeasonalRole;
        }

        public String getmTeamKey() {
            return mTeamKey;
        }

        public boolean isTeamA() {
            return getmTeamKey().equalsIgnoreCase("a");
        }

        public String getmPlayerKey() {
            return mPlayerKey;
        }

        public int getmIsBatsman() {
            return mIsBatsman;
        }

        public String getmName() {
            return mName;
        }

        public String getmNationality() {
            return mNationality;
        }

        public String getmFullName() {
            return mFullName;
        }

        public int getmIsKeeper() {
            return mIsKeeper;
        }

        public int getmIsBowler() {
            return mIsBowler;
        }

        public boolean isBowler() {
            return mIsBowler == 1 && mIsBatsman != 1 && mIsKeeper!=1? true:false;
        }

        public boolean isBatsman() {
            return mIsBatsman == 1 && mIsBowler != 1 && mIsKeeper != 1? true:false;
        }

        public boolean isKeeper() {
            return mIsKeeper == 1? true:false;
        }

        public boolean isAllRounder() {
            return mIsBatsman == 1 && mIsBowler == 1? true:false;
        }

        public double getmCreditValue() {
            return mCreditValue;
        }

        public int getmId() {
            return mId;
        }

        public double getmScore() {
            return mScore;
        }

        public String getmDateOfBirth() {
            return mDateOfBirth;
        }

        public int getmMatchId() {
            return mMatchId;
        }

        public String getmGender() {
            return mGender;
        }

        public boolean isMale() {
            return getmGender().equalsIgnoreCase("m")? true:false;
        }
    }

}
