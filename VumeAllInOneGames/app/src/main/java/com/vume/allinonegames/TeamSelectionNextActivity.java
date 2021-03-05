package com.vume.allinonegames;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vume.allinonegames.Adapters.FantasyCaptainViceCaptainSelectAdapter;
import com.vume.allinonegames.Model.FantasyAllMatchesResponseData;
import com.vume.allinonegames.Model.FantasyCreateTeamResponseData;
import com.vume.allinonegames.Model.FantasyGetMatchDetailsResponseData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamSelectionNextActivity extends AppCompatActivity implements View.OnClickListener, FantasyCaptainViceCaptainSelectAdapter.TeamCapAndViceCapSelectionUpdateInterface {
    private static final String TAG = TeamSelectionNextActivity.class.getName();
    Context mActivityContext;
    private FantasyAllMatchesResponseData currentMatchData;
    private ImageView btnBack, btnQuery;
    private TextView txtTimeLeft;
    private EditText edtxtTeamName;

    private CountDownTimer countDownTimer;
    private RecyclerView mPlayersRecyclerView;

    private Button btnSaveTeam;
    int captainPlayerId = -1, vicecaptainPlayerId = -1, keeperPlayerId = -1;

    ArrayList<FantasyGetMatchDetailsResponseData.FantasyPlayer> keeperSelectedPlayerList = new ArrayList<>();
    ArrayList<FantasyGetMatchDetailsResponseData.FantasyPlayer> bowlerSelectedPlayerList = new ArrayList<>();
    ArrayList<FantasyGetMatchDetailsResponseData.FantasyPlayer> batsmanSelectedPlayerList = new ArrayList<>();
    ArrayList<FantasyGetMatchDetailsResponseData.FantasyPlayer> allrounderSelectedPlayerList = new ArrayList<>();
    ArrayList<FantasyGetMatchDetailsResponseData.FantasyPlayer> filteredSelectedPlayerList = new ArrayList<>();
    private FantasyGetMatchDetailsResponseData.FantasyPlayer captainPlayer, viceCaptainPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_team_selection_next);

        mActivityContext = this;
        currentMatchData = JoshApplication.currentSelectedMatchData;

        initUI();
    }

    private void initUI(){
        btnBack = findViewById(R.id.btn_back);
        btnQuery = findViewById(R.id.query_menu);
        btnSaveTeam = findViewById(R.id.btn_saveteam);

        edtxtTeamName = findViewById(R.id.edittext_teamname);

        txtTimeLeft = findViewById(R.id.heading_time_left);

        mPlayersRecyclerView = findViewById(R.id.selectplayer_recyclerview);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long currenttimestamp = System.currentTimeMillis();
        long expiryTime = (JoshApplication.currentSelectedMatchData.getmStartDateTimestampInSeconds()*1000)-currenttimestamp;
        if(expiryTime>0) {
            countDownTimer = new CountDownTimer(expiryTime, 1000) {

                public void onTick(long millisUntilFinished) {
                    txtTimeLeft.setText(mActivityContext.getResources().getString(R.string.time_left,
                            String.valueOf(JoshApplication.calculateTime(millisUntilFinished))));
                }

                public void onFinish() {
                    cancel();
                }

            }.start();
        }
        else{
            txtTimeLeft.setText("");
        }

        btnSaveTeam.setOnClickListener(this);

        FantasyCaptainViceCaptainSelectAdapter selectCaptainViceCaptainAdapter =
                new FantasyCaptainViceCaptainSelectAdapter(mActivityContext, filterSortWithPlayerType());
        mPlayersRecyclerView.setHasFixedSize(true);
        mPlayersRecyclerView.setLayoutManager(new LinearLayoutManager(mActivityContext));
        mPlayersRecyclerView.setAdapter(selectCaptainViceCaptainAdapter);
    }

    private List<FantasyGetMatchDetailsResponseData.FantasyPlayer> filterSortWithPlayerType(){
        for(FantasyGetMatchDetailsResponseData.FantasyPlayer fantasyPlayer : JoshApplication.selectedPlayerList){
            if(fantasyPlayer.isKeeper()) {
                keeperSelectedPlayerList.add(fantasyPlayer);
            }
            else if(fantasyPlayer.isBatsman()){
                batsmanSelectedPlayerList.add(fantasyPlayer);
            }
            else if(fantasyPlayer.isBowler()){
                bowlerSelectedPlayerList.add(fantasyPlayer);
            }
            else if(fantasyPlayer.isAllRounder()){
                allrounderSelectedPlayerList.add(fantasyPlayer);
            }
        }
        filteredSelectedPlayerList.addAll(keeperSelectedPlayerList);
        filteredSelectedPlayerList.addAll(batsmanSelectedPlayerList);
        filteredSelectedPlayerList.addAll(bowlerSelectedPlayerList);
        filteredSelectedPlayerList.addAll(allrounderSelectedPlayerList);
        return filteredSelectedPlayerList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    @Override
    public void setSelectedCaptainAndViceCaptain(FantasyGetMatchDetailsResponseData.FantasyPlayer captainPlayer, FantasyGetMatchDetailsResponseData.FantasyPlayer viceCaptainPlayer) {
        this.captainPlayer = captainPlayer;
        this.viceCaptainPlayer = viceCaptainPlayer;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_saveteam:
                if(filteredSelectedPlayerList.size()==JoshApplication.MAX_PLAYERS_REQ
                        && captainPlayer!=null
                        && viceCaptainPlayer!=null
                        && (!edtxtTeamName.getText().toString().equalsIgnoreCase(""))){

                   createTeam();
                }
                else if(edtxtTeamName.getText().toString().equalsIgnoreCase("")){
                    JoshApplication.showErrorDialog(mActivityContext,
                            getResources().getString(R.string.error_title),
                            getResources().getString(R.string.enter_your_team_name));
                }
                else{
                    JoshApplication.showErrorDialog(mActivityContext,
                            getResources().getString(R.string.error_title),
                            getResources().getString(R.string.select_captain_and_vicecaptain));
                }
                break;
        }
    }

    private void createTeam() {
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.saving_team));
        ArrayList<Integer> otherPlayerIdList = new ArrayList<>();

        for(FantasyGetMatchDetailsResponseData.FantasyPlayer fantasyPlayer : JoshApplication.selectedPlayerList){
            otherPlayerIdList.add(fantasyPlayer.getmId());
            if(fantasyPlayer.isKeeper()){
                keeperPlayerId = fantasyPlayer.getmId();
            }
        }

        final int matchId = JoshApplication.currentSelectedMatchData.getmId(); //59;
        //TODO: REMOVE THIS ONCE SERVER IS FIXED
        //JoshApplication.currentSelectedMatchData.getmId()

        if (JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.createFantasyTeamApiCall(getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext),
                    matchId,
                    edtxtTeamName.getText().toString(), otherPlayerIdList, captainPlayer.getmId(), viceCaptainPlayer.getmId(), keeperPlayerId).
                    enqueue(new Callback<FantasyCreateTeamResponseData>() {
                        @Override
                        public void onResponse(Call<FantasyCreateTeamResponseData> call, Response<FantasyCreateTeamResponseData> response) {
                            if (response.isSuccessful()) {
                                JoshApplication.toast(mActivityContext, getResources().getString(R.string.new_team_created));
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                Intent joinContestIntent = new Intent(mActivityContext, MatchContestsActivity.class);
                                joinContestIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                joinContestIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(joinContestIntent);
                                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                            } else {
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                                String errorMsg, errorCode;
                                if (errorResponseObj != null) {
                                    errorMsg = errorResponseObj.getmMessage();
                                    errorCode = errorResponseObj.getmErrorCode();
                                    Log.d(TAG, "getKycDetailsApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                                } else {
                                    errorMsg = getResources().getString(R.string.server_not_responding);
                                    errorCode = String.valueOf(response.code());
                                }

                                JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                        errorMsg);
                                JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                            }
                        }

                        @Override
                        public void onFailure(Call<FantasyCreateTeamResponseData> call, Throwable t) {
                            JoshApplication.closeSpinnerProgress(mActivityContext);
                            Log.d(TAG, "getChangePasswordApiCall UnSuccessfull - " + t.getLocalizedMessage());
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
            JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}