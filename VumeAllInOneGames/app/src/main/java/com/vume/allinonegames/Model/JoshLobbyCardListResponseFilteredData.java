package com.vume.allinonegames.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class JoshLobbyCardListResponseFilteredData {

    public static final String TABLE_CATEGORY_201POOL = "201-pool";
    public static final String TABLE_CATEGORY_101POOL = "101-pool";
    public static final String TABLE_CATEGORY_POINTS = "points";
    public static final String TABLE_CATEGORY_BESTOF = "bestof";
    public static final String TABLE_CATEGORY_TOURNAMENTS = "tournaments";

    public static final int TABLE_NO_OF_PLAYERS_TWO = 2;
    public static final int TABLE_NO_OF_PLAYERS_SIX = 6;
    public static final int TABLE_NO_OF_DEALS_TWO = 2;
    public static final int TABLE_NO_OF_DEALS_THREE = 3;

    String mTableCategory;
    String mTableType;
    String mTableDisplayName; //Hardcoded as per the  tablecategory as per the requirement
    String mTableShowOptionFor; //Hardcoded as per the  tablecategory as per the requirement i.e., tableLimit for bestof and numOfPlayers for others
    HashMap<Integer, ArrayList<JoshLobbyCardSubListData>> mShowOptionToAttributesMap = new HashMap<>();

    public JoshLobbyCardListResponseFilteredData(final String tableCatg, final String tableType, final HashMap<Integer, ArrayList<JoshLobbyCardSubListData> > showOptionForToAttributesMap) {
        mTableCategory = tableCatg;
        mTableType = tableType;
        mShowOptionToAttributesMap = showOptionForToAttributesMap;
        mTableDisplayName = findGetTableDisplayName(mTableCategory);
        mTableShowOptionFor = findGetTableShowOptionForValue();
    }

    public static String findGetTableDisplayName(String tableCatg){
        switch (tableCatg){
            case TABLE_CATEGORY_201POOL:
                return "201 POOL";

            case TABLE_CATEGORY_101POOL:
                return "101 POOL";

            case TABLE_CATEGORY_POINTS:
                return "POINTS\nRUMMY";

            case TABLE_CATEGORY_BESTOF:
                return "DEAL\nRUMMY";

            case TABLE_CATEGORY_TOURNAMENTS:
            default:
                return "NEW\nTOURNAMENTS";
        }
    }

    private String findGetTableShowOptionForValue(){
        switch (mTableCategory){
            case "201-pool":
            case "101-pool":
            case "points":
                return "numPlayers";
            case "bestof":
                return "tableLimit";

            default:
                return "numPlayers";
        }
    }

    public String getmTableCategory() {
        return mTableCategory;
    }

    public String getmTableType() {
        return mTableType;
    }

    public String getmTableDisplayName() {
        return mTableDisplayName;
    }

    public String getmTableShowOptionFor() {
        return mTableShowOptionFor;
    }

    public HashMap<Integer, ArrayList<JoshLobbyCardSubListData>> getmShowOptionToAttributesMap() {
        return mShowOptionToAttributesMap;
    }
}
