package com.vume.allinonegames;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vume.allinonegames.Model.JoshBuyInCallResponseData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshFetchBalanceResponseData;
import com.vume.allinonegames.Model.JoshLobbyCardListResponseFilteredData;
import com.vume.allinonegames.Model.JoshLobbyCardSubListData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;
import com.vume.allinonegames.Util.JoshSharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameSelectPriceActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = GameSelectPriceActivity.class.getName();
    Context mActivityContext;

    public static final String ARGS_BUYIN_TABLE_TICKETID = "joshbuyintableticketid";
    public static final String ARGS_BUYIN_TABLE_WAGERID = "joshbuyintablewagerid";
    public static final String ARGS_BUYIN_TABLEID = "joshbuyintableid";
    public static final String ARGS_BUYIN_AMOUNT = "joshbuyintableamount";
    public static final String ARGS_LAUNCHINGFROM = "joshlaunchinggamefrom";

    private String currentSelectedGameCategory;

    private TextView txtGameName, txtCurrentBalance, txtOnlineNumbers,
            txtTwoPlayersLabel, txtSixPlayersLabel, txtGameNameVal, txtPlayersVal, txtBetAmountVal, txtPlayersLabelBuyinMenu;
    private ImageView btnAddCash, greenRoundedSixPlayers, greenRoundedTwoPlayers, btnBack,
    btnSixPFiveRupees, btnSixPTenRupees, btnSixPTwentyRupees, btnSixPTwentyFiveRupees, btnSixPFiftyRupees, btnSixPHundredRupees,
    btnSixPTwoHundredFiftyRupees, btnSixPFiveHundredRupees, btnSixPOneThousandRupees, btnSixPTwoTHousandRupees, btnSixPFiveThousandRupees,
    btnSixPTenTHousandRupees, btnTwoPFiveRupees, btnTwoPTenRupees, btnTwoPTwentyFiveRupees, btnTwoPFiftyRupees, btnTwoPTwoFiftyRupees,
    btnTwoPFiveHundredRupees, btnTwoPTwoThousandRupees, btnTwoPFiveThousandRupees, imgTwoPlayersorDealsTab, imgSixpLayerorThreeDealsTab;
    private Button btnCancelBuyIn, btnPlayGame;
    private View switchPlayers;
    private Group mSixPlayersPriceCards, mTwoPlayersPriceCards, mBuyInConfirmDialog;

    private JoshLobbyCardListResponseFilteredData mCurrentLobbyCardData;
    private HashMap<Integer, ArrayList<JoshLobbyCardSubListData>> mNoOfPlayersToDataMap;
    private HashMap<Integer, JoshLobbyCardSubListData> mViewIDToPlayerDataMap = new HashMap<>();

    private int lobbyCardApiFailCount = 0;

    private JoshLobbyCardSubListData mCurrentLobbySelectedPriceData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_game_select_price);

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        currentSelectedGameCategory = getIntent().getExtras().getString(LobbyActivity.GAME_TYPE_ARGUMENT);

        initUI();
        setCurrentLobbyCardUI();
        //retrieveCurrentBalanceAndBonus();
    }

    private void initUI(){
        mSixPlayersPriceCards = findViewById(R.id.group_sixplayer_pricecards);
        mTwoPlayersPriceCards = findViewById(R.id.group_twoplayer_pricecards);
        mBuyInConfirmDialog = findViewById(R.id.group_buyinconfirm);
        mBuyInConfirmDialog.setVisibility(View.GONE);

        btnAddCash = findViewById(R.id.btn_addcash);
        greenRoundedTwoPlayers = findViewById(R.id.green_rounded_twoplayers);
        greenRoundedSixPlayers = findViewById(R.id.green_rounded_sixplayers);
        btnBack = findViewById(R.id.btn_back);

        txtGameName = findViewById(R.id.txt_gamename);
        txtOnlineNumbers = findViewById(R.id.txt_onlinenumbers);
        txtTwoPlayersLabel = findViewById(R.id.txt_twoplayers);
        txtSixPlayersLabel = findViewById(R.id.txt_sixplayers);
        txtCurrentBalance = findViewById(R.id.txt_price);

        txtPlayersLabelBuyinMenu = findViewById(R.id.txt_playerslabel);
        txtGameNameVal = findViewById(R.id.txt_gamenameval);
        txtPlayersVal = findViewById(R.id.txt_playersval);
        txtBetAmountVal = findViewById(R.id.txt_betamountval);

        switchPlayers = findViewById(R.id.switch_players);
        switchPlayers.bringToFront();

        btnCancelBuyIn = findViewById(R.id.btn_cancelbuyin);
        btnPlayGame = findViewById(R.id.btn_playgame);

        btnSixPFiveRupees = findViewById(R.id.card_fiverupees);
        btnSixPFiveRupees.setEnabled(false);
        btnSixPFiftyRupees = findViewById(R.id.card_fiftyrupees);
        btnSixPFiftyRupees.setEnabled(false);
        btnSixPFiveHundredRupees = findViewById(R.id.card_fivehundredrupees);
        btnSixPFiveHundredRupees.setEnabled(false);
        btnSixPFiveThousandRupees = findViewById(R.id.card_fivethousandrupees);
        btnSixPFiveThousandRupees.setEnabled(false);
        btnSixPHundredRupees = findViewById(R.id.card_hundredrupees);
        btnSixPHundredRupees.setEnabled(false);
        btnSixPOneThousandRupees = findViewById(R.id.card_onethousandrupees);
        btnSixPOneThousandRupees.setEnabled(false);
        btnSixPTenRupees = findViewById(R.id.card_tenrupees);
        btnSixPTenRupees.setEnabled(false);
        btnSixPTenTHousandRupees = findViewById(R.id.card_tenthousandrupees);
        btnSixPTenTHousandRupees.setEnabled(false);
        btnSixPTwentyFiveRupees = findViewById(R.id.card_twentyfiverupees);
        btnSixPTwentyFiveRupees.setEnabled(false);
        btnSixPTwentyRupees = findViewById(R.id.card_twentyrupees);
        btnSixPTwentyRupees.setEnabled(false);
        btnSixPTwoHundredFiftyRupees = findViewById(R.id.card_twofiftyrupees);
        btnSixPTwoHundredFiftyRupees.setEnabled(false);
        btnSixPTwoTHousandRupees = findViewById(R.id.card_twothousandrupees);
        btnSixPTwoTHousandRupees.setEnabled(false);

        btnTwoPFiveRupees = findViewById(R.id.card_twoplayer_fiverupees);
        btnTwoPFiveRupees.setEnabled(false);
        btnTwoPTenRupees = findViewById(R.id.card_twoplayer_tenrupees);
        btnTwoPTenRupees.setEnabled(false);
        btnTwoPTwentyFiveRupees = findViewById(R.id.card_twoplayer_twentyfiverupees);
        btnTwoPTwentyFiveRupees.setEnabled(false);
        btnTwoPFiftyRupees = findViewById(R.id.card_twoplayer_fiftyrupees);
        btnTwoPFiftyRupees.setEnabled(false);
        btnTwoPTwoFiftyRupees = findViewById(R.id.card_twoplayer_twofiftyrupees);
        btnTwoPTwoFiftyRupees.setEnabled(false);
        btnTwoPFiveHundredRupees = findViewById(R.id.card_twoplayer_fivehundredrupees);
        btnTwoPFiveHundredRupees.setEnabled(false);
        btnTwoPTwoThousandRupees = findViewById(R.id.card_twoplayer_twothousandrupees);
        btnTwoPTwoThousandRupees.setEnabled(false);
        btnTwoPFiveThousandRupees = findViewById(R.id.card_twoplayer_fivethousandrupees);
        btnTwoPFiveThousandRupees.setEnabled(false);

        imgTwoPlayersorDealsTab = findViewById(R.id.img_twoplayersordeals);
        imgSixpLayerorThreeDealsTab = findViewById(R.id.img_sixplayersorthreedeals);

        btnBack.setOnClickListener(this);
        switchPlayers.setOnClickListener(this);

        btnSixPFiveRupees.setOnClickListener(this);
        btnSixPFiftyRupees.setOnClickListener(this);
        btnSixPFiveHundredRupees.setOnClickListener(this);
        btnSixPFiveThousandRupees.setOnClickListener(this);
        btnSixPHundredRupees.setOnClickListener(this);
        btnSixPOneThousandRupees.setOnClickListener(this);
        btnSixPTenRupees.setOnClickListener(this);
        btnSixPTenTHousandRupees.setOnClickListener(this);
        btnSixPTwentyFiveRupees.setOnClickListener(this);
        btnSixPTwentyRupees.setOnClickListener(this);
        btnSixPTwoHundredFiftyRupees.setOnClickListener(this);
        btnSixPTwoTHousandRupees.setOnClickListener(this);

        btnTwoPFiveRupees.setOnClickListener(this);
        btnTwoPTenRupees.setOnClickListener(this);
        btnTwoPTwentyFiveRupees.setOnClickListener(this);
        btnTwoPFiftyRupees.setOnClickListener(this);
        btnTwoPTwoFiftyRupees.setOnClickListener(this);
        btnTwoPFiveHundredRupees.setOnClickListener(this);
        btnTwoPTwoThousandRupees.setOnClickListener(this);
        btnTwoPFiveThousandRupees.setOnClickListener(this);
        btnAddCash.setOnClickListener(this);

        btnCancelBuyIn.setOnClickListener(this);
        btnPlayGame.setOnClickListener(this);
    }

    private void setCurrentLobbyCardUI(){
        for(JoshLobbyCardListResponseFilteredData joshFilteredData : JoshApplication.joshFilteredLobbyCardsList()){
            if(joshFilteredData.getmTableCategory().equalsIgnoreCase(currentSelectedGameCategory)){
                mCurrentLobbyCardData = joshFilteredData;
                break;
            }
        }

        txtCurrentBalance.setText(getResources().getString(R.string.rupee_sign)+" "+JoshApplication.currentbalance(mActivityContext));
        txtGameName.setText(mCurrentLobbyCardData.getmTableDisplayName());

        mNoOfPlayersToDataMap = mCurrentLobbyCardData.getmShowOptionToAttributesMap();

        setViewIdToLobbyCardDataMap();

        setUIForSixPlayers();
    }

    private void setViewIdToLobbyCardDataMap(){
        Object[] mNoOfPlayersToDataMapKeysey = mNoOfPlayersToDataMap.keySet().toArray();
        Arrays.sort(mNoOfPlayersToDataMapKeysey);

        //Setting the tab labels
        txtTwoPlayersLabel.setText(mNoOfPlayersToDataMapKeysey[0].toString());
        txtSixPlayersLabel.setText( mNoOfPlayersToDataMapKeysey[1].toString());

        if(currentSelectedGameCategory.equalsIgnoreCase(JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_BESTOF)){
            imgTwoPlayersorDealsTab.setImageResource(R.mipmap.icon_twodeals_grey);
            imgSixpLayerorThreeDealsTab.setImageResource(R.mipmap.icon_multipledeals);
            setmViewIDToPlayerDataMapForOtherThanPointsRummy(mNoOfPlayersToDataMapKeysey);
        }
        else if(currentSelectedGameCategory.equalsIgnoreCase(JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_POINTS)){
            imgTwoPlayersorDealsTab.setImageResource(R.mipmap.icon_twoplayers_grey);
            imgSixpLayerorThreeDealsTab.setImageResource(R.mipmap.icon_multiplayers);
            setmViewIDToPlayerDataMapForPointsRummy(mNoOfPlayersToDataMapKeysey);
        }
        else{
            imgTwoPlayersorDealsTab.setImageResource(R.mipmap.icon_twoplayers_grey);
            imgSixpLayerorThreeDealsTab.setImageResource(R.mipmap.icon_multiplayers);
            setmViewIDToPlayerDataMapForOtherThanPointsRummy(mNoOfPlayersToDataMapKeysey);
        }



    }

    private void setmViewIDToPlayerDataMapForPointsRummy(Object[] mNoOfPlayersToDataMapKeysey){
        int count = 0;
        for(Object options : mNoOfPlayersToDataMapKeysey){
            if(count == 0) { //For lower option value
                for (JoshLobbyCardSubListData lobbydata : mNoOfPlayersToDataMap.get(options)) {
                    ImageView currentImg = null;
                    switch (String.valueOf(lobbydata.getmBetValue())) {
                        case "0.1":
                            currentImg = btnTwoPFiveRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_dotonerupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_fiverupees, lobbydata);
                            break;

                        case "0.2":
                            currentImg = btnTwoPTenRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_dottworupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_tenrupees, lobbydata);
                            break;

                        case "0.5":
                            currentImg = btnTwoPTwentyFiveRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_dotfiverupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_twentyfiverupees, lobbydata);
                            break;

                        case "1.0":
                            currentImg = btnTwoPFiftyRupees;
                            currentImg.setImageResource(R.mipmap.icon_onerupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_fiftyrupees, lobbydata);
                            break;

                        case "5.0":
                            currentImg = btnTwoPTwoFiftyRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_fiverupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_twofiftyrupees, lobbydata);
                            break;

                        case "10.0":
                            currentImg = btnTwoPFiveHundredRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_tenrupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_fivehundredrupees, lobbydata);
                            break;

                        case "25.0":
                            currentImg = btnTwoPTwoThousandRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_twentyfiverupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_twothousandrupees, lobbydata);
                            break;

                        case "50.0":
                            currentImg = btnTwoPFiveThousandRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_fifetyrupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_fivethousandrupees, lobbydata);
                            break;

                        default:

                            break;
                    }
                    if (currentImg != null) {
                        currentImg.setEnabled(true);
                        currentImg.setColorFilter(ContextCompat.getColor(mActivityContext,
                                R.color.DrawerTransaprentBG));
                    }
                }
                count++; //Used to check first occurence
            }
            else {//For HIGHER option value
                for (JoshLobbyCardSubListData lobbydata : mNoOfPlayersToDataMap.get(options)) {
                    ImageView currentImg = null;
                    switch (String.valueOf(lobbydata.getmBetValue())) {
                        case "0.05":
                            currentImg = btnSixPFiveRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_dotzerofiverupees);
                            mViewIDToPlayerDataMap.put(R.id.card_fiverupees, lobbydata);
                            break;

                        case "0.1":
                            currentImg = btnSixPTenRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_dotonerupees);
                            mViewIDToPlayerDataMap.put(R.id.card_tenrupees, lobbydata);
                            break;

                        case "0.2":
                            currentImg = btnSixPTwentyRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_dottworupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twentyrupees, lobbydata);
                            break;

                        case "0.25":
                            currentImg = btnSixPTwentyFiveRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_dottwentyfiverupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twentyfiverupees, lobbydata);
                            break;

                        case "0.5":
                            currentImg = btnSixPFiftyRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_dotfiverupees);
                            mViewIDToPlayerDataMap.put(R.id.card_fiftyrupees, lobbydata);
                            break;

                        case "1.0":
                            currentImg = btnSixPHundredRupees;
                            currentImg.setImageResource(R.mipmap.icon_onerupees);
                            mViewIDToPlayerDataMap.put(R.id.card_hundredrupees, lobbydata);
                            break;

                        case "2.0":
                            currentImg = btnSixPTwoHundredFiftyRupees;
                            currentImg.setImageResource(R.mipmap.icon_tworupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twofiftyrupees, lobbydata);
                            break;

                        case "5.0":
                            currentImg = btnSixPFiveHundredRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_fiverupees);
                            mViewIDToPlayerDataMap.put(R.id.card_fivehundredrupees, lobbydata);
                            break;

                        case "10.0":
                            currentImg = btnSixPOneThousandRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_tenrupees);
                            mViewIDToPlayerDataMap.put(R.id.card_onethousandrupees, lobbydata);
                            break;

                        case "25.0":
                            currentImg = btnSixPTwoTHousandRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_twentyfiverupees);
                            mViewIDToPlayerDataMap.put(R.id.card_twothousandrupees, lobbydata);
                            break;

                        case "40.0":
                            currentImg = btnSixPFiveThousandRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_fortyrupees);
                            mViewIDToPlayerDataMap.put(R.id.card_fivethousandrupees, lobbydata);
                            break;

                        case "50.0":
                            currentImg = btnSixPTenTHousandRupees;
                            currentImg.setImageResource(R.mipmap.icon_points_fifetyrupees);
                            mViewIDToPlayerDataMap.put(R.id.card_tenthousandrupees, lobbydata);
                            break;

                        default:

                            break;
                    }
                    if (currentImg != null) {
                        currentImg.setEnabled(true);
                        currentImg.setColorFilter(ContextCompat.getColor(mActivityContext,
                                R.color.DrawerTransaprentBG));
                    }
                }
            }
        }

        //No Avaialble price points error
        if(mViewIDToPlayerDataMap.size() == 0){
            JoshApplication.toast(mActivityContext, getResources().getString(R.string.no_available_pricepoints));
        }
    }

    private void setmViewIDToPlayerDataMapForOtherThanPointsRummy(Object[] mNoOfPlayersToDataMapKeysey){
        int count = 0;
        for(Object options : mNoOfPlayersToDataMapKeysey){
            if(count == 0) {//For lower option value
                for (JoshLobbyCardSubListData lobbydata : mNoOfPlayersToDataMap.get(options)) {
                    ImageView currentImg = null;
                    switch (Math.round(lobbydata.getmBetValue())) {
                        case 5:
                            currentImg = btnTwoPFiveRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_fiverupees, lobbydata);
                            break;

                        case 10:
                            currentImg = btnTwoPTenRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_tenrupees, lobbydata);
                            break;

                        case 25:
                            currentImg = btnTwoPTwentyFiveRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_twentyfiverupees, lobbydata);
                            break;

                        case 50:
                            currentImg = btnTwoPFiftyRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_fiftyrupees, lobbydata);
                            break;

                        case 250:
                            currentImg = btnTwoPTwoFiftyRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_twofiftyrupees, lobbydata);
                            break;

                        case 500:
                            currentImg = btnTwoPFiveHundredRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_fivehundredrupees, lobbydata);
                            break;

                        case 2000:
                            currentImg = btnTwoPTwoThousandRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_twothousandrupees, lobbydata);
                            break;

                        case 5000:
                            currentImg = btnTwoPFiveThousandRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twoplayer_fivethousandrupees, lobbydata);
                            break;

                        default:

                            break;
                    }
                    if (currentImg != null) {
                        currentImg.setEnabled(true);
                        currentImg.setColorFilter(ContextCompat.getColor(mActivityContext,
                                R.color.DrawerTransaprentBG));
                    }
                }
                count++; //Used to check first occurence
            }
            else {//For HIGHER option value
                for (JoshLobbyCardSubListData lobbydata : mNoOfPlayersToDataMap.get(options)) {
                    ImageView currentImg = null;
                    switch (Math.round(lobbydata.getmBetValue())) {
                        case 5:
                            currentImg = btnSixPFiveRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_fiverupees, lobbydata);
                            break;

                        case 10:
                            currentImg = btnSixPTenRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_tenrupees, lobbydata);
                            break;

                        case 20:
                            currentImg = btnSixPTwentyRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twentyrupees, lobbydata);
                            break;

                        case 25:
                            currentImg = btnSixPTwentyFiveRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twentyfiverupees, lobbydata);
                            break;

                        case 50:
                            currentImg = btnSixPFiftyRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_fiftyrupees, lobbydata);
                            break;

                        case 100:
                            currentImg = btnSixPHundredRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_hundredrupees, lobbydata);
                            break;

                        case 250:
                            currentImg = btnSixPTwoHundredFiftyRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twofiftyrupees, lobbydata);
                            break;

                        case 500:
                            currentImg = btnSixPFiveHundredRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_fivehundredrupees, lobbydata);
                            break;

                        case 1000:
                            currentImg = btnSixPOneThousandRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_onethousandrupees, lobbydata);
                            break;

                        case 2000:
                            currentImg = btnSixPTwoTHousandRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_twothousandrupees, lobbydata);
                            break;

                        case 5000:
                            currentImg = btnSixPFiveThousandRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_fivethousandrupees, lobbydata);
                            break;

                        case 10000:
                            currentImg = btnSixPTenTHousandRupees;
                            mViewIDToPlayerDataMap.put(R.id.card_tenthousandrupees, lobbydata);
                            break;

                        default:

                            break;
                    }
                    if (currentImg != null) {
                        currentImg.setEnabled(true);
                        currentImg.setColorFilter(ContextCompat.getColor(mActivityContext,
                                R.color.DrawerTransaprentBG));
                    }
                }
            }
        }

        //No Avaialble price points error
        if(mViewIDToPlayerDataMap.size() == 0){
            JoshApplication.toast(mActivityContext, getResources().getString(R.string.no_available_pricepoints));
        }
    }

    private void setUIForTwoPlayers(){
        greenRoundedTwoPlayers.setVisibility(View.VISIBLE);
        greenRoundedSixPlayers.setVisibility(View.GONE);
        mSixPlayersPriceCards.setVisibility(View.GONE);
        mTwoPlayersPriceCards.setVisibility(View.VISIBLE);
        if(currentSelectedGameCategory.equalsIgnoreCase(JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_BESTOF)){
            imgTwoPlayersorDealsTab.setImageResource(R.mipmap.icon_twodeals);
            imgSixpLayerorThreeDealsTab.setImageResource(R.mipmap.icon_multipledeals_grey);
        }
        else{
            imgTwoPlayersorDealsTab.setImageResource(R.mipmap.icon_twoplayers);
            imgSixpLayerorThreeDealsTab.setImageResource(R.mipmap.icon_multipleplayers_grey);
        }
        txtTwoPlayersLabel.setTextColor(getResources().getColor(R.color.white));
        txtSixPlayersLabel.setTextColor(getResources().getColor(R.color.grey_four));
    }

    private void setUIForSixPlayers(){
        greenRoundedTwoPlayers.setVisibility(View.GONE);
        greenRoundedSixPlayers.setVisibility(View.VISIBLE);
        mSixPlayersPriceCards.setVisibility(View.VISIBLE);
        mTwoPlayersPriceCards.setVisibility(View.GONE);
        if(currentSelectedGameCategory.equalsIgnoreCase(JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_BESTOF)){
            imgTwoPlayersorDealsTab.setImageResource(R.mipmap.icon_twodeals_grey);
            imgSixpLayerorThreeDealsTab.setImageResource(R.mipmap.icon_multipledeals);
        }
        else{
            imgTwoPlayersorDealsTab.setImageResource(R.mipmap.icon_twoplayers_grey);
            imgSixpLayerorThreeDealsTab.setImageResource(R.mipmap.icon_multiplayers);
        }
        txtTwoPlayersLabel.setTextColor(getResources().getColor(R.color.grey_four));
        txtSixPlayersLabel.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.switch_players:
                if(greenRoundedSixPlayers.getVisibility() == View.VISIBLE){
                    setUIForTwoPlayers();
                }
                else{
                    setUIForSixPlayers();
                }
                break;

            case R.id.card_twoplayer_fiftyrupees:
            case R.id.card_twoplayer_fivehundredrupees:
            case R.id.card_twoplayer_fiverupees:
            case R.id.card_twoplayer_fivethousandrupees:
            case R.id.card_twoplayer_tenrupees:
            case R.id.card_twoplayer_twentyfiverupees:
            case R.id.card_twoplayer_twofiftyrupees:
            case R.id.card_twoplayer_twothousandrupees:
            case R.id.card_fiverupees:
            case R.id.card_tenrupees:
            case R.id.card_twentyrupees:
            case R.id.card_twentyfiverupees:
            case R.id.card_fiftyrupees:
            case R.id.card_hundredrupees:
            case R.id.card_twofiftyrupees:
            case R.id.card_fivehundredrupees:
            case R.id.card_onethousandrupees:
            case R.id.card_twothousandrupees:
            case R.id.card_fivethousandrupees:
            case R.id.card_tenthousandrupees:
                showBuyInConfirmDialog(view);
                break;

            case R.id.btn_cancelbuyin:
                hideBuyInConfirmDialog();
                break;

            case R.id.btn_playgame:
                if (currentSelectedGameCategory.equalsIgnoreCase(JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_POINTS)) {
                    if(hasMaxBuyinBalanceToProceedForPointsGame()){ //MAX BUYIN BALANCE IS AVAILABLE
                        callBuyInApi(mCurrentLobbySelectedPriceData.getmTableId(), mCurrentLobbySelectedPriceData.getmMaxBuyIn());
                    }
                    else{
                        callBuyInApi(mCurrentLobbySelectedPriceData.getmTableId(), JoshApplication.currentbalance(mActivityContext));
                    }
                }
                else {
                    callBuyInApi(mCurrentLobbySelectedPriceData.getmTableId(), mCurrentLobbySelectedPriceData.getmBetValue());
                }
                break;

            case R.id.btn_addcash:
                Intent addcashLayoputActivity = new Intent(mActivityContext, AddCashActivity.class);
                addcashLayoputActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(addcashLayoputActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

        }
    }

    private void showInsufficientBalanceDialog(JoshLobbyCardListResponseFilteredData mLobbyFiltereddata, float currentFinalBetAmount){
        final Dialog insufficientCashDialog = new Dialog(mActivityContext, R.style.ErrorThemeDialogCustom);
        insufficientCashDialog.setContentView(R.layout.dialog_without_title);
        final Window window = insufficientCashDialog.getWindow();
        //((TextView) insufficientCashDialog.findViewById(R.id.txt_insufficientbuyindialog_title)).setText(mLobbyFiltereddata.getmTableDisplayName().replace("\n"," "));
        ((Button) insufficientCashDialog.findViewById(R.id.btn_left)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insufficientCashDialog.dismiss();
            }
        });
        ((Button) insufficientCashDialog.findViewById(R.id.btn_right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addcashActivity = new Intent(mActivityContext, AddCashActivity.class);
                addcashActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(addcashActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                insufficientCashDialog.dismiss();
            }
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int displayWidth = displayMetrics.widthPixels;
        final int displayHeight = displayMetrics.heightPixels;
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.4f);

        layoutParams.width = dialogWindowWidth;

        // Apply the newly created layout parameters to the alert dialog window
        window.setAttributes(layoutParams);
        insufficientCashDialog.show();
    }

    private  void callBuyInApi(final String tableId, final float betFinalValue){
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.joining_table));
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getBuyInDataApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), tableId, betFinalValue).enqueue(new Callback<JoshBuyInCallResponseData>() {
                @Override
                public void onResponse(Call<JoshBuyInCallResponseData> call, Response<JoshBuyInCallResponseData> response) {
                    if (response.isSuccessful()) {
                        final JoshBuyInCallResponseData mBuyinCallResponseData = response.body();
                        hideBuyInConfirmDialog();

                        JoshApplication.sendGameStartedEvent(mActivityContext, tableId, ""+betFinalValue);
                        openGameinWebView(mBuyinCallResponseData);

                        //JoshApplication.toast(mActivityContext, "Buyin success. got to webview");
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                    }
                    else{
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            Log.d(TAG, "getBuyInDataApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                        }
                        JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                errorMsg);
                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                    }
                }
                @Override
                public void onFailure(Call<JoshBuyInCallResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getBuyInDataApiCall UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                            mActivityContext.getResources().getString(R.string.server_not_responding));
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                }
            });
        }
        else{//NO INTERNET CONDITION
            JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.no_internet_err_title),
                    mActivityContext.getResources().getString(R.string.no_internet_err));
            JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
        }
    }

    private void openGameinWebView(final JoshBuyInCallResponseData mBuyInresponse){
        Intent gameWebviewIntent = new Intent(mActivityContext, GameWebviewActivity.class);
        JoshApplication.sharedPreferences(mActivityContext).saveToSharedPreferences(JoshSharedPreferences.SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME, true);
        Bundle buyInBundles = new Bundle();
        buyInBundles.putString(ARGS_BUYIN_TABLE_WAGERID, mBuyInresponse.getmWagerId());
        buyInBundles.putString(ARGS_BUYIN_TABLEID, mBuyInresponse.getmTableId());
        buyInBundles.putFloat(ARGS_BUYIN_AMOUNT, mBuyInresponse.getmBuyIn());
        gameWebviewIntent.putExtras(buyInBundles);
        gameWebviewIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mActivityContext.startActivity(gameWebviewIntent);
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }

    private void showBuyInConfirmDialog(View view){
        mCurrentLobbySelectedPriceData = mViewIDToPlayerDataMap.get(view.getId());
        if(!hasSufficientBalanceToProceed()){
            showInsufficientBalanceDialog(mCurrentLobbyCardData, mCurrentLobbySelectedPriceData.getmBetValue());
        }else {//Have Sufficient balance
            //JoshApplication.toast(mActivityContext, "show "+mViewIDToPlayerDataMap.get(view.getId()).getmTableId());
            txtGameNameVal.setText(mCurrentLobbyCardData.getmTableDisplayName().replace("\n", " "));
            if(currentSelectedGameCategory.equalsIgnoreCase(JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_POINTS)){
                txtPlayersLabelBuyinMenu.setText(getResources().getString(R.string.playerslabel));
                txtPlayersVal.setText(""+mCurrentLobbySelectedPriceData.getmNumPlayers());
                txtBetAmountVal.setText(getResources().getString(R.string.points_bet_amountvalue_label, mCurrentLobbySelectedPriceData.getmBetValue().toString()));
            }
            else if(currentSelectedGameCategory.equalsIgnoreCase(JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_BESTOF)){
                txtPlayersLabelBuyinMenu.setText(getResources().getString(R.string.deals_label));
                txtPlayersVal.setText(""+mCurrentLobbySelectedPriceData.getmTableLimit());
                txtBetAmountVal.setText(getResources().getString(R.string.bet_amountvalue_label,  mCurrentLobbySelectedPriceData.getmBetValue().toString()));
            }
            else {
                txtPlayersLabelBuyinMenu.setText(getResources().getString(R.string.playerslabel));
                txtPlayersVal.setText(""+mCurrentLobbySelectedPriceData.getmNumPlayers());
                txtBetAmountVal.setText(getResources().getString(R.string.bet_amountvalue_label,  mCurrentLobbySelectedPriceData.getmBetValue().toString()));
            }
            mBuyInConfirmDialog.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasSufficientBalanceToProceed(){
        return currentSelectedGameCategory.equalsIgnoreCase(JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_POINTS)?
                (mCurrentLobbySelectedPriceData.getmMinBuyIn()<=JoshApplication.currentbalance(mActivityContext)):
                (mCurrentLobbySelectedPriceData.getmBetValue()<=JoshApplication.currentbalance(mActivityContext));
    }

    private boolean hasMaxBuyinBalanceToProceedForPointsGame(){
        return (mCurrentLobbySelectedPriceData.getmMaxBuyIn()<=JoshApplication.currentbalance(mActivityContext));
    }

    private void hideBuyInConfirmDialog(){
        mBuyInConfirmDialog.setVisibility(View.GONE);
    }

    private boolean isBuyInDialogOpen(){
        return mBuyInConfirmDialog.getVisibility() == View.VISIBLE;
    }

    public void retrieveCurrentBalanceAndBonus(){
        if(JoshApplication.progressDialog != null && JoshApplication.progressDialog.isShowing()) {
            JoshApplication.changeSpinnerProgressDialogMessage(getResources().getString(R.string.fetching_lobby_balance));
        }else{
            JoshApplication.startSpinnerProgress(mActivityContext, getResources().getString(R.string.fetching_lobby_balance));
        }

        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getLobbyBalanceApiCall(getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext)).enqueue(new Callback<JoshFetchBalanceResponseData>() {
                @Override
                public void onResponse(Call<JoshFetchBalanceResponseData> call, Response<JoshFetchBalanceResponseData> response) {
                    if (response.isSuccessful()) {
                        JoshFetchBalanceResponseData mBalanceData = response.body();
                        JoshApplication.saveCurrentBonusMoney(mBalanceData.getmCurrentBonus());
                        JoshApplication.saveCurrentBalance(mBalanceData.getmCurrentBalanceBonusSum());
                        txtCurrentBalance.setText(getResources().getString(R.string.rupee_sign)+" "+JoshApplication.currentbalance(mActivityContext));
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                    }
                    else{
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg, errorCode;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            errorCode = errorResponseObj.getmErrorCode();
                            Log.d(TAG, "getLobbyBalanceApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                                errorMsg);

                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.ok_heading));
                    }
                }
                @Override
                public void onFailure(Call<JoshFetchBalanceResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getLobbyBalanceApiCall UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                            getResources().getString(R.string.server_not_responding));

                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.ok_heading));
                }
            });
        }
        else{//NO INTERNET CONDITION
            JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                    getResources().getString(R.string.no_internet_err));
            JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.ok_heading));
        }
    }

    @Override
    public void onBackPressed() {
        if(isBuyInDialogOpen()){
           hideBuyInConfirmDialog();
        }
        else {
            super.onBackPressed();
            overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
        }
    }

    @Override
    protected void onResume() {
        if(JoshApplication.sharedPreferences(mActivityContext).
                getSharedPrefBooleanValue(JoshSharedPreferences.SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME, false)) {
            retrieveCurrentBalanceAndBonus();
            JoshApplication.sharedPreferences(mActivityContext).
                    saveToSharedPreferences(JoshSharedPreferences.SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME, false);
        }
        super.onResume();
    }
}