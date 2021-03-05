package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FantasyGetMyTeamsResponseData {

    @SerializedName("id")
    public int mMyTeamId;
    @SerializedName("match_id")
    public int mMatchId;
    @SerializedName("user_id")
    public int mUserId;
    @SerializedName("name")
    public String mName;
    @SerializedName("captain")
    public int mCaptainId;
    @SerializedName("vice_captain")
    public int mViceCaptainId;
    @SerializedName("keeper")
    public int mKeeperId;
    @SerializedName("players")
    public List<MyFantasyPlayer> myFantasyPlayersList;

    public FantasyGetMyTeamsResponseData(int mMyTeamId,
                                         int mMatchId, int mUserId, String mName, int mCaptainId,
                                         int mViceCaptainId, int mKeeperId, List<MyFantasyPlayer> myFantasyPlayersList) {
        this.mMyTeamId = mMyTeamId;
        this.mMatchId = mMatchId;
        this.mUserId = mUserId;
        this.mName = mName;
        this.mCaptainId = mCaptainId;
        this.mViceCaptainId = mViceCaptainId;
        this.mKeeperId = mKeeperId;
        this.myFantasyPlayersList = myFantasyPlayersList;
    }

    public int getmMyTeamId() {
        return mMyTeamId;
    }

    public int getmMatchId() {
        return mMatchId;
    }

    public int getmUserId() {
        return mUserId;
    }

    public String getmName() {
        return mName;
    }

    public int getmCaptainId() {
        return mCaptainId;
    }

    public int getmViceCaptainId() {
        return mViceCaptainId;
    }

    public int getmKeeperId() {
        return mKeeperId;
    }

    public List<MyFantasyPlayer> getMyFantasyPlayersList() {
        return myFantasyPlayersList;
    }

    public MyFantasyPlayer getCaptainPlayer(){
        for(MyFantasyPlayer fantasyPlayer : getMyFantasyPlayersList()){
            if(fantasyPlayer.mPlayerId == getmCaptainId()){
                return fantasyPlayer;
            }
        }
        return null;
    }

    public MyFantasyPlayer getViceCaptainPlayer(){
        for(MyFantasyPlayer fantasyPlayer : getMyFantasyPlayersList()){
            if(fantasyPlayer.mPlayerId == getmViceCaptainId()){
                return fantasyPlayer;
            }
        }
        return null;
    }

    public static class MyFantasyPlayer{

        @SerializedName("seasonal_role")
        public String mPlayerRole;
        @SerializedName("is_batsman")
        public int mIsBatsmanInt;
        @SerializedName("name")
        public String mPlayerName;
        @SerializedName("fullname")
        public String mPlayerFullName;
        @SerializedName("is_keeper")
        public int mIsKeeperInt;
        @SerializedName("is_bowler")
        public int mIsBowlerInt;
        @SerializedName("credit_value")
        public double mCreditValue;
        @SerializedName("id")
        public int mPlayerId;
        @SerializedName("date_of_birth")
        public String mPlayerDateOfBirth;
        @SerializedName("gender")
        public String mGender;

        public MyFantasyPlayer(String mPlayerRole, int mIsBatsmanInt, String mPlayerName, String mPlayerFullName,
                               int mIsKeeperInt, int mIsBowlerInt, double mCreditValue, int mPlayerId,
                               String mPlayerDateOfBirth, String mGender) {
            this.mPlayerRole = mPlayerRole;
            this.mIsBatsmanInt = mIsBatsmanInt;
            this.mPlayerName = mPlayerName;
            this.mPlayerFullName = mPlayerFullName;
            this.mIsKeeperInt = mIsKeeperInt;
            this.mIsBowlerInt = mIsBowlerInt;
            this.mCreditValue = mCreditValue;
            this.mPlayerId = mPlayerId;
            this.mPlayerDateOfBirth = mPlayerDateOfBirth;
            this.mGender = mGender;
        }

        public String getmPlayerRole() {
            return mPlayerRole;
        }

        public int getmIsBatsmanInt() {
            return mIsBatsmanInt;
        }

        public boolean isBatsman() {
            return mIsBatsmanInt==1? true:false;
        }

        public String getmPlayerName() {
            return mPlayerName;
        }

        public String getmPlayerFullName() {
            return mPlayerFullName;
        }

        public int getmIsKeeperInt() {
            return mIsKeeperInt;
        }

        public boolean isKeeper() {
            return mIsKeeperInt==1? true:false;
        }

        public int getmIsBowlerInt() {
            return mIsBowlerInt;
        }

        public boolean isBowler() {
            return mIsBowlerInt==1? true:false;
        }

        public double getmCreditValue() {
            return mCreditValue;
        }

        public int getmPlayerId() {
            return mPlayerId;
        }

        public String getmPlayerDateOfBirth() {
            return mPlayerDateOfBirth;
        }

        public String getmGender() {
            return mGender;
        }
    }
}
