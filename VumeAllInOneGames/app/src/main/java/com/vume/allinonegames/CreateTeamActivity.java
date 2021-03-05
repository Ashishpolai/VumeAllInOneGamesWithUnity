package com.vume.allinonegames;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.vume.allinonegames.Adapters.FantasyAllPlayersSelectAdapter;
import com.vume.allinonegames.Model.FantasyAllMatchesResponseData;
import com.vume.allinonegames.Model.FantasyGetMatchDetailsResponseData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTeamActivity extends AppCompatActivity implements View.OnClickListener, FantasyAllPlayersSelectAdapter.TeamSelectionUpdateInterface {

    private static final String TAG = com.vume.allinonegames.CreateTeamActivity.class.getName();
    Context mActivityContext;

    public static final String MATCH_ID_TOSHOW = "maytch_id_to_show";
    public static final int PLAYER_TYPE_KEEPER_INDEX = 0;
    public static final int PLAYER_TYPE_BATSMAN_INDEX = 1;
    public static final int PLAYER_TYPE_BOWLER_INDEX = 2;
    public static final int PLAYER_TYPE_ALLROUNDER_INDEX = 3;

    private ImageView mOpponentOneIcon, mOpponentTwoIcon, btnBack, btnKeeperIcon,
            btnBatsmanIcon, btnAllRounderIcon, btnBowlerIcon;
    private TextView mOpponentOneName, mOpponentTwoName, mMatchStartTimeCountdown, mTxtPlayerMinReq, noPlayersError,
                    selectedKeeperCountIndicator, selectedAllrounderCountIndicator, selectedBatsmanCountIndicator, selectedBowlerCountIndicator,
                    txtTotalPlayerSelectedIndicator, txtPlayerFromTeamsSelected, txtTotalCreditOfSelectedTeam, txtCurrentBalance;
    private CountDownTimer countDownTimer;
    private RecyclerView mPlayersRecyclerView;
    private Button btnNext;

    private FantasyAllMatchesResponseData currentMatchData = null;
    private int[] cricketerTypeArray = {PLAYER_TYPE_KEEPER_INDEX, PLAYER_TYPE_BATSMAN_INDEX, PLAYER_TYPE_BOWLER_INDEX, PLAYER_TYPE_ALLROUNDER_INDEX};
    private View[] cricketerTypeArrayView;
    private FantasyGetMatchDetailsResponseData fullMatchDetails;
    private LinearLayout addCashActionbar;

    private List<FantasyGetMatchDetailsResponseData.FantasyPlayer> selectedPlayerList = new ArrayList<>();
    private List<FantasyGetMatchDetailsResponseData.FantasyPlayer> fantasyPlayerList = new ArrayList<>();

    private FantasyAllPlayersSelectAdapter allContestsAdapter;
    private int selectedBatsmanCount = 0, selectedBowlerCount = 0, selectedAllrounderCount = 0, selectedKeeperCount = 0,
           selectedTeamAPlayers = 0, selectedTeamBPlayers = 0;
    private double totalCreditsOfSelectedPlayers = 0.0;
    private int teamAMaxLength = 0, teamBMaxLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_create_team);

        mActivityContext = this;

        currentMatchData = JoshApplication.currentSelectedMatchData;

        initUI();
    }

    private void initUI(){
        btnBack = findViewById(R.id.back_menu);
        btnNext = findViewById(R.id.btn_next);

        mOpponentOneName = findViewById(R.id.opponent_one_name);
        mOpponentTwoName = findViewById(R.id.opponent_two_name);
        mMatchStartTimeCountdown = findViewById(R.id.match_start_time);

        mOpponentOneIcon = findViewById(R.id.opponent_one_icon);
        mOpponentTwoIcon = findViewById(R.id.opponent_two_icon);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTxtPlayerMinReq = findViewById(R.id.txt_players_min_req);

        noPlayersError = findViewById(R.id.no_players_error);
        btnBatsmanIcon = findViewById(R.id.batsman_icon);
        btnKeeperIcon = findViewById(R.id.keeper_icon);
        btnBowlerIcon = findViewById(R.id.bowler_icon);
        btnAllRounderIcon = findViewById(R.id.allrounder_icon);
        selectedKeeperCountIndicator = findViewById(R.id.keeper_count_indicator);
        selectedBatsmanCountIndicator = findViewById(R.id.batsman_count_indicator);
        selectedBowlerCountIndicator = findViewById(R.id.bowler_count_indicator);
        selectedAllrounderCountIndicator = findViewById(R.id.allrounder_count_indicator);
        cricketerTypeArrayView = new View[]{btnKeeperIcon, btnBatsmanIcon, btnBowlerIcon, btnAllRounderIcon};

        txtTotalPlayerSelectedIndicator = findViewById(R.id.txt_all_players);
        txtPlayerFromTeamsSelected = findViewById(R.id.txt_team_players);
        txtTotalCreditOfSelectedTeam = findViewById(R.id.txt_all_creditdata);

        txtTotalPlayerSelectedIndicator.setText(
                getResources().getString(R.string.players_count, String.valueOf(selectedPlayerList.size()),
                        String.valueOf(JoshApplication.MAX_PLAYERS_REQ)));

        teamAMaxLength =  JoshApplication.currentSelectedMatchData.getTeams().get(0).getmTeamName().length() > 15 ?
                15 : JoshApplication.currentSelectedMatchData.getTeams().get(0).getmTeamName().length();
        teamBMaxLength =  JoshApplication.currentSelectedMatchData.getTeams().get(1).getmTeamName().length() > 15 ?
                15 : JoshApplication.currentSelectedMatchData.getTeams().get(1).getmTeamName().length();

        txtPlayerFromTeamsSelected.setText(
                getResources().getString(R.string.team_member_count,
                        JoshApplication.currentSelectedMatchData.getTeams().get(0).getmTeamName().substring(0, teamAMaxLength),
                        "0",JoshApplication.currentSelectedMatchData.getTeams().get(1).getmTeamName().substring(0, teamBMaxLength), "0" ));
        txtTotalCreditOfSelectedTeam.setText(
                getResources().getString(R.string.credit_count, "0", String.valueOf(JoshApplication.MAX_CREDITS_AVAILABLE)));

        btnBatsmanIcon.setOnClickListener(this);
        btnKeeperIcon.setOnClickListener(this);
        btnBowlerIcon.setOnClickListener(this);
        btnAllRounderIcon.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        addCashActionbar = findViewById(R.id.add_cash_layout);
        addCashActionbar.setOnClickListener(this);

        mPlayersRecyclerView = findViewById(R.id.selectplayer_recyclerview);

        mTxtPlayerMinReq.setText(getResources().getString(R.string.bowlers_min_req,
                String.valueOf(JoshApplication.MIN_BOWLER_REQ), String.valueOf(JoshApplication.MAX_BOWLER_REQ)));

        setupMatchDetails();
        setupAllMatchPlayerDetails();

        txtCurrentBalance = findViewById(R.id.cash_balance);
        txtCurrentBalance.setText(getResources().getString(R.string.rupee_sign)+" "+JoshApplication.currentbalance(mActivityContext));
    }

    private void setupMatchDetails(){
        if(JoshApplication.currentSelectedMatchData.getTeams().size()>=2) {
            mOpponentOneName.setText(JoshApplication.currentSelectedMatchData.getTeams().get(0).getmTeamShortName());
            mOpponentTwoName.setText(JoshApplication.currentSelectedMatchData.getTeams().get(1).getmTeamShortName());

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.mipmap.app_icon)
                    .fitCenter()
                    .error(R.mipmap.app_icon);


            Glide.with(mActivityContext)
                    .load(JoshApplication.currentSelectedMatchData.getTeams().get(0).getmTeamIconUrl().replace("http:", "https:"))
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("asisi", e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(mOpponentOneIcon);

            Glide.with(mActivityContext)
                    .load(JoshApplication.currentSelectedMatchData.getTeams().get(1).getmTeamIconUrl().replace("http:", "https:"))
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("asisi", e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(mOpponentTwoIcon);
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long currenttimestamp = System.currentTimeMillis();
        long expiryTime = (JoshApplication.currentSelectedMatchData.getmStartDateTimestampInSeconds()*1000)-currenttimestamp;
        if(expiryTime>0) {
            countDownTimer = new CountDownTimer(expiryTime, 1000) {

                public void onTick(long millisUntilFinished) {
                    mMatchStartTimeCountdown.setText(mActivityContext.getResources().getString(R.string.match_countdown_timer,
                            String.valueOf(JoshApplication.calculateTime(millisUntilFinished))));
                }

                public void onFinish() {
                    cancel();
                }

            }.start();
        }
        else{
            mMatchStartTimeCountdown.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_cash_layout:
                Intent addcashActivity = new Intent(mActivityContext, AddCashActivity.class);
                addcashActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(addcashActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.keeper_icon:
                    allContestsAdapter.setListdata(getFilteredPlayerData(PLAYER_TYPE_KEEPER_INDEX));
                    allContestsAdapter.notifyDataSetChanged();
                    mTxtPlayerMinReq.setText(getResources().getString(R.string.keeper_min_req,
                            String.valueOf(JoshApplication.MIN_KEEPER_REQ), String.valueOf(JoshApplication.MAX_KEEPER_REQ)));
                    setPlayerTypeUI(PLAYER_TYPE_KEEPER_INDEX);
                break;
            case R.id.batsman_icon:
                    allContestsAdapter.setListdata(getFilteredPlayerData(PLAYER_TYPE_BATSMAN_INDEX));
                    allContestsAdapter.notifyDataSetChanged();
                    mTxtPlayerMinReq.setText(getResources().getString(R.string.batsman_min_req,
                        String.valueOf(JoshApplication.MIN_BATSMAN_REQ), String.valueOf(JoshApplication.MAX_BATSMAN_REQ)));
                    setPlayerTypeUI(PLAYER_TYPE_BATSMAN_INDEX);
                break;
            case R.id.bowler_icon:
                    allContestsAdapter.setListdata(getFilteredPlayerData(PLAYER_TYPE_BOWLER_INDEX));
                    allContestsAdapter.notifyDataSetChanged();
                    mTxtPlayerMinReq.setText(getResources().getString(R.string.bowlers_min_req,
                        String.valueOf(JoshApplication.MIN_BOWLER_REQ), String.valueOf(JoshApplication.MAX_BOWLER_REQ)));
                    setPlayerTypeUI(PLAYER_TYPE_BOWLER_INDEX);
                break;
            case R.id.allrounder_icon:
                    allContestsAdapter.setListdata(getFilteredPlayerData(PLAYER_TYPE_ALLROUNDER_INDEX));
                    allContestsAdapter.notifyDataSetChanged();
                    mTxtPlayerMinReq.setText(getResources().getString(R.string.allrounder_min_req,
                            String.valueOf(JoshApplication.MIN_ALLROUNDER_REQ), String.valueOf(JoshApplication.MAX_ALLROUNDER_REQ)));
                    setPlayerTypeUI(PLAYER_TYPE_ALLROUNDER_INDEX);
                break;
            case R.id.btn_next:
                    if(selectedPlayerList.size() == JoshApplication.MAX_PLAYERS_REQ &&
                selectedBowlerCount >= JoshApplication.MIN_BOWLER_REQ && selectedBowlerCount <= JoshApplication.MAX_BOWLER_REQ
                && selectedBatsmanCount >= JoshApplication.MIN_BATSMAN_REQ && selectedBatsmanCount <= JoshApplication.MAX_BATSMAN_REQ
                && selectedKeeperCount >= JoshApplication.MIN_KEEPER_REQ && selectedKeeperCount <= JoshApplication.MAX_KEEPER_REQ
                && selectedAllrounderCount >= JoshApplication.MIN_ALLROUNDER_REQ && selectedAllrounderCount <= JoshApplication.MAX_ALLROUNDER_REQ
                    && totalCreditsOfSelectedPlayers <= JoshApplication.MAX_CREDITS_AVAILABLE){
                        Intent teamSelectionNext = new Intent(CreateTeamActivity.this, TeamSelectionNextActivity.class);
                        teamSelectionNext.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(teamSelectionNext);
                        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                    }
                    else{
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.error_title),
                                getResources().getString(R.string.invalid_team_selected_error));
                    }
                break;
        }
    }

    private void setPlayerTypeUI(int cricketerTypeView){
        for(int x =0;x<cricketerTypeArray.length;x++){
            if(cricketerTypeArray[x] == cricketerTypeView){
                cricketerTypeArrayView[x].setBackground(getResources().getDrawable(R.drawable.round_icon_with_shadow_selected));
            }
            else{
                cricketerTypeArrayView[x].setBackground(getResources().getDrawable(R.drawable.round_icon_with_shadow));
            }
        }
    }

    private void setupAllMatchPlayerDetails() {
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.loading_all_players_formatch));
        if (JoshApplication.isInternetAvailable(mActivityContext)) {
            int matchId = JoshApplication.currentSelectedMatchData.getmId();

            // TODO: Remove below line Dummy Match Id
             //matchId = 17;

            JoshApplication.getFantasyAllMatchDetailsApiCall(getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext),matchId).
                    enqueue(new Callback<FantasyGetMatchDetailsResponseData>() {
                        @Override
                        public void onResponse(Call<FantasyGetMatchDetailsResponseData> call, Response<FantasyGetMatchDetailsResponseData> response) {
                            if (response.isSuccessful()) {
                                fullMatchDetails = response.body();
                                fantasyPlayerList = fullMatchDetails.getmFantasyPlayersList();
                                if(fullMatchDetails.getmFantasyPlayersList().size()>0) {

                                    Collections.sort(fantasyPlayerList, new Comparator<FantasyGetMatchDetailsResponseData.FantasyPlayer>() {
                                        @Override
                                        public int compare(FantasyGetMatchDetailsResponseData.FantasyPlayer o1, FantasyGetMatchDetailsResponseData.FantasyPlayer o2) {
                                            return o1.getmRankInMatch()>o2.getmRankInMatch()?1:-1;
                                        }
                                    });

                                    noPlayersError.setVisibility(View.GONE);
                                    allContestsAdapter =
                                            new FantasyAllPlayersSelectAdapter(mActivityContext, getFilteredPlayerData(PLAYER_TYPE_BOWLER_INDEX));
                                    allContestsAdapter.setMatchDetailsData(fullMatchDetails);
                                    mPlayersRecyclerView.setHasFixedSize(true);
                                    mPlayersRecyclerView.setLayoutManager(new LinearLayoutManager(mActivityContext));
                                    mPlayersRecyclerView.setAdapter(allContestsAdapter);
                                    JoshApplication.closeSpinnerProgress(mActivityContext);
                                }
                                else{
                                    noPlayersError.setVisibility(View.VISIBLE);
                                    JoshApplication.closeSpinnerProgress(mActivityContext);
                                }
                            } else {
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                                String errorMsg, errorCode;
                                if (errorResponseObj != null) {
                                    errorMsg = errorResponseObj.getmMessage();
                                    errorCode = errorResponseObj.getmErrorCode();
                                    Log.d(TAG, "getFantasyAllMatchDetailsApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                                } else {
                                    errorMsg = getResources().getString(R.string.server_not_responding);
                                    errorCode = String.valueOf(response.code());
                                }

                                if(!errorCode.equalsIgnoreCase(ErrorUtils.MISSING_KYC_ERRORCODE)) {//No error dialogs for no kyc found errors
                                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                            errorMsg);
                                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<FantasyGetMatchDetailsResponseData> call, Throwable t) {
                            JoshApplication.closeSpinnerProgress(mActivityContext);
                            Log.d(TAG, "getFantasyAllMatchDetailsApiCall UnSuccessfull - " + t.getLocalizedMessage());
                            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                    mActivityContext.getResources().getString(R.string.server_not_responding));

                            JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                            JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                        }
                    });
        }
        else{//NO INTERNET CONDITION
            JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.no_internet_err_title),
                    mActivityContext.getResources().getString(R.string.no_internet_err));
            JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
            JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public void setSelectedFantasyPlayerList(List<FantasyGetMatchDetailsResponseData.FantasyPlayer> fantasyPlayerList) {
        selectedPlayerList.clear();
        selectedPlayerList.addAll(fantasyPlayerList);
        selectedTeamBPlayers = selectedTeamAPlayers = selectedBowlerCount = selectedBatsmanCount = selectedKeeperCount = selectedAllrounderCount = 0;
        totalCreditsOfSelectedPlayers = 0.0;
        for(FantasyGetMatchDetailsResponseData.FantasyPlayer fantasyPlayer : selectedPlayerList){
            if(fantasyPlayer.isBowler()) {
                selectedBowlerCount++;
            }
            else if(fantasyPlayer.isBatsman()){
                selectedBatsmanCount++;
            }
            else if(fantasyPlayer.isKeeper()){
                selectedKeeperCount++;
            }
            else if(fantasyPlayer.isAllRounder()){
                selectedAllrounderCount++;
            }

            if(fantasyPlayer.isTeamA()){
                selectedTeamAPlayers++;
            }
            else{
                selectedTeamBPlayers++;
            }

            totalCreditsOfSelectedPlayers+=fantasyPlayer.getmCreditValue();
        }
        selectedBowlerCountIndicator.setText(String.valueOf(selectedBowlerCount));
        selectedBatsmanCountIndicator.setText(String.valueOf(selectedBatsmanCount));
        selectedAllrounderCountIndicator.setText(String.valueOf(selectedAllrounderCount));
        selectedKeeperCountIndicator.setText(String.valueOf(selectedKeeperCount));
        txtPlayerFromTeamsSelected.setText(
                getResources().getString(R.string.team_member_count,
                        JoshApplication.currentSelectedMatchData.getTeams().get(0).getmTeamName().substring(0, teamAMaxLength),
                        String.valueOf(selectedTeamAPlayers),
                        JoshApplication.currentSelectedMatchData.getTeams().get(1).getmTeamName().substring(0, teamBMaxLength),
                        String.valueOf(selectedTeamBPlayers) ));
        txtTotalPlayerSelectedIndicator.setText(
                getResources().getString(R.string.players_count, String.valueOf(selectedPlayerList.size()),
                        String.valueOf(JoshApplication.MAX_PLAYERS_REQ)));
        txtTotalCreditOfSelectedTeam.setText(
                getResources().getString(R.string.credit_count, String.valueOf(totalCreditsOfSelectedPlayers), String.valueOf(JoshApplication.MAX_CREDITS_AVAILABLE)));
        allContestsAdapter.setSelectedPlayerCount(selectedBatsmanCount, selectedBowlerCount,
                selectedKeeperCount, selectedAllrounderCount, selectedTeamAPlayers, selectedTeamBPlayers, totalCreditsOfSelectedPlayers);

        JoshApplication.selectedPlayerList.clear();
        JoshApplication.selectedPlayerList.addAll(selectedPlayerList);
    }

    public List<FantasyGetMatchDetailsResponseData.FantasyPlayer> getFilteredPlayerData(int playerTypeIndex){
        List<FantasyGetMatchDetailsResponseData.FantasyPlayer> filteredPlayerData = new ArrayList<>();
        for(FantasyGetMatchDetailsResponseData.FantasyPlayer fantasyPlayer: fantasyPlayerList){
            if(playerTypeIndex == PLAYER_TYPE_KEEPER_INDEX){
                if(fantasyPlayer.isKeeper())
                    filteredPlayerData.add(fantasyPlayer);
            }
            else if(playerTypeIndex == PLAYER_TYPE_BATSMAN_INDEX){
                if(fantasyPlayer.isBatsman())
                    filteredPlayerData.add(fantasyPlayer);
            }
            else if(playerTypeIndex == PLAYER_TYPE_BOWLER_INDEX){
                if(fantasyPlayer.isBowler())
                    filteredPlayerData.add(fantasyPlayer);
            }
            else if(playerTypeIndex == PLAYER_TYPE_ALLROUNDER_INDEX){
                if(fantasyPlayer.isAllRounder())
                    filteredPlayerData.add(fantasyPlayer);
            }
        }
        return filteredPlayerData;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}