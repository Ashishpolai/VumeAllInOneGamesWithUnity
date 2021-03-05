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
import android.widget.Toast;

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
import com.vume.allinonegames.Adapters.FantasyAllContestsAdapter;
import com.vume.allinonegames.Model.FantasyContestsForMatchResponseData;
import com.vume.allinonegames.Model.FantasyGetMyTeamsResponseData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchContestsActivity extends AppCompatActivity {

    private static String TAG = MatchContestsActivity.class.getName();
    Context mActivityContext;

    public static final String SELECTED_MATCH_DATA = "selected_match_data";

    private TextView mMatchTitle, mOpponentOneName, mOpponentTwoName, mMatchStartTimeCountdown, noContestsError,
                totalPrizeHeading, entryFeeHeading, myTeamsCounter, myContestCounter,
            txtCurrentBalance;
    private ImageView mOpponentOneIcon, mOpponentTwoIcon, btnBack;
    private CountDownTimer countDownTimer;
    private RecyclerView mAllContestsRecyclerView;
    private View divider;
    private LinearLayout btnCreateOrEditTeam, btnMyContest;
    private Button btnCreateTeam;
    private LinearLayout addCashActionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_match_contests);

        mActivityContext = this;
        initUI();
    }

    private void initUI(){
        mOpponentOneName = findViewById(R.id.opponent_one_name);
        mOpponentTwoName = findViewById(R.id.opponent_two_name);
        mMatchStartTimeCountdown = findViewById(R.id.match_start_time);
        noContestsError = findViewById(R.id.no_contests_error);

        totalPrizeHeading = findViewById(R.id.total_prize_heading);
        entryFeeHeading = findViewById(R.id.entry_fee_heading);

        mOpponentOneIcon = findViewById(R.id.opponent_one_icon);
        mOpponentTwoIcon = findViewById(R.id.opponent_two_icon);

        myTeamsCounter = findViewById(R.id.createeditteam_count_indicator);
        myContestCounter  = findViewById(R.id.mycontest_count_indicator);

        mAllContestsRecyclerView = findViewById(R.id.allcontests_recyclerview);

        btnCreateTeam = findViewById(R.id.btn_create_team);
        btnCreateOrEditTeam = findViewById(R.id.createeditteam_layout);
        btnMyContest = findViewById(R.id.my_contest_layout);
        divider = findViewById(R.id.divider);

        btnBack = findViewById(R.id.back_menu);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addCashActionbar = findViewById(R.id.add_cash_layout);
        addCashActionbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addcashActivity = new Intent(mActivityContext, AddCashActivity.class);
                addcashActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(addcashActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
            }
        });

        txtCurrentBalance = findViewById(R.id.cash_balance);
        txtCurrentBalance.setText(getResources().getString(R.string.rupee_sign)+" "+JoshApplication.currentbalance(mActivityContext));

        setupMatchDetails();
        //getAllContestsDetails();

        btnCreateOrEditTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(JoshApplication.mySelectedMatchSavedTeamList.size()>0){
                    Toast.makeText(mActivityContext, "Teams already exist! Navigate to edit or create team page!", Toast.LENGTH_SHORT).show();
                    Intent createTeamIntent = new Intent(MatchContestsActivity.this, CreateTeamActivity.class);
                    createTeamIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(createTeamIntent);
                    overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                }
                else {
                    Intent createTeamIntent = new Intent(MatchContestsActivity.this, CreateTeamActivity.class);
                    createTeamIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(createTeamIntent);
                    overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                }
            }
        });

        btnCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createTeamIntent = new Intent(MatchContestsActivity.this, CreateTeamActivity.class);
                createTeamIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(createTeamIntent);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
            }
        });
    }

    private void getAllContestsDetails() {
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.loading_all_contests_formatch));
        if (JoshApplication.isInternetAvailable(mActivityContext)) {
            int matchId = JoshApplication.currentSelectedMatchData.getmId();

            // TODO: Remove below line Dummy Match Id
           // matchId = 40;

            JoshApplication.getFantasyAllContestsApiCall(getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), matchId).
                    enqueue(new Callback<List<FantasyContestsForMatchResponseData>>() {
                        @Override
                        public void onResponse(Call<List<FantasyContestsForMatchResponseData>> call, Response<List<FantasyContestsForMatchResponseData>> response) {
                            if (response.isSuccessful()) {
                                //JoshApplication.toast(mActivityContext, "Buyin success. got to webview");
                                List<FantasyContestsForMatchResponseData> allContestMatchData = response.body();
                                if(allContestMatchData.size()>0) {
                                    //new MatchesFragment.SortMatchData().execute();
                                    noContestsError.setVisibility(View.GONE);
                                    totalPrizeHeading.setVisibility(View.VISIBLE);
                                    entryFeeHeading.setVisibility(View.VISIBLE);
                                    mAllContestsRecyclerView.setVisibility(View.VISIBLE);
                                    FantasyAllContestsAdapter allContestsAdapter = new FantasyAllContestsAdapter(mActivityContext, allContestMatchData);
                                    mAllContestsRecyclerView.setHasFixedSize(true);
                                    mAllContestsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivityContext));
                                    mAllContestsRecyclerView.setAdapter(allContestsAdapter);
                                    //JoshApplication.closeSpinnerProgress(mActivityContext);
                                }
                                else{
                                    noContestsError.setVisibility(View.VISIBLE);
                                    totalPrizeHeading.setVisibility(View.GONE);
                                    entryFeeHeading.setVisibility(View.GONE);
                                    mAllContestsRecyclerView.setVisibility(View.GONE);
                                    //JoshApplication.closeSpinnerProgress(mActivityContext);
                                }
                                getMySavedTeams();
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
                        public void onFailure(Call<List<FantasyContestsForMatchResponseData>> call, Throwable t) {
                            JoshApplication.closeSpinnerProgress(mActivityContext);
                            Log.d(TAG, "getChangePasswordApiCall UnSuccessfull - " + t.getLocalizedMessage());
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

    private void getMySavedTeams(){
        JoshApplication.getMySavedTeamsForMatchApiCall(getResources().getString(R.string.josh_server_api_key),
                JoshApplication.currentLoginAuthTokenFromServer(mActivityContext),
                JoshApplication.currentSelectedMatchData.getmId()).
                enqueue(new Callback<List<FantasyGetMyTeamsResponseData>>() {
                    @Override
                    public void onResponse(Call<List<FantasyGetMyTeamsResponseData>> call, Response<List<FantasyGetMyTeamsResponseData>> response) {
                        if (response.isSuccessful()) {
                            List<FantasyGetMyTeamsResponseData> myTeamsList = response.body();
                            if(myTeamsList.size()>0){
                                JoshApplication.mySelectedMatchSavedTeamList = myTeamsList;
                                btnCreateTeam.setVisibility(View.INVISIBLE);
                                btnCreateOrEditTeam.setVisibility(View.VISIBLE);
                                divider.setVisibility(View.VISIBLE);
                                btnMyContest.setVisibility(View.VISIBLE);
                                myTeamsCounter.setText(String.valueOf(myTeamsList.size()));
                            }
                            else{ //No Teams created
                                btnCreateTeam.setVisibility(View.VISIBLE);
                                btnCreateOrEditTeam.setVisibility(View.GONE);
                                divider.setVisibility(View.GONE);
                                btnMyContest.setVisibility(View.GONE);
                            }
                            JoshApplication.closeSpinnerProgress(mActivityContext);
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
                                JoshApplication.getErrorDialog().setCancelable(false);
                                JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FantasyGetMyTeamsResponseData>> call, Throwable t) {
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        Log.d(TAG, "getChangePasswordApiCall UnSuccessfull - " + t.getLocalizedMessage());
                        JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                mActivityContext.getResources().getString(R.string.server_not_responding));

                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                        JoshApplication.getErrorDialog().setCancelable(false);
                        JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                    }
                });
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
    protected void onResume() {
        super.onResume();
        getAllContestsDetails();
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