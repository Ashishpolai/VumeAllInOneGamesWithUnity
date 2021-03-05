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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.tabs.TabLayout;
import com.vume.allinonegames.Adapters.ContestDetailsPagerAdapter;
import com.vume.allinonegames.Model.FantasyContestDetailsWithMatchAndPlayerData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContestDetailsActivity extends AppCompatActivity {

    private static String TAG = com.vume.allinonegames.ContestDetailsActivity.class.getName();
    Context mActivityContext;

    private TextView mOpponentOneName, mOpponentTwoName, mMatchStartTimeCountdown,
       mTotalprizeMoney, mTotalSpotsLeft, mTotalSpots, mFirstPrizeAmount;
    private ImageView mOpponentOneIcon, mOpponentTwoIcon, btnBack;
    private CountDownTimer countDownTimer;
    private Button btnJoin;
    private ProgressBar teamsJoinedProgressBar;
    private Double firstPrizeAmount = 0.0, totalPrizeAmount = 0.0;
    private int teamsJoinedProgress = 0, totalWinners = 0;
    private String firstRankExtraPrize = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_contest_details);

        mActivityContext = this;
        initUI();
    }

    private void initUI(){
        mOpponentOneName = findViewById(R.id.opponent_one_name);
        mOpponentTwoName = findViewById(R.id.opponent_two_name);
        mMatchStartTimeCountdown = findViewById(R.id.match_start_time);

        mOpponentOneIcon = findViewById(R.id.opponent_one_icon);
        mOpponentTwoIcon = findViewById(R.id.opponent_two_icon);

        mTotalprizeMoney = findViewById(R.id.total_prize_pool);
        mTotalSpotsLeft = findViewById(R.id.contest_spots_left);
        mTotalSpots = findViewById(R.id.total_spots);
        mFirstPrizeAmount = findViewById(R.id.first_prize_amount);

        btnJoin = findViewById(R.id.btn_join);
        teamsJoinedProgressBar = findViewById(R.id.contest_teams_progressbar);

        btnBack = findViewById(R.id.back_menu);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setupMatchDetails();
        //getAllContestsDetailsWithPlayerandMatchData();
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

    private void setupTabs(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ContestDetailsPagerAdapter(getSupportFragmentManager(),
                com.vume.allinonegames.ContestDetailsActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void getAllContestsDetailsWithPlayerandMatchData() {
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.loading_contest_details));
        if (JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getFantasyContestDetailsIncludingMatchAndPlayerDetails(getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), JoshApplication.selectedContest.getmId()).
                    enqueue(new Callback<FantasyContestDetailsWithMatchAndPlayerData>() {
                        @Override
                        public void onResponse(Call<FantasyContestDetailsWithMatchAndPlayerData> call, Response<FantasyContestDetailsWithMatchAndPlayerData> response) {
                            if (response.isSuccessful()) {
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                JoshApplication.selectedContestWithAllDetails = response.body();
                                setupTabs();
                                setupPrizes();
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

                                if(!errorCode.equalsIgnoreCase(ErrorUtils.MISSING_KYC_ERRORCODE)) {//No error dialogs for no kyc found errors
                                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                            errorMsg);
                                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<FantasyContestDetailsWithMatchAndPlayerData> call, Throwable t) {
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
        }
    }

    private void setupPrizes(){
        final List<FantasyContestDetailsWithMatchAndPlayerData.FantasyprizesData> prizeDataList =
                JoshApplication.selectedContestWithAllDetails.getmPrizesList();
        totalPrizeAmount = 0.0;
        for(int x =0; x<prizeDataList.size(); x++){
            final FantasyContestDetailsWithMatchAndPlayerData.FantasyprizesData prizeData = prizeDataList.get(x);
            if(prizeData.getmMinRank() == 1){
                firstPrizeAmount = prizeData.getmPrizeAmount();
                firstRankExtraPrize = prizeData.getmExtraPrize();
            }

            if(x == (prizeDataList.size()-1)){
                totalWinners = prizeData.getmMaxrank();
            }

            if(prizeData.getmMaxrank() > prizeData.getmMinRank()) {
                totalPrizeAmount += prizeData.getmPrizeAmount() * (prizeData.getmMaxrank() - prizeData.getmMinRank());
            }
            else if(prizeData.getmMaxrank() == prizeData.getmMinRank()){
                totalPrizeAmount += prizeData.getmPrizeAmount();
            }
        }
        Double teamsJoinedProgressDouble = ((double)JoshApplication.selectedContestWithAllDetails.getmNumTeams()/
                JoshApplication.selectedContestWithAllDetails.getmMaxTeams()) * 100;
        teamsJoinedProgress = (int) Math.round(teamsJoinedProgressDouble);

        mTotalprizeMoney.setText(mActivityContext.getResources().getString(R.string.rupee_sign)+
                JoshApplication.getNumberInWordsRupee(Math.round(totalPrizeAmount)));
        mFirstPrizeAmount.setText(mActivityContext.getResources().getString(R.string.rupee_sign)+
                JoshApplication.getNumberInWordsRupee(Math.round(firstPrizeAmount)));
        btnJoin.setText(mActivityContext.getResources().getString(R.string.rupee_sign)+
                JoshApplication.selectedContestWithAllDetails.getmEntryFees());
        mTotalSpots.setText(mActivityContext.getResources().getString(R.string.total_spots_msg,
                String.valueOf(JoshApplication.selectedContestWithAllDetails.getmMaxTeams())));
        mTotalSpotsLeft.setText(mActivityContext.getResources().getString(R.string.spots_left_msg,
                String.valueOf(JoshApplication.selectedContestWithAllDetails.getmMaxTeams()
                        - JoshApplication.selectedContestWithAllDetails.getmNumTeams())));
        teamsJoinedProgressBar.setProgress(teamsJoinedProgress);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(JoshApplication.mySelectedMatchSavedTeamList.size()>0) {
                    Intent selectTeamForContestActivity = new Intent(mActivityContext, com.vume.allinonegames.JoinContestSelectTeamActivity.class);
                    selectTeamForContestActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    mActivityContext.startActivity(selectTeamForContestActivity);
                    overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                }
                else{
                    Toast.makeText(mActivityContext, mActivityContext.getResources().getString(R.string.no_saved_teams_found), Toast.LENGTH_SHORT).show();
                    Intent createTeamIntent = new Intent(mActivityContext, com.vume.allinonegames.CreateTeamActivity.class);
                    createTeamIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    mActivityContext.startActivity(createTeamIntent);
                    overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllContestsDetailsWithPlayerandMatchData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}