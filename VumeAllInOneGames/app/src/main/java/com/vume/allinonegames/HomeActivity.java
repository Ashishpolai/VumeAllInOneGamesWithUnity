package com.vume.allinonegames;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshFetchBalanceResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = HomeActivity.class.getName();
    Context mActivityContext;

    private TextView menuProfile, menuAddmoney, menuWithdrawMoney, menuTransactionHistory, menuReferrals, menuContactUs, menuAboutus, menuLogOut,
         txtCashBalance;
    private DrawerLayout mDrawerLayout;
    private ImageView imgHmaburgerMenu, btnOpenRummy, btnOpenCallbreak, btnOpenLudo, btnOpenPoker;
    private Button btnHome, btnFantasy, btnRefer, btnWallet;
    private LinearLayout btnAddCash;
    private ImageSlider imageSlider;
    private int lobbyCardApiFailCount = 0;

    boolean doubleBackToExitPressedOnce = false;
    boolean isUnityLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_home);

        mActivityContext = this;

        initUI();
        setupSlides();
        retrieveCurrentBalanceAndBonus();
        handleIntent(getIntent());
    }

    private void initUI(){
        mDrawerLayout = findViewById(R.id.josh_drawer_layout);

        menuProfile = findViewById(R.id.menu_profile);
        menuAddmoney = findViewById(R.id.menu_addcash);
        menuWithdrawMoney = findViewById(R.id.menu_withdrawcash);
        menuTransactionHistory = findViewById(R.id.menu_transactionhistory);
        menuReferrals = findViewById(R.id.menu_referralsandoffers);
        menuContactUs = findViewById(R.id.menu_contactus);
        menuAboutus = findViewById(R.id.menu_aboutus);
        menuLogOut = findViewById(R.id.menu_logoout);

        btnAddCash = findViewById(R.id.add_cash_layout);
        txtCashBalance = findViewById(R.id.cash_balance);

        imgHmaburgerMenu = findViewById(R.id.hamburger_menu);

        btnOpenRummy = findViewById(R.id.btn_open_rummy);
        btnOpenCallbreak = findViewById(R.id.btn_open_callbreak);
        btnOpenLudo = findViewById(R.id.btn_open_ludo);
        btnOpenPoker = findViewById(R.id.btn_open_poker);btnOpenPoker = findViewById(R.id.btn_open_poker);
        btnHome = findViewById(R.id.btn_home);
        btnFantasy = findViewById(R.id.btn_fantasy);
        btnRefer = findViewById(R.id.btn_refer);
        btnWallet = findViewById(R.id.btn_wallet);


        menuProfile.setOnClickListener(this);
        menuAddmoney.setOnClickListener(this);
        menuWithdrawMoney.setOnClickListener(this);
        menuTransactionHistory.setOnClickListener(this);
        menuReferrals.setOnClickListener(this);
        menuContactUs.setOnClickListener(this);
        menuAboutus.setOnClickListener(this);
        menuLogOut.setOnClickListener(this);
        imgHmaburgerMenu.setOnClickListener(this);
        btnAddCash.setOnClickListener(this);
        btnOpenRummy.setOnClickListener(this);
        btnOpenCallbreak.setOnClickListener(this);
        btnOpenLudo.setOnClickListener(this);
        btnOpenPoker.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnFantasy.setOnClickListener(this);
        btnRefer.setOnClickListener(this);
        btnWallet.setOnClickListener(this);

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
    }

    private void setupSlides(){
        ArrayList<SlideModel> imageList = new ArrayList<>();// Create image list

        imageList.add(new SlideModel(R.drawable.slide_one, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slide_two,  ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slide_three,  ScaleTypes.FIT));

        imageSlider = findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);
        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int position) {
                Toast.makeText(mActivityContext, "slide - "+position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

            case R.id.hamburger_menu:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;

            case R.id.btn_open_rummy:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                JoshApplication.setCurrentGameType(JoshApplication.RUMMY_GAME_TYPE);
                Intent rummyActivity = new Intent(mActivityContext, LobbyActivity.class);
                rummyActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(rummyActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.btn_open_callbreak:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                JoshApplication.setCurrentGameType(JoshApplication.CALLBREAK_GAME_TYPE);
                Intent callbreakActivity = new Intent(mActivityContext, LobbyActivity.class);
                callbreakActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(callbreakActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.btn_fantasy:
                JoshApplication.setCurrentGameType(JoshApplication.FANTASY_LEAGUE_GAME_TYPE);
                Intent fantasyLeagueActivity = new Intent(mActivityContext, FantasyLobbyActivity.class);
                fantasyLeagueActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(fantasyLeagueActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.btn_open_ludo:
                    loadUnity(JoshApplication.UNITY_GAME_TYPE_LUDO);
                break;
            case R.id.btn_open_poker:
                    loadUnity(JoshApplication.UNITY_GAME_TYPE_POKER);
                break;
            case R.id.btn_refer:
            case R.id.btn_wallet:
                Toast.makeText(mActivityContext, "Coming Soon!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void logout(){
        JoshApplication.signOut(mActivityContext);
        goToLoginScreen();
    }

    private void goToLoginScreen(){
        Intent i = new Intent(mActivityContext, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
        finish();
    }

    @Override
    public void onBackPressed() {

        if(mDrawerLayout!=null && mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        JoshApplication.toast(mActivityContext, getResources().getString(R.string.click_backtwice_to_close_msg));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
                        txtCashBalance.setText(getResources().getString(R.string.rupee_sign)+" "+JoshApplication.currentbalance(mActivityContext));
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        handleIntent(intent);
//        setIntent(intent);
    }

    void handleIntent(Intent intent) {
        if(intent == null || intent.getExtras() == null) return;

//        if(intent.getExtras().containsKey("setColor")){
//            View v = findViewById(R.id.button2);
//            switch (intent.getExtras().getString("setColor")) {
//                case "yellow": v.setBackgroundColor(Color.YELLOW); break;
//                case "red": v.setBackgroundColor(Color.RED); break;
//                case "blue": v.setBackgroundColor(Color.BLUE); break;
//                default: break;
//            }
//        }
    }

    public void loadUnity(String gameType) {
        isUnityLoaded = true;
        Intent intent = new Intent(this, MainUnityActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(JoshApplication.UNITY_GAME_TYPE_KEY, gameType);
        startActivityForResult(intent, 1);
    }

    public void unloadUnity(Boolean doShowToast) {
        if(isUnityLoaded) {
            Intent intent = new Intent(this, MainUnityActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("doQuit", true);
            startActivity(intent);
            isUnityLoaded = false;
        }
        else if(doShowToast) showToast("Show Unity First");
    }

    public void btnUnloadUnity(View v) {
        unloadUnity(true);
    }

    public void showToast(String message) {
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) isUnityLoaded = false;
    }
}