package com.vume.allinonegames.Util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.vume.allinonegames.ApiCalls.MyRetrofitInstance;
import com.vume.allinonegames.ApiCalls.JoshRummyServicesInterface;
import com.vume.allinonegames.Model.FantasyAllMatchesResponseData;
import com.vume.allinonegames.Model.FantasyContestDetailsWithMatchAndPlayerData;
import com.vume.allinonegames.Model.FantasyContestsForMatchResponseData;
import com.vume.allinonegames.Model.FantasyCreateTeamRequestData;
import com.vume.allinonegames.Model.FantasyCreateTeamResponseData;
import com.vume.allinonegames.Model.FantasyGetMatchDetailsResponseData;
import com.vume.allinonegames.Model.FantasyGetMyTeamsResponseData;
import com.vume.allinonegames.Model.FantasyJoinContestRequestData;
import com.vume.allinonegames.Model.FantasyJoinContestResponseData;
import com.vume.allinonegames.Model.FirebaseAnalyticsCustomKeys;
import com.vume.allinonegames.Model.JoshAllTransactionsResponseData;
import com.vume.allinonegames.Model.JoshBankAccDetailsRequestData;
import com.vume.allinonegames.Model.JoshBankAccDetailsResponseData;
import com.vume.allinonegames.Model.JoshBuyInCallResponseData;
import com.vume.allinonegames.Model.JoshBuyinRequestData;
import com.vume.allinonegames.Model.JoshCancelWithdrawRequestData;
import com.vume.allinonegames.Model.JoshChangePassRequestData;
import com.vume.allinonegames.Model.JoshCloseTransactionRequest;
import com.vume.allinonegames.Model.JoshCloseTransactionResponseData;
import com.vume.allinonegames.Model.JoshFCMTokenUpdateRequestData;
import com.vume.allinonegames.Model.JoshFcmTokenUpdateResponseData;
import com.vume.allinonegames.Model.JoshFetchBalanceResponseData;
import com.vume.allinonegames.Model.JoshGenericOnlyStatusResponseData;
import com.vume.allinonegames.Model.JoshInitiateTransactionRequest;
import com.vume.allinonegames.Model.JoshInitiateTransactionResponseData;
import com.vume.allinonegames.Model.JoshInstallResponseData;
import com.vume.allinonegames.Model.JoshIsMobileNoRegisteredExistsResponseData;
import com.vume.allinonegames.Model.JoshKycResponseData;
import com.vume.allinonegames.Model.JoshLobbyCardListResponseData;
import com.vume.allinonegames.Model.JoshLobbyCardListResponseFilteredData;
import com.vume.allinonegames.Model.JoshLoginRequestData;
import com.vume.allinonegames.Model.JoshLoginResponseData;
import com.vume.allinonegames.Model.JoshOffersResponseData;
import com.vume.allinonegames.Model.JoshOpenGamesResponseData;
import com.vume.allinonegames.Model.JoshProfileDetailsResponseData;
import com.vume.allinonegames.Model.JoshRaiseWithdrawRequestData;
import com.vume.allinonegames.Model.JoshRegisterRequestData;
import com.vume.allinonegames.Model.JoshRegisterResponseData;
import com.vume.allinonegames.Model.JoshResetPasswordRequestData;
import com.vume.allinonegames.Model.JoshUpdateEmailRequest;
import com.vume.allinonegames.Model.JoshUpdateUsernameEmailRequest;
import com.vume.allinonegames.Model.JoshUpdateUsernameRequest;
import com.vume.allinonegames.Model.JoshWithdrawRaiseOrcancelResponse;
import com.vume.allinonegames.Model.JoshWithdrawalTransactionsResponseData;
import com.vume.allinonegames.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;


public class JoshApplication extends Application {
    private static volatile JoshApplication sInstance;

    public static final String NAVIGATED_FROM = "navigated_from_screen";

    private static String currentGameType = JoshApplication.CALLBREAK_GAME_TYPE;
    public static Context mContext;

    public static int DEFAULT_PROFILE_PIC = R.drawable.icon_mushprofile;
    public static int DEFAULT_KYC_FRONTPIC = R.mipmap.id_front;
    public static int DEFAULT_KYC_BACKPIC = R.mipmap.id_back;

    public static final String ALLMATCHES_LIMIT = "30";
    public static final String MYMATCHES_LIMIT = "30";

    public static String FANTASY_MATCH_STATUS_ALL = "all";
    public static String FANTASY_MATCH_STATUS_COMPLETED = "completed";
    public static String FANTASY_MATCH_STATUS_UPCOMING = "notstarted";
    public static String FANTASY_MATCH_STATUS_LIVE = "started";

    private static final String TAG = "JoshApplication";
    public static final String PASSWORD_KEY = "*";
    public static final String FCM_ID_TYPE = "fcm";

    public static final String GAME_SERVER_BASE_URL = "https://games.peka.ooo";
    public static final String BASE_URL = "https://api.peka.ooo";
    public static final String FANTASYLEAGUE_BASE_URL = BASE_URL; //"https://f5y.jaqk.in";

    public static final String RUMMY_GAME_TYPE = "rummy";
    public static final String CALLBREAK_GAME_TYPE = "callbreak";
    public static final String FANTASY_LEAGUE_GAME_TYPE = "fantasyleague";

    public static final int MIN_KEEPER_REQ = 1;
    public static final int MAX_KEEPER_REQ = 4;
    public static final int MIN_BATSMAN_REQ = 3;
    public static final int MAX_BATSMAN_REQ = 6;
    public static final int MIN_ALLROUNDER_REQ = 1;
    public static final int MAX_ALLROUNDER_REQ = 4;
    public static final int MIN_BOWLER_REQ = 3;
    public static final int MAX_BOWLER_REQ = 6;

    public static final int MAX_PLAYERS_REQ = 11;
    public static final int MAX_CREDITS_AVAILABLE = 100;

    public static final int activityAnimationIn = R.anim.blink;
    public static final int activityAnimationOut = R.anim.blink;

//Hide everything including status bar
    public static final int hidenavigationandstatusbarflags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    //Hide everything except status bar
    public static final int hidenavigationflags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;;

    public static final int currentApiversion = android.os.Build.VERSION.SDK_INT;

    public static ProgressDialog progressDialog;
    public static Dialog errorDialog, fantasyErrorDialog;

    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseUser mFirebaseuser;

    private final int shortAnimationDuration = 300;

    private static List<JoshLobbyCardListResponseData> mLobbyCardLists;
    private static List<JoshLobbyCardListResponseFilteredData> mFilteredLobbyCardLists;
    private static HashMap<String, JoshLobbyCardListResponseData> mTableIdToLobbyCardList = new HashMap<>();

    private static List<JoshLobbyCardListResponseData> mLobbyCallbreakCardLists;
    private static List<JoshLobbyCardListResponseFilteredData> mFilteredLobbyCallbreakCardLists;
    private static HashMap<String, JoshLobbyCardListResponseData> mTableIdToLobbyCallbreakCardList = new HashMap<>();

    public static boolean isFirstTimeOpenMadeForLobby = false;
    public static boolean isFirstTimeOpenMadeForGame = false;

    public static FantasyAllMatchesResponseData currentSelectedMatchData;
    public static List<FantasyGetMatchDetailsResponseData.FantasyPlayer> selectedPlayerList = new ArrayList<>();
    public static List<FantasyGetMyTeamsResponseData> mySelectedMatchSavedTeamList = new ArrayList<>();
    public static FantasyContestsForMatchResponseData selectedContest;
    public static FantasyContestDetailsWithMatchAndPlayerData selectedContestWithAllDetails;

    public JoshApplication() {
        if (sInstance != null) {
            throw new RuntimeException(
                    "Use getInstance() method to get the single instance of this class.");
        }
    }

    public static JoshApplication getInstance(Context context) {
        mContext = context;
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (sInstance == null) {
            synchronized (JoshApplication.class) {
                if (sInstance == null) sInstance = new JoshApplication();
            }
        }
        return sInstance;
    }

    //FIREBASE UTIL

    public static FirebaseAuth getMyFirebaseAuth(Context context){
        return JoshApplication.getInstance(context).getFirebaseAuth();
    }

    private FirebaseAuth getFirebaseAuth(){
        return mFirebaseAuth;
    }

    public static void setMyFirebaseAuth(Context context, FirebaseAuth mFAuth){
        JoshApplication.getInstance(context).setFirebaseAuth(mFAuth);
    }

    private void setFirebaseAuth(FirebaseAuth mFAuth){
        mFirebaseAuth = mFAuth;
    }

    public static FirebaseUser getMyFirebaseUser(Context context){
        return JoshApplication.getInstance(context).getFirebaseAuth().getCurrentUser();
    }

    public static boolean isLoggedIn(Context context){
        return (!JoshApplication.username(context).equalsIgnoreCase("")) && (!JoshApplication.currentLoginAuthTokenFromServer(context).equalsIgnoreCase(""));
    }
    //SHARED PREFERENCE UTIL

    public static JoshSharedPreferences sharedPreferences(Context context) {
        return JoshApplication.getInstance(context).getJoshSharedPreferences();
    }

    private JoshSharedPreferences getJoshSharedPreferences(){
        return JoshSharedPreferences.getInstance(mContext);
    }



    public static String installKey(Context context) {
        return JoshApplication.getInstance(context).getInstallKeyFromPreferences();
    }

    private String getInstallKeyFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.INSTALL_KEY, null);
    }

    public static void saveInstallKey(String installKey){
        saveInstallKeyToPreferences(installKey);
    }

    private static void saveInstallKeyToPreferences(String installKey){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.INSTALL_KEY, installKey);
    }


    public static String fcmToken(Context context) {
        return JoshApplication.getInstance(context).getFcmTokenFromPreferences();
    }

    private String getFcmTokenFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.CURRENTFCMTOKEN, null);
    }

    public static void saveFcmToken(String fcmToken){
        saveFcmTokeToPreferences(fcmToken);
    }

    private static void saveFcmTokeToPreferences(String fcmToken){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.CURRENTFCMTOKEN, fcmToken);
    }



    public static String firebaseCurrentAccessToken(Context context) {
        return JoshApplication.getInstance(context).getCurrentAccessTokenFromPreferences();
    }

    private static String getCurrentAccessTokenFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.AUTH_RESULT_USER_ID_TOKEN_, "");
    }

    public static void saveFirebaseCurrentAccessToken(String currentAccessToken){
        saveCurrentAccessTokenToPreferences(currentAccessToken);
    }

    private static void saveCurrentAccessTokenToPreferences(String currentAccessToken){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.AUTH_RESULT_USER_ID_TOKEN_, currentAccessToken);
    }



    public static String currentLoginAuthTokenFromServer(Context context) {
        return JoshApplication.getInstance(context).getLoginAuthTokenFromServerFromPreferences();
    }

    private static String getLoginAuthTokenFromServerFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.LOGIN_AUTHTOKEN, "");
    }

    public static void saveCurrentLoginAuthTokenFromServer(String currentLoginAuthToken){
        saveLoginAuthTokenFromServerToPreferences(currentLoginAuthToken);
    }

    private static void saveLoginAuthTokenFromServerToPreferences(String currentLoginAuthToken){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.LOGIN_AUTHTOKEN, currentLoginAuthToken);
    }



    public static String currentCredential(Context context) {
        return JoshApplication.getInstance(context).getCurrentCredentialFromPreferences();
    }

    private String getCurrentCredentialFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.AUTH_RESULT_CREDENTIAL, "");
    }

    public static void saveCredentials(String currentCredentials){
        saveCredentialsToPreferences(currentCredentials);
    }

    private static void saveCredentialsToPreferences(String currentCredentials){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.AUTH_RESULT_CREDENTIAL, currentCredentials);
    }



    public static int installId(Context context) {
        return JoshApplication.getInstance(context).getInstallIdFromPreferences();
    }

    private int getInstallIdFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefIntValue(JoshSharedPreferences.INSTALL_ID, 0);
    }

    public static void saveInstallId(int installId){
        saveInstallIdToPreferences(installId);
    }

    private static void saveInstallIdToPreferences(int installId){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.INSTALL_ID, installId);
    }



    public static int oauthUserId(Context context) {
        return JoshApplication.getInstance(context).getOAuthUserIdFromPreferences();
    }

    private int getOAuthUserIdFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefIntValue(JoshSharedPreferences.OAUTH_USER_ID, 0);
    }

    public static void saveOAuthUserId(int oauthUserId){
        saveOAuthUserIDToPreferences(oauthUserId);
    }

    private static void saveOAuthUserIDToPreferences(int oauthUserId){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.OAUTH_USER_ID, oauthUserId);
    }



    public static int userId(Context context) {
        return JoshApplication.getInstance(context).getUserIdFromPreferences();
    }

    public static void saveUserId(int userId){
        saveUserIDToPreferences(userId);
    }

    private int getUserIdFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefIntValue(JoshSharedPreferences.USER_ID, 0);
    }

    private static void saveUserIDToPreferences(int userId){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.USER_ID, userId);
    }



    public static int deviceId(Context context) {
        return JoshApplication.getInstance(context).getDeviceIdFromPreferences();
    }

    public static void saveDeviceId(int deviceId){
        saveDeviceIDToPreferences(deviceId);
    }

    private int getDeviceIdFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefIntValue(JoshSharedPreferences.DEVICE_ID, 0);
    }

    private static void saveDeviceIDToPreferences(int deviceId){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.DEVICE_ID, deviceId);
    }



    public static String username(Context context){
        return JoshApplication.getInstance(context).getUsernameFromPreferences();
    }

    public static void saveUsername(String username){
        saveUsernameToPreferences(username);
    }

    private String getUsernameFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.USER_NAME, "");
    }

    private static void saveUsernameToPreferences(String username){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.USER_NAME, username);
    }


    public static String email(Context context){
        return JoshApplication.getInstance(context).getEmailFromPreferences();
    }

    public static void saveEmail(String email){
        saveEmailToPreferences(email);
    }

    private String getEmailFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.EMAIL, "");
    }

    private static void saveEmailToPreferences(String email){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.EMAIL, email);
    }



    public static String password(Context context){
        return JoshApplication.getInstance(context).getPasswordFromPreferences();
    }

    public static void savePassword(String password){
        savePasswordToPreferences(password);
    }

    private String getPasswordFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.PASSWORD, "");
    }

    private static void savePasswordToPreferences(String password){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.PASSWORD, password);
    }



    public static boolean isRememberMeTicked(Context context){
        return JoshApplication.getInstance(context).getIsRememberMeTickedFromPreferences();
    }

    public static void saveRememberMeTick(boolean isRememberMe){
        saveIsRememberMeTickedToPreferences(isRememberMe);
    }

    private boolean getIsRememberMeTickedFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefBooleanValue(JoshSharedPreferences.REMEMBER_ME, false);
    }

    private static void saveIsRememberMeTickedToPreferences(boolean isRememberMe){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.REMEMBER_ME, isRememberMe);
    }



    public static String phoneno(Context context){
        return JoshApplication.getInstance(context).getPhoneNoFromPreferences();
    }

    public static void savePhoneno(String phoneno){
        savePhoneNoToPreferences(phoneno);
    }

    private String getPhoneNoFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.USER_PHONENO, "");
    }

    private static void savePhoneNoToPreferences(String phoneno){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.USER_PHONENO, phoneno);
    }

    public static String profilePicUrl(Context context){
        return JoshApplication.getInstance(context).getProfilePicUrlFromPreferences();
    }

    public static void saveProfilePicUrl(String profilePicUrl){
        saveProfilePicUrlToPreferences(profilePicUrl);
    }

    private String getProfilePicUrlFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.USER_PROFILEURL, "");
    }

    private static void saveProfilePicUrlToPreferences(String profilePicUrl){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.USER_PROFILEURL, profilePicUrl);
    }


    public static boolean isBankAccountVerified(Context context){
        return JoshApplication.getInstance(context).getIsBankAccountVerifiedFromPreferences();
    }

    public static void saveIsBankAccountVerfied(boolean isBankAccountVerified){
        saveIsBankAccoutnVerfiedToPreferences(isBankAccountVerified);
    }

    private boolean getIsBankAccountVerifiedFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefBooleanValue(JoshSharedPreferences.USER_ISBANKACCOUTNVERIFIED, false);
    }

    private static void saveIsBankAccoutnVerfiedToPreferences(boolean isBankAccountVerified){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.USER_ISBANKACCOUTNVERIFIED, isBankAccountVerified);
    }


    public static boolean isKYCVerified(Context context){
        return JoshApplication.getInstance(context).getIsKYCVerifiedFromPreferences();
    }

    public static void saveIsKYCVerfied(boolean isKYCVerified){
        saveIsKYCVerfiedToPreferences(isKYCVerified);
    }

    private boolean getIsKYCVerifiedFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefBooleanValue(JoshSharedPreferences.USER_ISKYCVERIFIED, false);
    }

    private static void saveIsKYCVerfiedToPreferences(boolean isKYCVerified){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.USER_ISKYCVERIFIED, isKYCVerified);
    }


    public static float totalBalanceWithBonus(Context context){
        return (JoshApplication.getInstance(context).getCurrentBalanceFromPreferences()+
                JoshApplication.getInstance(context).getCurrentBonusMoneyFromPreferences());
    }

    public static float currentbalance(Context context){
        return JoshApplication.getInstance(context).getCurrentBalanceFromPreferences();
    }

    public static void saveCurrentBalance(float currentbalance){
        saveCurrentBalanceToPreferences(currentbalance);
    }

    private float getCurrentBalanceFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefFloatValue(JoshSharedPreferences.CURRENTBALANCE, 0.0f);
    }

    private static void saveCurrentBalanceToPreferences(float currentbalance){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.CURRENTBALANCE, currentbalance);
    }


    public static float currentbonusmoney(Context context){
        return JoshApplication.getInstance(context).getCurrentBonusMoneyFromPreferences();
    }

    public static void saveCurrentBonusMoney(float currentbonusmoney){
        saveCurrentBonusMoneyToPreferences(currentbonusmoney);
    }

    private float getCurrentBonusMoneyFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefFloatValue(JoshSharedPreferences.CURRENTBONUSMONEY, 0.0f);
    }

    private static void saveCurrentBonusMoneyToPreferences(float currentbonusmoney){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.CURRENTBONUSMONEY, currentbonusmoney);
    }



    public static String firebaseuseruuid(Context context){
        return JoshApplication.getInstance(context).getFirebaseUserUUIDFromPreferences();
    }

    public static void saveFirebaseUserUUID(String firebaseuseruuid){
        saveFirebaseUserUUIDToPreferences(firebaseuseruuid);
    }

    private String getFirebaseUserUUIDFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.FIREBASE_USER_UUID, "");
    }

    private static void saveFirebaseUserUUIDToPreferences(String firebaseuseruuid){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.FIREBASE_USER_UUID, firebaseuseruuid);
    }



    public static String firebaseuseridtoken(Context context){
        return JoshApplication.getInstance(context).getFirebaseUserIDTokenFromPreferences();
    }

    public static void saveFirebaseUserIDToken(String firebaseuseridtoken){
        saveFirebaseUserIDTokenToPreferences(firebaseuseridtoken);
    }

    private String getFirebaseUserIDTokenFromPreferences(){
        return JoshSharedPreferences.getInstance(mContext).getSharedPrefStringValue(JoshSharedPreferences.FIREBASE_USER_ID_TOKEN, "");
    }

    private static void saveFirebaseUserIDTokenToPreferences(String firebaseuseridtoken){
        JoshSharedPreferences.getInstance(mContext).saveToSharedPreferences(JoshSharedPreferences.FIREBASE_USER_ID_TOKEN, firebaseuseridtoken);
    }

    //UTILS

    public static void loadDynamicLayoutInViewStubView(Context context, ConstraintLayout viewStubLayout, int newLayoutID){
        JoshApplication.getInstance(context).loadDynamicLayoutInViewStubView(viewStubLayout, newLayoutID);
    }

    private void loadDynamicLayoutInViewStubView(ConstraintLayout viewStubLayout, int newLayoutID){
        viewStubLayout.removeAllViews();
        final ConstraintLayout otpLayout = (ConstraintLayout) LayoutInflater.from(mContext).inflate(newLayoutID, viewStubLayout, false);
        viewStubLayout.addView(otpLayout);
        //JoshApplication.setVisibilityCrossfadeAnim(mContext, viewStubLayout, true);
    }

    public static void hideNavigationBarWithStatusBar(Context context, boolean setSystemVisibilityListener){
        JoshApplication.getInstance(context).hideNavigationBarWithStatusBar(setSystemVisibilityListener);
    }

    private void hideNavigationBarWithStatusBar(boolean setSystemVisibilityListener){
        final View decorView = ((Activity)mContext).getWindow().getDecorView();
        if(currentApiversion >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(hidenavigationandstatusbarflags);
            if(setSystemVisibilityListener) {
                decorView
                        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                            @Override
                            public void onSystemUiVisibilityChange(int visibility) {
                                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                    decorView.setSystemUiVisibility(hidenavigationandstatusbarflags);
                                }
                            }
                        });
            }
        }

        //Set Status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isLoggedIn(mContext)) {
            Window window = ((Activity)mContext).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mContext.getResources().getColor(R.color.statusbarcolor_postlogin));
        }
    }

    public static void hideStatusBarWhenTappedOnEdittext(Context context){
        JoshApplication.getInstance(context).hideStatusBarWhenTappedOnEdittext();
    }

    private void hideStatusBarWhenTappedOnEdittext(){
        ((Activity)mContext).requestWindowFeature(Window.FEATURE_NO_TITLE);
        ((Activity)mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void hideNavigationBar(Context context, boolean setSystemVisibilityListener){
        JoshApplication.getInstance(context).hideNavigationBar(setSystemVisibilityListener);
    }

    private void hideNavigationBar(boolean setSystemVisibilityListener){
        final View decorView = ((Activity)mContext).getWindow().getDecorView();
        if(currentApiversion >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(hidenavigationflags);
            if(setSystemVisibilityListener) {
                decorView
                        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                            @Override
                            public void onSystemUiVisibilityChange(int visibility) {
                                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                    decorView.setSystemUiVisibility(hidenavigationflags);
                                }
                            }
                });
            }
        }

        //Set Status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isLoggedIn(mContext)) {
            Window window = ((Activity)mContext).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mContext.getResources().getColor(R.color.statusbarcolor_postlogin));
        }
    }

    public static JoshLocaleUtils localeUtils(Context context) {
        return JoshApplication.getInstance(context).getJoshLocaleUtils();
    }

    private JoshLocaleUtils getJoshLocaleUtils(){
        return JoshLocaleUtils.getInstance(mContext);
    }

    public static void toast(Context context, String msg){
        JoshApplication.getInstance(context).showToast(msg);
    }

    private static void showToast( String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private static void showToast( String msg, int toast_time){
        Toast.makeText(mContext, msg, toast_time).show();
    }

    public static void customisedtoast(Context context, String msg, int toast_time){
        JoshApplication.getInstance(context).showToast(msg, toast_time);
    }

    public static void customisedtoast(Context context, String msg){
        JoshApplication.getInstance(context).showToast(msg);
    }


    public static int getCurrentCountryCode(Context context){
        return JoshApplication.getInstance(context).getCurrentCountryCode();
    }

    private int getCurrentCountryCode() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = telephonyManager.getSimCountryIso().toUpperCase();
        return PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryIso);
    }

    public static void closeInputKeybrd(Context context, View view){
        JoshApplication.getInstance(context).closeInputKeyboard(view);
    }

    private void closeInputKeyboard(View view){
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void startSpinnerProgress(Context context, String loadingMsg){
        JoshApplication.getInstance(context).startSpinnerProgressDialog(loadingMsg);
    }

    private void startSpinnerProgressDialog(String laodingMsg){
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(laodingMsg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void changeSpinnerProgressDialogMessage(String loadingMsg){
        if(progressDialog!=null){
            progressDialog.setMessage(loadingMsg);
        }
    }

    public static void closeSpinnerProgress(Context context){
        JoshApplication.getInstance(context).closeSpinnerProgressDialog();
    }

    private void closeSpinnerProgressDialog(){
        if(progressDialog!=null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static void showErrorDialog(Context context, String title, String errormsg){
        JoshApplication.getInstance(context).showErrorDialogMethod(title, errormsg);
    }

    private void showErrorDialogMethod(String title, String errormsg){
        errorDialog = new Dialog(mContext, R.style.ErrorThemeDialogCustom);
        errorDialog.setContentView(R.layout.dialog_without_title);
        errorDialog.setCanceledOnTouchOutside(true);
        errorDialog.setCancelable(true);
        final Window window = errorDialog.getWindow();
        //((TextView)errorDialog.findViewById(R.id.txt_errordialog_title)).setText(title);
        ((TextView)errorDialog.findViewById(R.id.txt_errordialog_msg)).setText(errormsg);
        ((Button) errorDialog.findViewById(R.id.btn_left)).setVisibility(View.INVISIBLE);
        ((Button) errorDialog.findViewById(R.id.btn_right)).setText(mContext.getResources().getString(R.string.got_it));
        ((Button) errorDialog.findViewById(R.id.btn_right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeErrorDialogMethod();
            }
        });
        ((Button) errorDialog.findViewById(R.id.btn_left)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeErrorDialogMethod();
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
        errorDialog.show();
    }

    public static Dialog getErrorDialog(){
        return  errorDialog;
    }

    public static Button getErrorDialogOkButton(Context context){
       return JoshApplication.getInstance(context).getErrorDialogActionButton();
    }

    private Button getErrorDialogActionButton(){
        return ((Button) errorDialog.findViewById(R.id.btn_right));
    }

    private Button getErrorDialogLeftButton(){
        return ((Button) errorDialog.findViewById(R.id.btn_left));
    }

    private Button getErrorDialogRightButton(){
        return ((Button) errorDialog.findViewById(R.id.btn_right));
    }

    public static Button getErrorDialogLeftButton(Context context){
        return JoshApplication.getInstance(context).getErrorDialogLeftButton();
    }

    public static Button getErrorDialogRightButton(Context context){
        return JoshApplication.getInstance(context).getErrorDialogRightButton();
    }

    public static void closeErrorDialog(Context context){
        JoshApplication.getInstance(context).closeErrorDialogMethod();
    }

    private void closeErrorDialogMethod(){
        if(errorDialog!=null){
            errorDialog.dismiss();
            errorDialog = null;
        }
    }

    //FANTASY ERROR DIALOG
    public static void showFantasyErrorDialog(Context context, String title, String errormsg){
        JoshApplication.getInstance(context).showFantasyErrorDialogMethod(title, errormsg);
    }

    private void showFantasyErrorDialogMethod(String title, String errormsg){
        fantasyErrorDialog = new Dialog(mContext, R.style.ErrorThemeDialogCustom);
        fantasyErrorDialog.setContentView(R.layout.dialog_without_title);
        fantasyErrorDialog.setCanceledOnTouchOutside(true);
        fantasyErrorDialog.setCancelable(true);
        final Window window = fantasyErrorDialog.getWindow();
        //((TextView)fantasyErrorDialog.findViewById(R.id.txt_errordialog_title)).setText(title);
        ((TextView)fantasyErrorDialog.findViewById(R.id.txt_errordialog_msg)).setText(errormsg);
        ((Button) fantasyErrorDialog.findViewById(R.id.btn_left)).setVisibility(View.INVISIBLE);
        ((Button) fantasyErrorDialog.findViewById(R.id.btn_right)).setText(mContext.getResources().getString(R.string.got_it));
        ((Button) fantasyErrorDialog.findViewById(R.id.btn_right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFantasyErrorDialogMethod();
            }
        });
        ((Button) fantasyErrorDialog.findViewById(R.id.btn_left)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFantasyErrorDialogMethod();
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
        fantasyErrorDialog.show();
    }

    public static Dialog getFantasyErrorDialog(){
        return  fantasyErrorDialog;
    }

    public static Button getFantasyErrorDialogOkButton(Context context){
        return JoshApplication.getInstance(context).getFantasyErrorDialogActionButton();
    }

    private Button getFantasyErrorDialogActionButton(){
        return ((Button) fantasyErrorDialog.findViewById(R.id.btn_right));
    }

    private Button getFantasyErrorDialogLeftButton(){
        return ((Button) fantasyErrorDialog.findViewById(R.id.btn_left));
    }

    private Button getFantasyErrorDialogRightButton(){
        return ((Button) fantasyErrorDialog.findViewById(R.id.btn_right));
    }

    public static Button getFantasyErrorDialogLeftButton(Context context){
        return JoshApplication.getInstance(context).getFantasyErrorDialogLeftButton();
    }

    public static Button getFantasyErrorDialogRightButton(Context context){
        return JoshApplication.getInstance(context).getFantasyErrorDialogRightButton();
    }

    public static void closeFantasyErrorDialog(Context context){
        JoshApplication.getInstance(context).closeFantasyErrorDialogMethod();
    }

    private void closeFantasyErrorDialogMethod(){
        if(fantasyErrorDialog!=null){
            fantasyErrorDialog.dismiss();
            fantasyErrorDialog = null;
        }
    }

    public static boolean isInternetAvailable(Context context){
        return JoshApplication.getInstance(context).isNetworkAvailable();
    }

    private boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static Call<JoshInstallResponseData> getRegisterInstallApiCall(String apiKey){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.sendInstallData(apiKey);
    }

    public static Call<JoshLoginResponseData> getRegisterLoginApiCall(String apiKey, String username, String password, String install_key){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        JoshLoginRequestData requestDataLoginObj = new JoshLoginRequestData(username, password, install_key);
        return myApiInterface.sendLoginData(apiKey, requestDataLoginObj);
    }

    public static Call<JoshBuyInCallResponseData> getBuyInDataApiCall(String apiKey, String accessToken, String tableId, float buyInValue){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        final JoshBuyinRequestData joshBuyinRequestData = new JoshBuyinRequestData(tableId, buyInValue);
        return myApiInterface.sendBuyInData(apiKey, accessToken, joshBuyinRequestData);
    }

    public static Call<JoshGenericOnlyStatusResponseData> getChangePasswordApiCall(String apiKey, String accessToken, String oldPassword, String newPassword){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        final JoshChangePassRequestData joshChangePassRequestData = new JoshChangePassRequestData(oldPassword, newPassword);
        return myApiInterface.sendChangePasswordRequest(apiKey, accessToken, joshChangePassRequestData);
    }

    public static Call<JoshFcmTokenUpdateResponseData> getFcmTokenUpdateApiCall(String apiKey, String installKey, String idType, String idValue){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        final JoshFCMTokenUpdateRequestData joshFcmTokenUpdateRequestData = new JoshFCMTokenUpdateRequestData(idType, idValue, installKey);
        return myApiInterface.sendFcmTokenUpdateData(apiKey, joshFcmTokenUpdateRequestData);
    }

    public static Call<JoshRegisterResponseData> getRegisterSignUpApiCall(String apiKey, String authResultUserIdToken,
                                                                          String authResultCredential,
                                                                          String installKey,
                                                                          String username,
                                                                          String password){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        JoshRegisterRequestData requestDataRegisterObj = new JoshRegisterRequestData(authResultUserIdToken, authResultCredential, installKey, username, password);
        return myApiInterface.sendRegisterData(apiKey, requestDataRegisterObj);
    }

    public static Call<JoshIsMobileNoRegisteredExistsResponseData> getIsMobileNoAlreadyExistApi(String apiKey,
                                                                                                String firebaseAuthToken,
                                                                                                String installKey,
                                                                                                String credential){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.sendIfMobileNoAlreadyExist(apiKey, firebaseAuthToken, installKey, credential);
    }

    public static Call<List<JoshLobbyCardListResponseData>> getLobbyCardListApiCall(String apiKey, String authResultUserIdToken,
                                                                                    String currentLatitude,
                                                                                    String currentLongtitude, String gameType){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getRummyTables(apiKey, authResultUserIdToken, currentLatitude, currentLongtitude, gameType);
    }

    public static Call<JoshInitiateTransactionResponseData> getInitiatePaytmTransactionApiCall(String apiKey, String authResultUserIdToken, double amount, String orderId, String paymentGateway){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getInitiatePaytmTransactionApi(apiKey, authResultUserIdToken, new JoshInitiateTransactionRequest(amount, orderId, paymentGateway));
    }

    public static Call<JoshCloseTransactionResponseData> getClosePaytmTransactionApiCall(String apiKey, String authResultUserIdToken, JoshCloseTransactionRequest closeTransctnrequest){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getClosePaytmTransactionApi(apiKey, authResultUserIdToken, closeTransctnrequest);
    }

    public static Call<List<JoshAllTransactionsResponseData>> getAllTransactionsApiCall(String apiKey, String authResultUserIdToken){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getAllTransactionsApi(apiKey, authResultUserIdToken);
    }

    public static Call<JoshGenericOnlyStatusResponseData> getResetPasswordApiCall(String apiKey, String password,
                                                                                    String firebaseAuthToken, String credential, String username){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getResetPasswordApi(apiKey, new JoshResetPasswordRequestData(password, firebaseAuthToken, credential, username));
    }

    public static Call<JoshGenericOnlyStatusResponseData> getUpdateProfileEmailApiCall(String apiKey, String authResultUserIdToken,
                                                                                   String email){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getUpdateEmailApi(apiKey, authResultUserIdToken, new JoshUpdateEmailRequest(email));
    }

    public static Call<JoshGenericOnlyStatusResponseData> getUpdateProfileUsernameApiCall(String apiKey, String authResultUserIdToken, String username){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getUpdateProfileApi(apiKey, authResultUserIdToken, new JoshUpdateUsernameRequest(username));
    }

    public static Call<JoshWithdrawRaiseOrcancelResponse> getRaiseWithdrawRequestApi(String apiKey, String authResultUserIdToken, int withdrawAmount){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getRaiseWithdrawRequestApi(apiKey, authResultUserIdToken, new JoshRaiseWithdrawRequestData(withdrawAmount));
    }

    public static Call<JoshWithdrawRaiseOrcancelResponse> getCancelWithdrawRequestApi(String apiKey, String authResultUserIdToken, int withdrawTxnId){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getCancelWithdrawRequestApi(apiKey, authResultUserIdToken, new JoshCancelWithdrawRequestData(withdrawTxnId));
    }

    public static Call<JoshWithdrawalTransactionsResponseData> getWithdrawTransactionsListApi(String apiKey, String authResultUserIdToken){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getWithdrawRequestListApi(apiKey, authResultUserIdToken);
    }

    public static Call<JoshGenericOnlyStatusResponseData> getUpdateUsernameEmailApiCall(String apiKey, String authResultUserIdToken, String emailid, String username){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getUpdateUsernameEmailDetailsApi(apiKey, authResultUserIdToken, new JoshUpdateUsernameEmailRequest(username, emailid));
    }

    public static Call<JoshProfileDetailsResponseData> getProfileDatasApiCall(String apiKey, String authResultUserIdToken){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getProfileDetails(apiKey, authResultUserIdToken);
    }

    public static Call<JoshBankAccDetailsResponseData> getBankAccDetailsApiCall(String apiKey, String authResultUserIdToken){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getBankAccDetails(apiKey, authResultUserIdToken);
    }

    public static Call<JoshGenericOnlyStatusResponseData> updateBankAccDetailsApiCall(String apiKey, String authResultUserIdToken, String accHolderName,
                                                                                   String bankCode, String accNo, String ifsc, String branch){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.sendBankAccDetailsData(apiKey, authResultUserIdToken, new JoshBankAccDetailsRequestData(accHolderName, bankCode, accNo, ifsc, branch));
    }

    public static Call<List<JoshOffersResponseData>> getOffersDetailsApiCall(){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getJoshOffers();
    }

    public static Call<List<JoshKycResponseData>> getKycDetailsApiCall(String apiKey, String accessToken){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getKycDetails(apiKey, accessToken);
    }

    public static Call<List<JoshOpenGamesResponseData>> getOpenGameApiCall(String apiKey, String accessToken){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getServerBaseUrlRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getOpenGames(apiKey, accessToken, currentGameType);
    }

    public static Call<JoshFetchBalanceResponseData> getLobbyBalanceApiCall(String apiKey, String accessToken){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getRummyBalance(apiKey, accessToken);
    }

    public static Call<List<FantasyAllMatchesResponseData>> getFantasyAllMatchesApiCall(String apiKey, String accessToken,
                                                                                        String limit, String offset, String status){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getFantasyLeagueBaseUrlRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getAllMatchesList(apiKey, accessToken, limit, offset, status);
    }

    public static Call<List<FantasyAllMatchesResponseData>> getFantasyMyMatchesApiCall(String apiKey, String accessToken,
                                                                                       String limit, String offset, String status){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getFantasyLeagueBaseUrlRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getMyMatchesList(apiKey, accessToken, limit, offset, status);
    }

    public static Call<List<FantasyContestsForMatchResponseData>> getFantasyAllContestsApiCall(String apiKey, String accessToken, int matchId){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getFantasyLeagueBaseUrlRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getAllContestsForTheMatch(apiKey, accessToken, matchId);
    }

    public static Call<FantasyContestDetailsWithMatchAndPlayerData> getFantasyContestDetailsIncludingMatchAndPlayerDetails
            (String apiKey, String accessToken, int contestId){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getFantasyLeagueBaseUrlRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getContestDetailsIncludingMatchAndPlayerDetails(apiKey, accessToken, contestId);
    }

    public static Call<FantasyGetMatchDetailsResponseData> getFantasyAllMatchDetailsApiCall(String apiKey, String accessToken, int matchId){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getFantasyLeagueBaseUrlRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getMatchDetails(apiKey, accessToken, matchId);
    }

    public static Call<FantasyCreateTeamResponseData> createFantasyTeamApiCall(String apiKey, String accessToken, int matchId,
                                                                               String name, List<Integer> mPlayersList,
                                                                               int mCaptain, int mViceCaptain, int mKeeper){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getFantasyLeagueBaseUrlRetrofitInstance().create(JoshRummyServicesInterface.class);
        FantasyCreateTeamRequestData fantasyCreateTeamRequestData = new FantasyCreateTeamRequestData(matchId, name,
                new FantasyCreateTeamRequestData.FantasyTeam(mPlayersList, mCaptain, mViceCaptain, mKeeper));
        return myApiInterface.createMyFantasyTeam(apiKey, accessToken, fantasyCreateTeamRequestData);
    }

    public static Call<FantasyJoinContestResponseData> joinContestApiCall(String apiKey, String accessToken,
                                                                          String contestId, int teamId){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getFantasyLeagueBaseUrlRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.joinContest(apiKey, accessToken, contestId, new FantasyJoinContestRequestData(teamId));
    }

    public static Call<List<FantasyGetMyTeamsResponseData>> getMySavedTeamsForMatchApiCall(String apiKey, String accessToken,
                                                                                           int matchId){
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getFantasyLeagueBaseUrlRetrofitInstance().create(JoshRummyServicesInterface.class);
        return myApiInterface.getMyTeamsForTheMatch(apiKey, accessToken, matchId);
    }

    public static boolean isNewInstall(Context context){
        return JoshSharedPreferences.getInstance(mContext).
                getSharedPrefBooleanValue(JoshSharedPreferences.FIRST_INSTALL, true);
    }

    public static void registerNewInstall(){
        isFirstTimeOpenMadeForLobby = true;
        isFirstTimeOpenMadeForGame = true;
        JoshSharedPreferences.getInstance(mContext).
                saveToSharedPreferences(JoshSharedPreferences.FIRST_INSTALL, false);
    }

    public static void togglePasswordTextVisibility(final EditText edttxtPassword, final ImageView btnToggle){
        //Toggle Password visibility
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edttxtPassword.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                    edttxtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnToggle.setImageResource(R.mipmap.password_invisible);
                }else{
                    edttxtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnToggle.setImageResource(R.mipmap.password_visible);
                }
                edttxtPassword.setSelection(edttxtPassword.getText().length());
            }
        });
    }

    //RUMMY
    public static List<JoshLobbyCardListResponseData> joshLobbyCardsList(){
        return mLobbyCardLists;
    }

    public static JoshLobbyCardListResponseData joshLobbyCardDataForTableID(String tableId){
        return mTableIdToLobbyCardList.get(tableId);
    }

    public static void setJoshLobbyCardsList(List<JoshLobbyCardListResponseData> lobbyCardList){
        mLobbyCardLists = lobbyCardList;
        for(JoshLobbyCardListResponseData lobbyCardData : lobbyCardList){
            mTableIdToLobbyCardList.put(lobbyCardData.getmTableId(), lobbyCardData);
        }
    }

    public static List<JoshLobbyCardListResponseFilteredData> joshFilteredLobbyCardsList(){
        return mFilteredLobbyCardLists;
    }

    public static void setJoshFilteredLobbyCardsList(List<JoshLobbyCardListResponseFilteredData> filteredlobbyCardList){
        mFilteredLobbyCardLists = filteredlobbyCardList;
    }

    //CALLBREAK
    public static List<JoshLobbyCardListResponseData> joshLobbyCallbreakCardsList(){
        return mLobbyCallbreakCardLists;
    }

    public static JoshLobbyCardListResponseData joshLobbyCallbreakCardDataForTableID(String tableId){
        return mTableIdToLobbyCallbreakCardList.get(tableId);
    }

    public static void setJoshLobbyCallbreakCardsList(List<JoshLobbyCardListResponseData> lobbyCardList){
        mLobbyCallbreakCardLists = lobbyCardList;
        for(JoshLobbyCardListResponseData lobbyCardData : lobbyCardList){
            mTableIdToLobbyCallbreakCardList.put(lobbyCardData.getmTableId(), lobbyCardData);
        }
    }

    public static List<JoshLobbyCardListResponseFilteredData> joshFilteredLobbyCallbreakCardsList(){
        return mFilteredLobbyCallbreakCardLists;
    }

    public static void setJoshFilteredLobbyCallbreakCardsList(List<JoshLobbyCardListResponseFilteredData> filteredlobbyCardList){
        mFilteredLobbyCallbreakCardLists = filteredlobbyCardList;
    }

    public static String selectPredictedCountryCode(Spinner spinner, String automatedCountryCode){
        return selectAutomaticCountryCode(spinner, automatedCountryCode);
    }

    private static String selectAutomaticCountryCode(Spinner spinner, String countrycode){
        int index = 0;
        String currentCountryCode = "";
        for (int i=0;i<spinner.getCount();i++){
            String spinnerCurrentValue = spinner.getItemAtPosition(i).toString().trim();
            spinnerCurrentValue = spinnerCurrentValue.split("\\(")[1];
            spinnerCurrentValue = spinnerCurrentValue.substring(0, spinnerCurrentValue.length()-1);
            if (spinnerCurrentValue.equals(countrycode)){
                index = i;
                currentCountryCode = spinnerCurrentValue;
            }
        }
        spinner.setSelection(index);
        return currentCountryCode;
    }

    public static  void setVisibilityCrossfadeAnim(Context context, View view, boolean makeVisible){
        if(makeVisible){
            JoshApplication.getInstance(context).makeCrossfadeVisibility(view);
        }
        else{
            JoshApplication.getInstance(context).makeCrossfadeInvisibility(view);
        }
    }

    private void makeCrossfadeVisibility(final View view){
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

    }

    private void makeCrossfadeInvisibility(final View view){
        view.animate()
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(shortAnimationDuration/2)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public static void hideKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void signOut(Context context){
        if(JoshApplication.getMyFirebaseAuth(context)!=null)
            JoshApplication.getMyFirebaseAuth(context).signOut();
        if(!JoshApplication.isRememberMeTicked(context)){//Remove username password from preferences if remember me is not ticked
            JoshApplication.saveUsername("");
            JoshApplication.savePassword("");
        }
        JoshApplication.saveFirebaseCurrentAccessToken("");
        JoshApplication.saveCurrentLoginAuthTokenFromServer("");
        JoshApplication.saveCredentials("");
        JoshApplication.saveUserId(0);
        JoshApplication.saveOAuthUserId(0);
        JoshApplication.saveDeviceId(0);
        JoshApplication.saveIsKYCVerfied(false);
        JoshApplication.saveIsBankAccountVerfied(false);
        JoshApplication.saveProfilePicUrl(null);
        JoshApplication.saveEmail("");
        JoshApplication.savePhoneno("");
        JoshApplication.saveCurrentBalance(0);
    }

    //FIREBASE ANALYTICS
    public static void sendLoginEvent(Context context, String signInMethod){
        JoshApplication.getInstance(context).sendLoginAnalyticsEvent(signInMethod);
    }

    public void sendLoginAnalyticsEvent(String signinMethod){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, signinMethod);
        logAnalyticsEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    public static void sendLoginErrorEvent(Context context, String errorMsg, String errorCode, String loginMethod){
        JoshApplication.getInstance(context).sendLoginErrorAnalyticsEvent(errorMsg, errorCode, loginMethod);
    }

    public void sendLoginErrorAnalyticsEvent(String errorMsg, String errorCode, String loginMethod){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARMATER_LOGINERROR_MSG, errorMsg);
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARMATER_LOGINERROR_CODE, errorCode);
        bundle.putString(FirebaseAnalytics.Param.METHOD, loginMethod);
        logAnalyticsEvent(FirebaseAnalyticsCustomKeys.EVENT_LOGIN_ERROR, bundle);
    }

    public static void sendRegisterEvent(Context context){
        JoshApplication.getInstance(context).sendRegisterAnalyticsEvent();
    }

    public void sendRegisterAnalyticsEvent(){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, FirebaseAnalyticsCustomKeys.LOGIN_PARAMETER_METHOD_VALUE_USERNAMEPASS);
        logAnalyticsEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
    }

    public static void sendNewInstallEvent(Context context){
        JoshApplication.getInstance(context).sendNewInstallAnalyticsEvent();
    }

    private void sendNewInstallAnalyticsEvent(){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARMATER_INSTALLKEY, JoshApplication.installKey(mContext));
        logAnalyticsEvent(FirebaseAnalyticsCustomKeys.EVENT_NEW_INSTALL, bundle);
    }

    public static void sendStartRegistrationEvent(Context context, String phoneNo){
        JoshApplication.getInstance(context).sendStartRegistrationAnalyticsEvent(phoneNo);
    }

    public void sendStartRegistrationAnalyticsEvent(String phoneNo){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARMATER_STARTREGISTRATION_MOBILENO, phoneNo);
        logAnalyticsEvent(FirebaseAnalyticsCustomKeys.EVENT_START_REGISTRATION, bundle);
    }

    public static void sendContinueRegistrationEvent(Context context, String phoneNo){
        JoshApplication.getInstance(context).sendContinueRegistrationAnalyticsEvent(phoneNo);
    }

    public void sendContinueRegistrationAnalyticsEvent(String phoneNo){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARMATER_CONTINUEREGISTRATION_MOBILENO, phoneNo);
        logAnalyticsEvent(FirebaseAnalyticsCustomKeys.EVENT_CONTINUE_REGISTRATION, bundle);
    }

    public static void sendFinishRegistrationEvent(Context context, String userID){
        JoshApplication.getInstance(context).sendFinishRegistrationAnalyticsEvent(userID);
    }

    public void sendFinishRegistrationAnalyticsEvent(String userID){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARAMETER_FINISHREGISTRATION_USERID, userID);
        logAnalyticsEvent(FirebaseAnalyticsCustomKeys.EVENT_FINISH_REGISTRATION, bundle);
    }

    public static void sendGameStartedEvent(Context context, String tableId, String betAmount){
        JoshApplication.getInstance(context).sendGameStartedAnalyticsEvent(tableId, betAmount);
    }

    public void sendGameStartedAnalyticsEvent(String tableId, String betAmount){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARAMETER_GAME_STARTED_TABLEID, tableId);
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARAMETER_GAME_STARTED_BETAMOUNT, betAmount);
        logAnalyticsEvent(FirebaseAnalyticsCustomKeys.EVENT_GAME_STARTED, bundle);
    }

    public static void sendFromInstallToLobbyAtFirstTimeEvent(Context context, String installKey, String userId){
        JoshApplication.getInstance(context).sendFromInstallToLobbyAtFirstTimeAnalyticsEvent(installKey, userId);
    }

    public void sendFromInstallToLobbyAtFirstTimeAnalyticsEvent(String installKey, String userId){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARMATER_INSTALLKEY, installKey);
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARMATER_USERID, userId);
        logAnalyticsEvent(FirebaseAnalyticsCustomKeys.EVENT_FROM_INSTALL_TO_LOBBY_AT_FIRSTTIME, bundle);
    }

    public static void sendFromInstallToGameAtFirstTimeEvent(Context context, String installKey, String userId){
        JoshApplication.getInstance(context).sendFromInstallToGameAtFirstTimeAnalyticsEvent(installKey, userId);
    }

    public void sendFromInstallToGameAtFirstTimeAnalyticsEvent(String installKey, String userId){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARMATER_INSTALLKEY, installKey);
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARMATER_USERID, userId);
        logAnalyticsEvent(FirebaseAnalyticsCustomKeys.EVENT_FROM_INSTALL_TO_WEBVIEWGAME_AT_FIRSTTIME, bundle);
    }

    public static void sendEditPhoneNoOnOtpScreenDuringRegisterEvent(Context context, String phoneno){
        JoshApplication.getInstance(context).sendEditPhoneNoOnOtpScreenDuringRegisterAnalyticsEvent(phoneno);
    }

    public void sendEditPhoneNoOnOtpScreenDuringRegisterAnalyticsEvent(String phoneno){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalyticsCustomKeys.EVENT_PARAMETER_EDITPHONENO_ONOTPSCREEN_REGISTER_MOBILENO, phoneno);
        logAnalyticsEvent(FirebaseAnalyticsCustomKeys.EVENT_EDITPHONENO_ONOTPSCREEN_REGISTER, bundle);
    }

    public static void setAnalyticsUserPropertyIdentifers(Context context){
        JoshApplication.getInstance(context).setAnalyticsIdentifiers();
    }

    private void setAnalyticsIdentifiers(){
        if(JoshApplication.isLoggedIn(mContext)){
            FirebaseAnalytics.getInstance(mContext).setUserId(""+ JoshApplication.userId(mContext));
            FirebaseAnalytics.getInstance(mContext).setUserProperty(FirebaseAnalyticsCustomKeys.EVENT_PARAMETER_USERANALYTICSIDENTIFER,
                    ""+ JoshApplication.userId(mContext));
            FirebaseAnalytics.getInstance(mContext).setUserProperty(FirebaseAnalyticsCustomKeys.USER_PROPERTY_USERNAME, JoshApplication.username(mContext));
            FirebaseAnalytics.getInstance(mContext).setUserProperty(FirebaseAnalyticsCustomKeys.USER_PROPERTY_IDTYPE,
                    FirebaseAnalyticsCustomKeys.USER_PROPERTY_IDTYPE_VALUE_USERID);
        }
        else{
            FirebaseAnalytics.getInstance(mContext).setUserId(JoshApplication.installKey(mContext));
            FirebaseAnalytics.getInstance(mContext).setUserProperty(FirebaseAnalyticsCustomKeys.EVENT_PARAMETER_USERANALYTICSIDENTIFER,
                    JoshApplication.installKey(mContext));
            FirebaseAnalytics.getInstance(mContext).setUserProperty(FirebaseAnalyticsCustomKeys.USER_PROPERTY_IDTYPE,
                    FirebaseAnalyticsCustomKeys.USER_PROPERTY_IDTYPE_VALUE_INSTALLKEY);
        }
    }

    public static void setCrashlyticsUserPropertyIdentifiers(Context context){
        JoshApplication.getInstance(context).setCrashlyticsIdentifiers();
    }

    private void setCrashlyticsIdentifiers(){
        if(JoshApplication.isLoggedIn(mContext)){
            //Set server login userid as Crashlytics user id
            FirebaseCrashlytics.getInstance().setUserId(""+ JoshApplication.userId(mContext));
            FirebaseCrashlytics.getInstance().setCustomKey("Username", JoshApplication.username(mContext));
            FirebaseCrashlytics.getInstance().setCustomKey("ID Type", "User ID");
        }
        else{
            //Set server installkey as Crashlytics user id
            FirebaseCrashlytics.getInstance().setUserId(JoshApplication.installKey(mContext));
            FirebaseCrashlytics.getInstance().setCustomKey("ID Type", "Install Key");
        }
    }

    private void logAnalyticsEvent(String eventName, Bundle eventParameters){
        if(JoshApplication.isLoggedIn(mContext)){
            eventParameters.putString(FirebaseAnalyticsCustomKeys.EVENT_PARAMETER_USERANALYTICSIDENTIFER, ""+ JoshApplication.userId(mContext));
            eventParameters.putString(FirebaseAnalyticsCustomKeys.EVENT_PARAMETER_USERANALYTICSNAME, JoshApplication.username(mContext));
            eventParameters.putString(FirebaseAnalyticsCustomKeys.EVENT_PARAMETER_USERANALYTICSIDENTIFERTYPE, "User ID");
        }
        else{
            eventParameters.putString(FirebaseAnalyticsCustomKeys.EVENT_PARAMETER_USERANALYTICSIDENTIFER, JoshApplication.installKey(mContext));
            eventParameters.putString(FirebaseAnalyticsCustomKeys.EVENT_PARAMETER_USERANALYTICSIDENTIFERTYPE, "Install Key");
        }
        FirebaseAnalytics.getInstance(mContext).logEvent(eventName, eventParameters);
    }

    public static String getCurrentGameType(){
        return currentGameType;
    }

    public static void setCurrentGameType(String gameType){
        currentGameType = gameType;
    }

    public static String hideVowelsInWord(String word){
        char[] charArray = word.toCharArray();
        String replacedWord = ""+charArray[0];//First letter not to be replaced
        for (int x =1; x<charArray.length; x++)
        {
            char c = charArray[x];
            if (c=='a' || c=='e' || c=='i' || c=='o' || c=='u' || c=='A' || c=='E' || c=='I' || c=='O' || c=='U') {
                replacedWord = replacedWord + '*';
            }
            else
                replacedWord = replacedWord + c;
        }
        return replacedWord;
    }

    public static int getExifRotation(String imgPath) {
        try {
            ExifInterface exif = new ExifInterface(imgPath);
            String rotationAmount = exif
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(rotationAmount)) {
                int rotationParam = Integer.parseInt(rotationAmount);
                switch (rotationParam) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        return 0;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                    default:
                        return 0;
                }
            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public static Bitmap fixOrientation(String filePath, Bitmap bm) {
        int orientation = getExifRotation(filePath);
        if (orientation == 0 || orientation % 360 == 0) {
            //it is already right orientation, no need to rotate
            return bm;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                matrix, true);
    }

    public static File persistImage(Context context, Bitmap bitmap, String name) {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("JOSH APP", "Error writing bitmap", e);
        }
        return imageFile;
    }

    public static String getOrdinalOfNumber(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }

    public static String getNumberInWordsRupee(long i) {
        if(i>=100000 && i<10000000){
            double lakhs = (double) i/(double) 100000;
            if(lakhs>1){
                return (lakhs+ " Lakhs").replace(".0","");
            }
            else{
                return (lakhs + " Lakh").replace(".0","");
            }
        }
        else if(i>=10000000){
            double crores = (double)i/(double)10000000;
            if(crores>1){
                return (crores + " Crores").replace(".0","");
            }
            else{
                return (crores + " Crore").replace(".0","");
            }
        }
        return String.valueOf(i);
    }

    public static String calculateTime(long remainingmilliseconds) {
        long seconds = remainingmilliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        String countdownString = "";
        if(days>0){
            countdownString +=  days + " d ";
        }
        if(hours>0){
            countdownString +=  (hours % 24) + " h ";
        }
        if(minutes>0){
            countdownString +=  (minutes % 60) + " m " ;
        }
        if(seconds>0){
            countdownString +=  (seconds % 60) +" s";
        }
        return countdownString;
    }
}
