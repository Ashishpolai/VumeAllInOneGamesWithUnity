package com.vume.allinonegames;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.tabs.TabLayout;
import com.vume.allinonegames.Adapters.MyFragmentPagerAdapter;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshFetchBalanceResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FantasyLobbyActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = LobbyActivity.class.getName();
    Context mActivityContext;

    private ImageSlider imageSlider;
    private ImageView btnHamburgerMenu;
    private DrawerLayout mDrawerLayout;

    private TextView menuProfile, menuAddmoney, menuWithdrawMoney, menuTransactionHistory, menuReferrals, menuContactUs, menuAboutus, menuLogOut,
    txtCurrentBalance;
    private LinearLayout addCashActionbar;

    private int lobbyCardApiFailCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_fantasy_lobby);

        mActivityContext = this;

        initUI();
    }

    private void initUI() {
        setupTabs();
        addCashActionbar = findViewById(R.id.add_cash_layout);
        txtCurrentBalance = findViewById(R.id.cash_balance);
        btnHamburgerMenu = findViewById(R.id.hamburger_menu);
        mDrawerLayout = findViewById(R.id.josh_drawer_layout);
        menuLogOut = findViewById(R.id.menu_logoout);
        menuProfile = findViewById(R.id.menu_profile);
        menuAddmoney = findViewById(R.id.menu_addcash);
        menuWithdrawMoney = findViewById(R.id.menu_withdrawcash);
        menuTransactionHistory = findViewById(R.id.menu_transactionhistory);
        menuReferrals = findViewById(R.id.menu_referralsandoffers);
        menuContactUs = findViewById(R.id.menu_contactus);
        menuAboutus = findViewById(R.id.menu_aboutus);

        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        btnHamburgerMenu.setOnClickListener(this);
        menuLogOut.setOnClickListener(this);
        menuProfile.setOnClickListener(this);
        menuAddmoney.setOnClickListener(this);
        menuWithdrawMoney.setOnClickListener(this);
        menuTransactionHistory.setOnClickListener(this);
        menuReferrals.setOnClickListener(this);
        menuContactUs.setOnClickListener(this);
        menuAboutus.setOnClickListener(this);
        addCashActionbar.setOnClickListener(this);

        retrieveCurrentBalanceAndBonus();
    }

    private void setupTabs(){
        ArrayList<SlideModel> imageList = new ArrayList<>();// Create image list

        // imageList.add(SlideModel("String Url" or R.drawable)
        // imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title

        imageList.add(new SlideModel(R.drawable.cricket, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.cricket,  ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.cricket,  ScaleTypes.FIT));

        imageSlider = findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);
        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int position) {
                Toast.makeText(FantasyLobbyActivity.this, "slide - "+position, Toast.LENGTH_SHORT).show();
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),
                FantasyLobbyActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hamburger_menu:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;

            case R.id.menu_logoout:
                    logout();
                break;

            case R.id.menu_profile:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent profileActivity = new Intent(mActivityContext, ProfileActivity.class);
                profileActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(profileActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.add_cash_layout:
            case R.id.menu_addcash:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent addcashActivity = new Intent(mActivityContext, AddCashActivity.class);
                addcashActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(addcashActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.menu_withdrawcash:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent withdrawcashActivity = new Intent(mActivityContext, WithdrawCashActivity.class);
                withdrawcashActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(withdrawcashActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.menu_referralsandoffers:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent referralsandoffersActivity = new Intent(mActivityContext, ReferralsAndOffersActivity.class);
                referralsandoffersActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(referralsandoffersActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.menu_contactus:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent contactusActivity = new Intent(mActivityContext, ContactUsActivity.class);
                contactusActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(contactusActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.menu_transactionhistory:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent transactionhistoryActivity = new Intent(mActivityContext, TransactionHistoryActivity.class);
                transactionhistoryActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(transactionhistoryActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.menu_aboutus:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent aboutusActivity = new Intent(mActivityContext, AboutUsActivity.class);
                aboutusActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(aboutusActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;
        }
    }

    private void logout(){
        JoshApplication.signOut(mActivityContext);
        goToLoginScreen();
    }

    private void goToLoginScreen(){
        Intent i = new Intent(mActivityContext, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
        finish();
    }

    public void retrieveCurrentBalanceAndBonus(){
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
                        JoshApplication.showFantasyErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                                errorMsg);
                        JoshApplication.getFantasyErrorDialog().setCancelable(false);
                        JoshApplication.getFantasyErrorDialog().setCanceledOnTouchOutside(false);

                        //RETRY 3 TIMES
                        JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
                        JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (lobbyCardApiFailCount < 3) {
                                    JoshApplication.closeFantasyErrorDialog(mActivityContext);
                                    lobbyCardApiFailCount++;
                                    retrieveCurrentBalanceAndBonus();
                                    JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                            + lobbyCardApiFailCount);
                                } else {
                                    JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                                    JoshApplication.closeFantasyErrorDialog(mActivityContext);
                                    finish();
                                }
                            }
                        });

                    }
                }
                @Override
                public void onFailure(Call<JoshFetchBalanceResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getLobbyBalanceApiCall UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showFantasyErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                            getResources().getString(R.string.server_not_responding));
                    JoshApplication.getFantasyErrorDialog().setCancelable(false);
                    JoshApplication.getFantasyErrorDialog().setCanceledOnTouchOutside(false);

                    //RETRY 3 TIMES
                    JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
                    JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lobbyCardApiFailCount < 3) {
                                JoshApplication.closeFantasyErrorDialog(mActivityContext);
                                lobbyCardApiFailCount++;
                                retrieveCurrentBalanceAndBonus();
                                JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                        + lobbyCardApiFailCount);
                            } else {
                                JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                                JoshApplication.closeFantasyErrorDialog(mActivityContext);
                                finish();
                            }
                        }
                    });
                }
            });
        }
        else{//NO INTERNET CONDITION
            JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showFantasyErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                    getResources().getString(R.string.no_internet_err));
            JoshApplication.getFantasyErrorDialog().setCancelable(false);
            JoshApplication.getFantasyErrorDialog().setCanceledOnTouchOutside(false);

            //RETRY 3 TIMES
            JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
            JoshApplication.getFantasyErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lobbyCardApiFailCount < 3) {
                        JoshApplication.closeFantasyErrorDialog(mActivityContext);
                        lobbyCardApiFailCount++;
                        retrieveCurrentBalanceAndBonus();
                        JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                + lobbyCardApiFailCount);
                    } else {
                        JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                        JoshApplication.closeFantasyErrorDialog(mActivityContext);
                        finish();
                    }
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