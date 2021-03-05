package com.vume.allinonegames;

import android.app.Activity;
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
import android.widget.RelativeLayout;
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
import com.vume.allinonegames.Adapters.FantasyAllMyTeamsAdapter;
import com.vume.allinonegames.Model.FantasyJoinContestResponseData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinContestSelectTeamActivity extends AppCompatActivity implements FantasyAllMyTeamsAdapter.SelectedTeamForContest {

    private static String TAG = com.vume.allinonegames.JoinContestSelectTeamActivity.class.getName();
    Context mActivityContext;

    private TextView mOpponentOneName, mOpponentTwoName, mMatchStartTimeCountdown,
            btnCancelJoin, btnJoinNow, totalWinningsAmountTxtview, entryFeeAmountTxtview,txtCurrentBalance;
    private ImageView mOpponentOneIcon, mOpponentTwoIcon, btnBack;
    private CountDownTimer countDownTimer;
    private RecyclerView mAllMyTeamsRecyclerView;
    private Button btnJoinWithTeam, btnCreateNewTeam;
    private RelativeLayout layoutJoinContest;
    private LinearLayout addCashActionbar;

    private int selectedTeamIdForContest = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_join_contest_select_team);

        mActivityContext = this;

        initUI();
    }

    private void initUI(){
        mOpponentOneName = findViewById(R.id.opponent_one_name);
        mOpponentTwoName = findViewById(R.id.opponent_two_name);
        mMatchStartTimeCountdown = findViewById(R.id.match_start_time);

        mOpponentOneIcon = findViewById(R.id.opponent_one_icon);
        mOpponentTwoIcon = findViewById(R.id.opponent_two_icon);

        btnJoinWithTeam = findViewById(R.id.btn_joinwithteam);
        btnCreateNewTeam = findViewById(R.id.btn_createnewteam);

        btnCancelJoin = findViewById(R.id.btn_canceljoin);
        btnJoinNow = findViewById(R.id.btn_joinnow);
        totalWinningsAmountTxtview = findViewById(R.id.totalwinnings_amount);
        entryFeeAmountTxtview = findViewById(R.id.entryfee_amount);

        layoutJoinContest = findViewById(R.id.join_contest_dialog);
        layoutJoinContest.setVisibility(View.GONE);

        mAllMyTeamsRecyclerView = findViewById(R.id.allmyteams_recyclerview);

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

        totalWinningsAmountTxtview.setText(getResources().getString(R.string.rupee_sign)+
                JoshApplication.selectedContest.getmTotalWinnings());
        entryFeeAmountTxtview.setText(getResources().getString(R.string.rupee_sign)+
                JoshApplication.selectedContest.getmEntryFees());

        FantasyAllMyTeamsAdapter allMyTeamsAdapter = new FantasyAllMyTeamsAdapter(mActivityContext, JoshApplication.mySelectedMatchSavedTeamList);
        mAllMyTeamsRecyclerView.setHasFixedSize(true);
        mAllMyTeamsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivityContext));
        mAllMyTeamsRecyclerView.setAdapter(allMyTeamsAdapter);

        btnJoinWithTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedTeamIdForContest>0) {
                    showJoinContestConfirmationDialog();
                }
                else{
                    JoshApplication.showErrorDialog(mActivityContext,
                            getResources().getString(R.string.error_title),
                            getResources().getString(R.string.select_team_for_contest_error));
                }
            }
        });

        btnCreateNewTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createTeamIntent = new Intent(mActivityContext, com.vume.allinonegames.CreateTeamActivity.class);
                createTeamIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(createTeamIntent);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
            }
        });

        btnCancelJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideJoinContestConfirmationDialog();
            }
        });

        btnJoinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedTeamIdForContest>0) {
                    joinContestApiCall();
                }
                else{
                    JoshApplication.showErrorDialog(mActivityContext,
                            getResources().getString(R.string.error_title),
                            getResources().getString(R.string.select_team_for_contest_error));
                }
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

    private void joinContestApiCall(){
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.joining_contest));
        if (JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.joinContestApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext),
                    String.valueOf(JoshApplication.selectedContest.getmId()), selectedTeamIdForContest).
                    enqueue(new Callback<FantasyJoinContestResponseData>() {
                        @Override
                        public void onResponse(Call<FantasyJoinContestResponseData> call, Response<FantasyJoinContestResponseData> response) {
                            if (response.isSuccessful()) {
                                JoshApplication.toast(mActivityContext, "You have joined contest!");
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                hideJoinContestConfirmationDialog();
                                onBackPressed();
                            } else {
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                                String errorMsg, errorCode;
                                if (errorResponseObj != null) {
                                    errorMsg = errorResponseObj.getmMessage();
                                    errorCode = errorResponseObj.getmErrorCode();
                                } else {
                                    errorMsg = mActivityContext.getResources().getString(R.string.server_not_responding);
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
                                            ((Activity)mActivityContext).finish();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<FantasyJoinContestResponseData> call, Throwable t) {
                            JoshApplication.closeSpinnerProgress(mActivityContext);
                            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                    mActivityContext.getResources().getString(R.string.server_not_responding));

                            JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                            JoshApplication.getErrorDialog().setCancelable(false);
                            JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ((Activity)mActivityContext).finish();
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
                    ((Activity)mActivityContext).finish();
                }
            });
        }
    }

    private void showJoinContestConfirmationDialog(){
        layoutJoinContest.setVisibility(View.VISIBLE);
    }

    private void hideJoinContestConfirmationDialog(){
        layoutJoinContest.setVisibility(View.GONE);
    }

    @Override
    public void setSelectedTeamIdForContest(int selectedTeamIdForContest) {
        this.selectedTeamIdForContest = selectedTeamIdForContest;
    }


    @Override
    public void onBackPressed() {
        if(layoutJoinContest.getVisibility()==View.VISIBLE){
            hideJoinContestConfirmationDialog();
        }
        else {
            super.onBackPressed();
            overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
        }
    }
}