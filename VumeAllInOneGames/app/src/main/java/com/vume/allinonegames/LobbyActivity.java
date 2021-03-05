package com.vume.allinonegames;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.vume.allinonegames.Adapters.OpenGamesCustomAdapter;
import com.vume.allinonegames.Model.JoshBuyInCallResponseData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshFetchBalanceResponseData;
import com.vume.allinonegames.Model.JoshLobbyCardListResponseData;
import com.vume.allinonegames.Model.JoshLobbyCardListResponseFilteredData;
import com.vume.allinonegames.Model.JoshLobbyCardSubListData;
import com.vume.allinonegames.Model.JoshOpenGamesResponseData;
import com.vume.allinonegames.Model.JoshProfileDetailsResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;
import com.vume.allinonegames.Util.JoshSharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.vume.allinonegames.GameSelectPriceActivity.ARGS_BUYIN_AMOUNT;
import static com.vume.allinonegames.GameSelectPriceActivity.ARGS_BUYIN_TABLEID;
import static com.vume.allinonegames.GameSelectPriceActivity.ARGS_BUYIN_TABLE_WAGERID;

public class LobbyActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = LobbyActivity.class.getName();
    Context mActivityContext;

    private TextView menuProfile, menuAddmoney, menuWithdrawMoney, menuTransactionHistory, menuReferrals, menuContactUs, menuAboutus, menuLogOut;
    private DrawerLayout mDrawerLayout;
    private ImageView btnOneZeroOneRummy, btnTwoZeroOneRummy, btnPointsRummy, btnDealsRummy, btnTournaments, btnPlayWithFriends, btnOpenGames;

    private ImageView btnPlayWithFriendsCallbreak, btnTournamentsCallbreak, btnOpenGamesCallbreak;
    private ImageView btnCardFiveRsCallBreak, btnCardTenRsCallBreak, btnCardTwentyRsCallBreak, btnCardTwentyFiveRsCallBreak, btnCardFiftyRsCallBreak,
            btnCardHundredRsCallBreak, btnCardTwoHundredFiftyRsCallBreak, btnCardFiveHundredRsCallBreak, btnCardOneThousandRsCallBreak;
    private Button btnCancelBuyInCallBreak, btnPlayGameInCallBreak;
    private TextView txtGameNameValCallBreak, txtPlayersValCallBreak, txtBetAmountValCallBreak;
    private AlertDialog gpsPermissionAlertDialog;

    private static final int EXTERNAL_LOCATION_PERMISSION_CONSTANT_HERE_START = 798;
    private static final int GPS_ON_PERMISSION = 863;
    private static final int REQUEST_PERMISSION_SETTING = 1023;
    private static final String RUMMY_PERMISSION_STATUS = "joshrummypermissionstatus_callbreak";

    public static final String GAME_TYPE_ARGUMENT = "game_card_type";

    private SharedPreferences permissionStatus;
    private LocationManager locationManager;

    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;
    private boolean isGPS;

    boolean doubleBackToExitPressedOnce = false;
    private int lobbyCardApiFailCount = 0, locationFetchFailCount = 0, otherGameLobbyCardApiFailCount = 0;
    private String latitude, longitude;
    private FusedLocationProviderClient fusedLocationClient;

    private TextView txtCurrentBalance, txtNoOpenGames;
    private ImageView imgHmaburgerMenu, imgProfilePicture;
    private Button btnAddCash;
    private Group groupPlaywithFriends, groupJoinATable, grpOpengames, grpRummyContent, grpCallbreakContent, grpBuyInConfirm;
    private View btnOpenJoinATableLayout, btnCreatePrivateTableLayout;
    private ImageView btnBackFromJoinaTable, btnBack, btnBackFromOpenGames;
    private RecyclerView mOpenGamesRecyclerView;
    private OpenGamesCustomAdapter openGamesAdapter;

    private String callbreakTableIdForTheGame= "";
    private float callbreakBetAmountForTheGame = 0.0f;
    private int callbreakNumOfPlayersForTheGame = 0;

    List<JoshLobbyCardListResponseFilteredData> filteredLobbyCardList = new ArrayList<>();
    List<JoshLobbyCardListResponseFilteredData> filteredLobbyCallbreakCardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_lobby);

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        initUI();
        permissionStatus = getSharedPreferences(RUMMY_PERMISSION_STATUS,MODE_PRIVATE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //RESET THIS oncreate if app iclosed from background after gamewebview is opened
        JoshApplication.sharedPreferences(mActivityContext).
                saveToSharedPreferences(JoshSharedPreferences.SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME, false);

        loadProfilePicture();
        askLocationPermissionsStart();

        if(JoshApplication.isFirstTimeOpenMadeForLobby){
            JoshApplication.sendFromInstallToLobbyAtFirstTimeEvent(mActivityContext,
                    JoshApplication.installKey(mActivityContext), ""+JoshApplication.userId(mActivityContext));
            JoshApplication.isFirstTimeOpenMadeForLobby = false;
        }
    }

    private void initUI(){
        mDrawerLayout = findViewById(R.id.josh_drawer_layout);
        //mDrawerLayout.openDrawer(Gravity.LEFT); //TODO: remove this line

        mLocationRequest = LocationRequest.create();
        //mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(6000000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mSettingsClient = LocationServices.getSettingsClient(mActivityContext);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        txtCurrentBalance = findViewById(R.id.txt_price);
        txtNoOpenGames = findViewById(R.id.txt_noopengames);
        btnAddCash = findViewById(R.id.btn_addcash);

        menuProfile = findViewById(R.id.menu_profile);
        menuAddmoney = findViewById(R.id.menu_addcash);
        menuWithdrawMoney = findViewById(R.id.menu_withdrawcash);
        menuTransactionHistory = findViewById(R.id.menu_transactionhistory);
        menuReferrals = findViewById(R.id.menu_referralsandoffers);
        menuContactUs = findViewById(R.id.menu_contactus);
        menuAboutus = findViewById(R.id.menu_aboutus);
        menuLogOut = findViewById(R.id.menu_logoout);

        btnOneZeroOneRummy = findViewById(R.id.card_101rummy);
        btnTwoZeroOneRummy = findViewById(R.id.card_201rummy);
        btnPointsRummy = findViewById(R.id.card_pointsrummy);
        btnDealsRummy = findViewById(R.id.card_dealsrummy);
        btnTournaments = findViewById(R.id.card_tournaments);
        btnPlayWithFriends = findViewById(R.id.card_playwithfriends);
        btnOpenJoinATableLayout = findViewById(R.id.btn_openjoinatablelayout);
        btnCreatePrivateTableLayout = findViewById(R.id.btn_createprivatetable);
        btnBack = findViewById(R.id.btn_back);
        btnBackFromJoinaTable = findViewById(R.id.btn_backjoinatable);
        btnBackFromOpenGames = findViewById(R.id.btn_backopengames);
        btnOpenGames = findViewById(R.id.img_myopengames);

        btnTournamentsCallbreak = findViewById(R.id.card_tournaments_callbreak);
        btnPlayWithFriendsCallbreak = findViewById(R.id.card_playwithfriends_callbreak);
        btnOpenGamesCallbreak = findViewById(R.id.img_myopengames_callbreak);
        btnCardFiveRsCallBreak = findViewById(R.id.card_fiverupees);
        btnCardTenRsCallBreak = findViewById(R.id.card_tenrupees);
        btnCardTwentyRsCallBreak = findViewById(R.id.card_twentyrupees);
        btnCardTwentyFiveRsCallBreak = findViewById(R.id.card_twentyfiverupees);
        btnCardFiftyRsCallBreak = findViewById(R.id.card_fiftyrupees);
        btnCardHundredRsCallBreak = findViewById(R.id.card_hundredrupees);
        btnCardTwoHundredFiftyRsCallBreak = findViewById(R.id.card_twofiftyrupees);
        btnCardFiveHundredRsCallBreak = findViewById(R.id.card_fivehundredrupees);
        btnCardOneThousandRsCallBreak = findViewById(R.id.card_onethousandrupees);
        btnCancelBuyInCallBreak = findViewById(R.id.btn_cancelbuyin);
        btnPlayGameInCallBreak = findViewById(R.id.btn_playgame);
        txtGameNameValCallBreak = findViewById(R.id.txt_gamenameval);
        txtPlayersValCallBreak = findViewById(R.id.txt_playersval);
        txtBetAmountValCallBreak = findViewById(R.id.txt_betamountval);

        imgHmaburgerMenu = findViewById(R.id.hamburger_menu);
        imgProfilePicture = findViewById(R.id.img_profileavatar);

        mOpenGamesRecyclerView = findViewById(R.id.opengames_recyclerview);
        mOpenGamesRecyclerView.setHasFixedSize(true);
        mOpenGamesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        openGamesAdapter = new OpenGamesCustomAdapter(mActivityContext, new ArrayList<JoshOpenGamesResponseData>());
        mOpenGamesRecyclerView.setAdapter(openGamesAdapter);

        groupJoinATable = findViewById(R.id.layout_joinatable);
        groupPlaywithFriends = findViewById(R.id.layout_playwithfriendsoptions);
        grpOpengames = findViewById(R.id.layout_opengames);
        grpRummyContent = findViewById(R.id.layout_rummylobby_content);
        grpCallbreakContent = findViewById(R.id.layout_callbreaklobby_content);
        grpBuyInConfirm = findViewById(R.id.group_buyinconfirm);

        menuProfile.setOnClickListener(this);
        menuAddmoney.setOnClickListener(this);
        menuWithdrawMoney.setOnClickListener(this);
        menuTransactionHistory.setOnClickListener(this);
        menuReferrals.setOnClickListener(this);
        menuContactUs.setOnClickListener(this);
        menuAboutus.setOnClickListener(this);
        menuLogOut.setOnClickListener(this);
        btnOneZeroOneRummy.setOnClickListener(this);
        btnTwoZeroOneRummy.setOnClickListener(this);
        btnPointsRummy.setOnClickListener(this);
        btnDealsRummy.setOnClickListener(this);
        btnTournaments.setOnClickListener(this);
        btnPlayWithFriends.setOnClickListener(this);
        imgHmaburgerMenu.setOnClickListener(this);
        btnAddCash.setOnClickListener(this);
        btnOpenJoinATableLayout.setOnClickListener(this);
        btnCreatePrivateTableLayout.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnBackFromJoinaTable.setOnClickListener(this);
        btnBackFromOpenGames.setOnClickListener(this);
        btnOpenGames.setOnClickListener(this);

        btnTournamentsCallbreak.setOnClickListener(this);
        btnPlayWithFriendsCallbreak.setOnClickListener(this);
        btnOpenGamesCallbreak.setOnClickListener(this);
        btnCardFiveRsCallBreak.setOnClickListener(this);
        btnCardTenRsCallBreak.setOnClickListener(this);
        btnCardTwentyRsCallBreak.setOnClickListener(this);
        btnCardTwentyFiveRsCallBreak.setOnClickListener(this);
        btnCardFiftyRsCallBreak.setOnClickListener(this);
        btnCardHundredRsCallBreak.setOnClickListener(this);
        btnCardTwoHundredFiftyRsCallBreak.setOnClickListener(this);
        btnCardFiveHundredRsCallBreak.setOnClickListener(this);
        btnCardOneThousandRsCallBreak.setOnClickListener(this);
        btnCancelBuyInCallBreak.setOnClickListener(this);
        btnPlayGameInCallBreak.setOnClickListener(this);

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

        updateGameContent(JoshApplication.getCurrentGameType());
    }

    private void updateGameContent(String gameType){
        switch (gameType){
            case JoshApplication.RUMMY_GAME_TYPE:
                    grpRummyContent.setVisibility(View.VISIBLE);
                    grpCallbreakContent.setVisibility(View.GONE);
                break;

            case JoshApplication.CALLBREAK_GAME_TYPE:
                    grpRummyContent.setVisibility(View.GONE);
                    grpCallbreakContent.setVisibility(View.VISIBLE);
                break;
        }
//        if(otherGameLobbyCardApiFailCount == 0) {
//            //If lobby card call for other game fails even once, will not be showing the second game
//            //TODO: SHOW THE SWITCH BUTTON
//        }
    }

    //TODO When both the games are enabled
    private void switchGameContent(String gameType){
            JoshApplication.setCurrentGameType(gameType);
            updateGameContent(JoshApplication.getCurrentGameType());
    }

    private void loadProfilePicture(){
        //JoshApplication.startSpinnerProgress(mActivityContext, getResources().getString(R.string.laoding_lobby));
        //LOAD PROFILE PIC FROM SERVER
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getProfileDatasApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext)).enqueue(new Callback<JoshProfileDetailsResponseData>() {
                @Override
                public void onResponse(Call<JoshProfileDetailsResponseData> call, Response<JoshProfileDetailsResponseData> response) {
                    if (response.isSuccessful()) {
                        final JoshProfileDetailsResponseData mJoshProfileDetailsResponseData = response.body();
                        if(mJoshProfileDetailsResponseData.getmProfilePicUrl()!=null &&
                                (!mJoshProfileDetailsResponseData.getmProfilePicUrl().equalsIgnoreCase(""))) {
                            Glide.with(mActivityContext).load(mJoshProfileDetailsResponseData.getmProfilePicUrl())
                                    .placeholder(R.drawable.icon_mushprofile)
                                    .addListener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            Log.d(TAG, "fail- " + e.getMessage());
                                            imgProfilePicture.setImageResource(JoshApplication.DEFAULT_PROFILE_PIC);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            Log.d(TAG, "reosurce ready");
                                            //enableUI();
                                            return false;
                                        }
                                    })
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(imgProfilePicture);
                        }
                        else{
                            imgProfilePicture.setImageResource(JoshApplication.DEFAULT_PROFILE_PIC);
                        }
                         //JoshApplication.closeSpinnerProgress(mActivityContext);
                    }
                    else{
                        //JoshApplication.closeSpinnerProgress(mActivityContext);
                        Log.d(TAG, "getProfileDatasApiCall UnSuccessfull - "+response.errorBody());
                    }
                }
                @Override
                public void onFailure(Call<JoshProfileDetailsResponseData> call, Throwable t) {
                    //JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getProfileDatasApiCall UnSuccessfull - " + t.getLocalizedMessage());
                }
            });
        }
    }

    public void askLocationPermissionsStart(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.ask_location_permission_title));
                    builder.setMessage(getResources().getString(R.string.ask_location_permission_message));
                    builder.setPositiveButton(getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(LobbyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, EXTERNAL_LOCATION_PERMISSION_CONSTANT_HERE_START);
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            closeWithPermissionRequiredMsg();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                } else if (permissionStatus.getBoolean(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.ask_location_permission_title));
                    builder.setMessage(getResources().getString(R.string.ask_location_permission_message));
                    builder.setPositiveButton(getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            JoshApplication.sharedPreferences(mActivityContext).
                                    saveToSharedPreferences(JoshSharedPreferences.ISGONETOSETTINGSFORPERMISSION, true);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            JoshApplication.customisedtoast(mActivityContext, getResources().getString(R.string.got_to_permissions_to_grant_location), Toast.LENGTH_LONG);
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            closeWithPermissionRequiredMsg();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, EXTERNAL_LOCATION_PERMISSION_CONSTANT_HERE_START);
                }

                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(Manifest.permission.ACCESS_FINE_LOCATION, true);
                editor.commit();


            } else {
                //You already have the permission, just go ahead.
                showLobbyCards();
            }
        }
        else{
            //Not needed for asking permissions below MarshMallow
            showLobbyCards();
        }
    }

    private void closeWithPermissionRequiredMsg(){
        finish();
        JoshApplication.customisedtoast(mActivityContext, "Permissions required to proceed!");
    }

    private void showLobbyCards(){
        //TODO get Location lats and Longs
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
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

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.ask_gps_permission)).setCancelable(false).setPositiveButton(getResources().getString(R.string.ok_heading), new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    turnGPSOn(new onGpsListener() {
                        @Override
                        public void gpsStatus(boolean isGPSEnable) {
                            // turn on GPS
                            if(isGPSEnable)
                                gpsPermissionAlertDialog.dismiss();
                            isGPS = isGPSEnable;
                            askLocationPermissionsStart();
                            Log.d("asisi", "gpsStatus onGpsListener = "+isGPS);
                        }
                    });
                }
                else{//GPS ENABLED> PROCEED TO GET LOCATIOn
                    getLocation();
                }
            }
        })
        .setNegativeButton("Close App", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        gpsPermissionAlertDialog = builder.create();
        gpsPermissionAlertDialog.show();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, EXTERNAL_LOCATION_PERMISSION_CONSTANT_HERE_START);
        } else {
            JoshApplication.startSpinnerProgress(mActivityContext, getResources().getString(R.string.laoding_lobby));
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        latitude = "";
                        longitude = "";
                        Log.d(TAG, "Failed Location - " + latitude + "--" + longitude);
                    }else {
                        for (Location location : locationResult.getLocations()) {
                            if (location != null) {
                                double lat = location.getLatitude();
                                double longi = location.getLongitude();
                                latitude = String.valueOf(lat);
                                longitude = String.valueOf(longi);
                                Log.d(TAG, "Success Location - " + latitude + "--" + longitude);
                                //JoshApplication.toast(mActivityContext, "Location came - "+location.getLatitude()+"--"+location.getLongitude());
                            }
                        }
                    }

                    getLobbyCards();
                }
            };
            LocationServices.getFusedLocationProviderClient(mActivityContext).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void getLobbyCards(){
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getLobbyCardListApiCall(getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), latitude, longitude, JoshApplication.RUMMY_GAME_TYPE).enqueue(new Callback<List<JoshLobbyCardListResponseData>>() {
                @Override
                public void onResponse(Call<List<JoshLobbyCardListResponseData>> call, Response<List<JoshLobbyCardListResponseData>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            JoshApplication.setJoshLobbyCardsList(response.body());
                            List<JoshLobbyCardListResponseFilteredData> filteredLobbyCardsData = getFilteredLobbyCards(JoshApplication.joshLobbyCardsList());
                            JoshApplication.setJoshFilteredLobbyCardsList(filteredLobbyCardsData);
                            lobbyCardApiFailCount = 0;//Fail Count reset
                        } else {
                            JoshApplication.customisedtoast(mActivityContext, "success but null body");
                        }
                        retrieveOtherGameTables();
                        //retrieveCurrentBalanceAndBonus();

                    } else {
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg, errorCode;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            errorCode = errorResponseObj.getmErrorCode();
                            Log.d(TAG, "getLobbyCardListApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.getErrorDialog().setCancelable(false);
                        JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                                errorMsg);

                        //RETRY 3 TIMES
                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
                        JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (lobbyCardApiFailCount < 3) {
                                    JoshApplication.closeErrorDialog(mActivityContext);
                                    lobbyCardApiFailCount++;
                                    showLobbyCards();
                                    JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                            + lobbyCardApiFailCount);
                                } else {
                                    JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                                    JoshApplication.closeErrorDialog(mActivityContext);
                                    finish();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<JoshLobbyCardListResponseData>> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getLobbyCardListApiCall UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                            getResources().getString(R.string.server_not_responding));
                    JoshApplication.getErrorDialog().setCancelable(false);
                    JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);

                    //RETRY 3 TIMES
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lobbyCardApiFailCount < 3) {
                                JoshApplication.closeErrorDialog(mActivityContext);
                                lobbyCardApiFailCount++;
                                showLobbyCards();
                                JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                        + lobbyCardApiFailCount);
                            } else {
                                JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                                JoshApplication.closeErrorDialog(mActivityContext);
                                finish();
                            }
                        }
                    });
                }
            });
        }
        else{//NO INTERNET CONDITION
            JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                    getResources().getString(R.string.no_internet_err));
            JoshApplication.getErrorDialog().setCancelable(false);
            JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);
            //RETRY 3 TIMES
            JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
            JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lobbyCardApiFailCount < 3) {
                        JoshApplication.closeErrorDialog(mActivityContext);
                        lobbyCardApiFailCount++;
                        showLobbyCards();
                        JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                + lobbyCardApiFailCount);
                    } else {
                        JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                        JoshApplication.closeErrorDialog(mActivityContext);
                        finish();
                    }
                }
            });
        }
    }

    public void retrieveOtherGameTables(){
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getLobbyCardListApiCall(getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), latitude, longitude, JoshApplication.CALLBREAK_GAME_TYPE).enqueue(new Callback<List<JoshLobbyCardListResponseData>>() {
                @Override
                public void onResponse(Call<List<JoshLobbyCardListResponseData>> call, Response<List<JoshLobbyCardListResponseData>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            JoshApplication.setJoshLobbyCallbreakCardsList(response.body());
                            List<JoshLobbyCardListResponseFilteredData> filteredLobbyCallbreakCardsData = getFilteredLobbyCallbreakCards(JoshApplication.joshLobbyCallbreakCardsList());
                            JoshApplication.setJoshFilteredLobbyCallbreakCardsList(filteredLobbyCallbreakCardsData);
                            otherGameLobbyCardApiFailCount = 0;//Fail Count reset
                        } else {
                            JoshApplication.customisedtoast(mActivityContext, "success but null body");
                        }
                        retrieveCurrentBalanceAndBonus();

                    } else {
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg, errorCode;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            errorCode = errorResponseObj.getmErrorCode();
                            Log.d(TAG, "getLobbyCardListApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                                errorMsg);
                        JoshApplication.getErrorDialog().setCancelable(false);
                        JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);

                        //RETRY 3 TIMES
                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
                        JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (otherGameLobbyCardApiFailCount < 3) {
                                    JoshApplication.closeErrorDialog(mActivityContext);
                                    otherGameLobbyCardApiFailCount++;
                                    showLobbyCards();
                                    JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                            + otherGameLobbyCardApiFailCount);
                                } else {
                                    JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                                    JoshApplication.closeErrorDialog(mActivityContext);
                                    finish();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<JoshLobbyCardListResponseData>> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getLobbyCardListApiCall UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                            getResources().getString(R.string.server_not_responding));
                    JoshApplication.getErrorDialog().setCancelable(false);
                    JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);

                    //RETRY 3 TIMES
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (otherGameLobbyCardApiFailCount < 3) {
                                JoshApplication.closeErrorDialog(mActivityContext);
                                otherGameLobbyCardApiFailCount++;
                                showLobbyCards();
                                JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                        + otherGameLobbyCardApiFailCount);
                            } else {
                                JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                                JoshApplication.closeErrorDialog(mActivityContext);
                                finish();
                            }
                        }
                    });
                }
            });
        }
        else{//NO INTERNET CONDITION
            JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                    getResources().getString(R.string.no_internet_err));
            JoshApplication.getErrorDialog().setCancelable(false);
            JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);

            //RETRY 3 TIMES
            JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
            JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (otherGameLobbyCardApiFailCount < 3) {
                        JoshApplication.closeErrorDialog(mActivityContext);
                        otherGameLobbyCardApiFailCount++;
                        showLobbyCards();
                        JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                + otherGameLobbyCardApiFailCount);
                    } else {
                        JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                        JoshApplication.closeErrorDialog(mActivityContext);
                        finish();
                    }
                }
            });
        }
    }

    public void retrieveCurrentBalanceAndBonus(){
        if(JoshApplication.progressDialog != null && JoshApplication.progressDialog.isShowing()) {
            JoshApplication.changeSpinnerProgressDialogMessage(getResources().getString(R.string.fetching_lobby_balance));
        }else if(!isFinishing()){
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
                        JoshApplication.getErrorDialog().setCancelable(false);
                        JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);

                        //RETRY 3 TIMES
                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
                        JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (lobbyCardApiFailCount < 3) {
                                    JoshApplication.closeErrorDialog(mActivityContext);
                                    lobbyCardApiFailCount++;
                                    retrieveCurrentBalanceAndBonus();
                                    JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                            + lobbyCardApiFailCount);
                                } else {
                                    JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                                    JoshApplication.closeErrorDialog(mActivityContext);
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
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                            getResources().getString(R.string.server_not_responding));
                    JoshApplication.getErrorDialog().setCancelable(false);
                    JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);

                    //RETRY 3 TIMES
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lobbyCardApiFailCount < 3) {
                                JoshApplication.closeErrorDialog(mActivityContext);
                                lobbyCardApiFailCount++;
                                retrieveCurrentBalanceAndBonus();
                                JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                        + lobbyCardApiFailCount);
                            } else {
                                JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                                JoshApplication.closeErrorDialog(mActivityContext);
                                finish();
                            }
                        }
                    });
                }
            });
        }
        else{//NO INTERNET CONDITION
            JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                    getResources().getString(R.string.no_internet_err));
            JoshApplication.getErrorDialog().setCancelable(false);
            JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);

            //RETRY 3 TIMES
            JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
            JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lobbyCardApiFailCount < 3) {
                        JoshApplication.closeErrorDialog(mActivityContext);
                        lobbyCardApiFailCount++;
                        retrieveCurrentBalanceAndBonus();
                        JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                + lobbyCardApiFailCount);
                    } else {
                        JoshApplication.toast(mActivityContext, getResources().getString(R.string.max_retries_reached));
                        JoshApplication.closeErrorDialog(mActivityContext);
                        finish();
                    }
                }
            });
        }
    }

    private List<JoshLobbyCardListResponseFilteredData> getFilteredLobbyCallbreakCards(List<JoshLobbyCardListResponseData> lobbyCardList){
        for(JoshLobbyCardListResponseData lobbyCardData : lobbyCardList){
            final String catg = lobbyCardData.getmCategory();
            final String tableType = lobbyCardData.getmTableType();

            HashMap<Integer, ArrayList<JoshLobbyCardSubListData>> showOptionsTAttributesMap = new HashMap<>();
            //Update the HashMap in the correct model
            boolean isLobbyDataAlreadyAvailableInFiltereddata = false;
            int currentLobbyDataIndex = -1;
            for(int x = 0; x<filteredLobbyCallbreakCardList.size();x++){
                if(filteredLobbyCallbreakCardList.get(x).getmTableCategory().equalsIgnoreCase(catg)){
                    currentLobbyDataIndex = x;
                    isLobbyDataAlreadyAvailableInFiltereddata = true;
                    showOptionsTAttributesMap = filteredLobbyCallbreakCardList.get(x).getmShowOptionToAttributesMap();
                }
            }

            if(catg.equalsIgnoreCase("bestof")){//For Callbreak, deals game is used and should be filtered oin the basis of cost
                if(!showOptionsTAttributesMap.containsKey(Math.round(lobbyCardData.getmBetValue()))){
                    ArrayList<JoshLobbyCardSubListData> optionData = new ArrayList<>();
                    optionData.add(new JoshLobbyCardSubListData(lobbyCardData.getmTableId(), lobbyCardData.getmTableName(),
                            lobbyCardData.getmTableLimit(), lobbyCardData.getmNumOfPlayers(), lobbyCardData.getmBetValue(),
                            lobbyCardData.getmMinBuyIn(), lobbyCardData.getmMaxBuyIn()));
                    showOptionsTAttributesMap.put(Math.round(lobbyCardData.getmBetValue()), optionData);
                }
                else {//Already have the list for current key with some betVALUE
                    ArrayList<JoshLobbyCardSubListData> optionData = showOptionsTAttributesMap.get(Math.round(lobbyCardData.getmBetValue()));
                    optionData.add(new JoshLobbyCardSubListData(lobbyCardData.getmTableId(), lobbyCardData.getmTableName(),
                            lobbyCardData.getmTableLimit(), lobbyCardData.getmNumOfPlayers(), lobbyCardData.getmBetValue(),
                            lobbyCardData.getmMinBuyIn(), lobbyCardData.getmMaxBuyIn()));
                    showOptionsTAttributesMap.put(Math.round(lobbyCardData.getmBetValue()), optionData);
                }
            }

            if(!isLobbyDataAlreadyAvailableInFiltereddata){
                filteredLobbyCallbreakCardList.add(new JoshLobbyCardListResponseFilteredData(catg, tableType, showOptionsTAttributesMap));
            }
            else{
                filteredLobbyCallbreakCardList.set(currentLobbyDataIndex, new JoshLobbyCardListResponseFilteredData(catg, tableType, showOptionsTAttributesMap));
            }
        }

        return filteredLobbyCallbreakCardList;
    }

    private List<JoshLobbyCardListResponseFilteredData> getFilteredLobbyCards(List<JoshLobbyCardListResponseData> lobbyCardList){
        for(JoshLobbyCardListResponseData lobbyCardData : lobbyCardList){
            final String catg = lobbyCardData.getmCategory();
            final String tableType = lobbyCardData.getmTableType();

            HashMap<Integer, ArrayList<JoshLobbyCardSubListData>> showOptionsTAttributesMap = new HashMap<>();
            //Update the HashMap in the correct model
            boolean isLobbyDataAlreadyAvailableInFiltereddata = false;
            int currentLobbyDataIndex = -1;
            for(int x = 0; x<filteredLobbyCardList.size();x++){
                if(filteredLobbyCardList.get(x).getmTableCategory().equalsIgnoreCase(catg)){
                    currentLobbyDataIndex = x;
                    isLobbyDataAlreadyAvailableInFiltereddata = true;
                    showOptionsTAttributesMap = filteredLobbyCardList.get(x).getmShowOptionToAttributesMap();
                }
            }

            if(catg.equalsIgnoreCase("bestof")){//For best of category, tableLImit will be the option in UI
                if(!showOptionsTAttributesMap.containsKey(lobbyCardData.getmTableLimit())){
                    ArrayList<JoshLobbyCardSubListData> optionData = new ArrayList<>();
                    optionData.add(new JoshLobbyCardSubListData(lobbyCardData.getmTableId(), lobbyCardData.getmTableName(),
                            lobbyCardData.getmTableLimit(), lobbyCardData.getmNumOfPlayers(), lobbyCardData.getmBetValue(),
                            lobbyCardData.getmMinBuyIn(), lobbyCardData.getmMaxBuyIn()));
                    showOptionsTAttributesMap.put(lobbyCardData.getmTableLimit(), optionData);
                }
                else {//Already have the list for current key with some betVALUE
                    ArrayList<JoshLobbyCardSubListData> optionData = showOptionsTAttributesMap.get(lobbyCardData.getmTableLimit());
                    optionData.add(new JoshLobbyCardSubListData(lobbyCardData.getmTableId(), lobbyCardData.getmTableName(),
                            lobbyCardData.getmTableLimit(), lobbyCardData.getmNumOfPlayers(), lobbyCardData.getmBetValue(),
                            lobbyCardData.getmMinBuyIn(), lobbyCardData.getmMaxBuyIn()));
                    showOptionsTAttributesMap.put(lobbyCardData.getmTableLimit(), optionData);
                }
            }
            else if(catg.equalsIgnoreCase("201-pool") ||
                    catg.equalsIgnoreCase("101-pool") ||
                    catg.equalsIgnoreCase("points")){ //For all other type of cards, numOfPlayers will be the option in UI
                if(!showOptionsTAttributesMap.containsKey(lobbyCardData.getmNumOfPlayers())){
                    ArrayList<JoshLobbyCardSubListData> optionData = new ArrayList<>();
                    optionData.add( new JoshLobbyCardSubListData(lobbyCardData.getmTableId(), lobbyCardData.getmTableName(),
                            lobbyCardData.getmTableLimit(), lobbyCardData.getmNumOfPlayers(), lobbyCardData.getmBetValue(),
                            lobbyCardData.getmMinBuyIn(), lobbyCardData.getmMaxBuyIn()));
                    showOptionsTAttributesMap.put(lobbyCardData.getmNumOfPlayers(), optionData);
                }
                else {//Already have the list for current key with some betVALUE, Add different betValues for the option
                    ArrayList<JoshLobbyCardSubListData> optionData = showOptionsTAttributesMap.get(lobbyCardData.getmNumOfPlayers());
                    optionData.add(new JoshLobbyCardSubListData(lobbyCardData.getmTableId(), lobbyCardData.getmTableName(),
                            lobbyCardData.getmTableLimit(), lobbyCardData.getmNumOfPlayers(), lobbyCardData.getmBetValue(),
                            lobbyCardData.getmMinBuyIn(), lobbyCardData.getmMaxBuyIn()));
                    showOptionsTAttributesMap.put(lobbyCardData.getmNumOfPlayers(), optionData);
                }
            }

            if(!isLobbyDataAlreadyAvailableInFiltereddata){
                filteredLobbyCardList.add(new JoshLobbyCardListResponseFilteredData(catg, tableType, showOptionsTAttributesMap));
            }
            else{
                filteredLobbyCardList.set(currentLobbyDataIndex, new JoshLobbyCardListResponseFilteredData(catg, tableType, showOptionsTAttributesMap));
            }
        }

        //ADDING COMING SOON TOURNAMENTS OPTION
        filteredLobbyCardList.add(new JoshLobbyCardListResponseFilteredData("tournaments", null, null));
        return filteredLobbyCardList;
    }

    private void openMyGames(){
        if(JoshApplication.progressDialog != null && JoshApplication.progressDialog.isShowing()) {
            JoshApplication.changeSpinnerProgressDialogMessage(getResources().getString(R.string.fetching_open_games));
        }else if(!isFinishing()){
            JoshApplication.startSpinnerProgress(mActivityContext, getResources().getString(R.string.fetching_open_games));
        }

        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getOpenGameApiCall(getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext)).enqueue(new Callback<List<JoshOpenGamesResponseData>>() {
                @Override
                public void onResponse(Call<List<JoshOpenGamesResponseData>> call, Response<List<JoshOpenGamesResponseData>> response) {
                    if (response.isSuccessful()) {
                        List<JoshOpenGamesResponseData> mOpenGamesList = response.body();
                        grpOpengames.setVisibility(View.VISIBLE);
                        if(mOpenGamesList!=null && mOpenGamesList.size()>0){
                            openGamesAdapter.setList(mOpenGamesList);
                            openGamesAdapter.notifyDataSetChanged();
                            txtNoOpenGames.setVisibility(View.GONE);
                            mOpenGamesRecyclerView.setVisibility(View.VISIBLE);
                        }
                        else{
                            txtNoOpenGames.setVisibility(View.VISIBLE);
                            mOpenGamesRecyclerView.setVisibility(View.INVISIBLE);
                        }
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                    }
                    else{
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg, errorCode;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            errorCode = errorResponseObj.getmErrorCode();
                            Log.d(TAG, "getOpenGameApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                                errorMsg);

                    }
                }
                @Override
                public void onFailure(Call<List<JoshOpenGamesResponseData>> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getOpenGameApiCall UnSuccessfull - " + t.getLocalizedMessage());
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
            //RETRY 3 TIMES
            JoshApplication.getErrorDialogOkButton(mActivityContext).setText(getResources().getString(R.string.retry_button_label));
            JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lobbyCardApiFailCount < 3) {
                        JoshApplication.closeErrorDialog(mActivityContext);
                        lobbyCardApiFailCount++;
                        retrieveCurrentBalanceAndBonus();
                        JoshApplication.toast(mActivityContext, getResources().getString(R.string.retry_no)
                                + lobbyCardApiFailCount);
                    } else {
                        JoshApplication.closeErrorDialog(mActivityContext);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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

            case R.id.card_101rummy:
                //JoshApplication.sharedPreferences(mActivityContext).saveToSharedPreferences(JoshSharedPreferences.SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME, true);
                Intent intentSelectPriceForonezeroone = new Intent(LobbyActivity.this, GameSelectPriceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(GAME_TYPE_ARGUMENT, JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_101POOL);
                intentSelectPriceForonezeroone.putExtras(bundle);
                intentSelectPriceForonezeroone.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentSelectPriceForonezeroone);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.card_201rummy:
                //JoshApplication.sharedPreferences(mActivityContext).saveToSharedPreferences(JoshSharedPreferences.SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME, true);
                Intent intentSelectPriceFortwozeroone = new Intent(LobbyActivity.this, GameSelectPriceActivity.class);
                Bundle bundleFortwozeroone = new Bundle();
                bundleFortwozeroone.putString(GAME_TYPE_ARGUMENT, JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_201POOL);
                intentSelectPriceFortwozeroone.putExtras(bundleFortwozeroone);
                intentSelectPriceFortwozeroone.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentSelectPriceFortwozeroone);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.card_pointsrummy:
                //JoshApplication.sharedPreferences(mActivityContext).saveToSharedPreferences(JoshSharedPreferences.SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME, true);
                Intent intentSelectPriceForpointsrummy = new Intent(LobbyActivity.this, GameSelectPriceActivity.class);
                Bundle bundleForpointsrummy = new Bundle();
                bundleForpointsrummy.putString(GAME_TYPE_ARGUMENT, JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_POINTS);
                intentSelectPriceForpointsrummy.putExtras(bundleForpointsrummy);
                intentSelectPriceForpointsrummy.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentSelectPriceForpointsrummy);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.card_dealsrummy:
                //JoshApplication.sharedPreferences(mActivityContext).saveToSharedPreferences(JoshSharedPreferences.SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME, true);
                Intent intentSelectPriceFordealsrummy = new Intent(LobbyActivity.this, GameSelectPriceActivity.class);
                Bundle bundleFordealsrummy = new Bundle();
                bundleFordealsrummy.putString(GAME_TYPE_ARGUMENT, JoshLobbyCardListResponseFilteredData.TABLE_CATEGORY_BESTOF);
                intentSelectPriceFordealsrummy.putExtras(bundleFordealsrummy);
                intentSelectPriceFordealsrummy.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentSelectPriceFordealsrummy);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.card_tournaments:
            case R.id.card_tournaments_callbreak:
                JoshApplication.toast(mActivityContext, "Coming soon!");
                break;

            case R.id.card_playwithfriends:
            case R.id.card_playwithfriends_callbreak:
                groupPlaywithFriends.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_openjoinatablelayout:
                groupJoinATable.setVisibility(View.VISIBLE);
                break;

            case R.id.hamburger_menu:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;

            case R.id.btn_addcash:
                Intent addcashLayoputActivity = new Intent(mActivityContext, AddCashActivity.class);
                addcashLayoputActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(addcashLayoputActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.btn_createprivatetable:
                Intent openPrivateTableIntent = new Intent(mActivityContext, CreatePrivateTableActivity.class);
                openPrivateTableIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(openPrivateTableIntent);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.btn_back:
            case R.id.btn_backopengames:
            case R.id.btn_backjoinatable:
                isSublayoutOpen();
                break;

            case R.id.img_myopengames:
            case R.id.img_myopengames_callbreak:
                openMyGames();
                break;

            case R.id.card_fiverupees:
                    openBuyInConfirmDialogCallbreak(5);
                break;

            case R.id.card_tenrupees:
                    openBuyInConfirmDialogCallbreak(10);
                break;

            case R.id.card_twentyrupees:
                    openBuyInConfirmDialogCallbreak(20);
                break;

            case R.id.card_twentyfiverupees:
                    openBuyInConfirmDialogCallbreak(25);
                break;

            case R.id.card_fiftyrupees:
                    openBuyInConfirmDialogCallbreak(50);
                break;

            case R.id.card_hundredrupees:
                    openBuyInConfirmDialogCallbreak(100);
                break;

            case R.id.card_twofiftyrupees:
                    openBuyInConfirmDialogCallbreak(250);
                break;

            case R.id.card_fivehundredrupees:
                    openBuyInConfirmDialogCallbreak(500);
                break;

            case R.id.card_onethousandrupees:
                    openBuyInConfirmDialogCallbreak(1000);
                break;

            case R.id.btn_cancelbuyin:
                    hideBuyInConfirmDialog();
                break;

            case R.id.btn_playgame:
                callBuyInApi(callbreakTableIdForTheGame, callbreakBetAmountForTheGame);
                //Toast.makeText(mActivityContext, "Awaiting API's", Toast.LENGTH_SHORT).show();
                hideBuyInConfirmDialog();
                break;
        }
    }

    private void openBuyInConfirmDialogCallbreak(final int betRupees){
        if(JoshApplication.joshFilteredLobbyCallbreakCardsList()!=null && JoshApplication.joshFilteredLobbyCallbreakCardsList().size()>0) {
            float betRupeesInFloat = (float) betRupees;
            callbreakBetAmountForTheGame = betRupeesInFloat;
            JoshLobbyCardSubListData currentGameData = JoshApplication.joshFilteredLobbyCallbreakCardsList().get(0).
                    getmShowOptionToAttributesMap().get(betRupees).get(0);
            callbreakNumOfPlayersForTheGame = currentGameData.getmNumPlayers();
            callbreakTableIdForTheGame = currentGameData.getmTableId();

            if (hasSufficientBalanceToProceed(betRupeesInFloat)) {
                txtPlayersValCallBreak.setText(callbreakNumOfPlayersForTheGame+" "+getResources().getString(R.string.playerslabel));
                txtGameNameValCallBreak.setText(getResources().getString(R.string.gametype_callbreak));
                txtBetAmountValCallBreak.setText(getResources().getString(R.string.bet_amountvalue_label,
                        String.valueOf(betRupeesInFloat)));
                grpBuyInConfirm.setVisibility(View.VISIBLE);
            } else {
                showInsufficientBalanceDialog(betRupeesInFloat);
            }
        }
        else{
            Toast.makeText(mActivityContext, "Games not loaded!", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideBuyInConfirmDialog(){
        grpBuyInConfirm.setVisibility(View.GONE);
    }

    private boolean hasSufficientBalanceToProceed(float betRupeesInFloat){
        return betRupeesInFloat<=JoshApplication.currentbalance(mActivityContext);
    }

    private void showInsufficientBalanceDialog(float currentFinalBetAmount){
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == EXTERNAL_LOCATION_PERMISSION_CONSTANT_HERE_START){
            if(grantResults[0] == 0) {//Permission granted
                //Show the lobby game cards here
                showLobbyCards();
            }
            else{
                closeWithPermissionRequiredMsg();
            }
        }
        else if(requestCode == GPS_ON_PERMISSION){
            if(grantResults[0] == 0) {//Permission granted
            }
            else{

            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        if(JoshApplication.sharedPreferences(mActivityContext).
                getSharedPrefBooleanValue(JoshSharedPreferences.ISGONETOSETTINGSFORPERMISSION, false)){
            askLocationPermissionsStart();
            JoshApplication.sharedPreferences(mActivityContext).
                    saveToSharedPreferences(JoshSharedPreferences.ISGONETOSETTINGSFORPERMISSION, false);
        }
        else if(JoshApplication.sharedPreferences(mActivityContext).
                getSharedPrefBooleanValue(JoshSharedPreferences.SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME, false)) {
            retrieveCurrentBalanceAndBonus();
            JoshApplication.sharedPreferences(mActivityContext).
                    saveToSharedPreferences(JoshSharedPreferences.SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME, false);
        }
        if(JoshApplication.profilePicUrl(mActivityContext)!=null && !JoshApplication.profilePicUrl(mActivityContext).equalsIgnoreCase("")){
            Glide.with(mActivityContext).load(JoshApplication.profilePicUrl(mActivityContext))
                    .placeholder(R.drawable.icon_mushprofile)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d(TAG, "fail- " + e.getMessage());
                            imgProfilePicture.setImageResource(JoshApplication.DEFAULT_PROFILE_PIC);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "reosurce ready");
                            //enableUI();
                            return false;
                        }
                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imgProfilePicture);
        }
            txtCurrentBalance.setText(getResources().getString(R.string.rupee_sign)+" "+String.valueOf(JoshApplication.currentbalance(mActivityContext)));
            super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
    }

    // method for turn on GPS
    public void turnGPSOn(final onGpsListener onGpsListener) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (onGpsListener != null) {
                onGpsListener.gpsStatus(true);
            }
        } else {
            mSettingsClient
                    .checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener((Activity) mActivityContext, new OnSuccessListener<LocationSettingsResponse>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//  GPS is already enable, callback GPS status through listener
                            if (onGpsListener != null) {
                                onGpsListener.gpsStatus(true);
                            }
                        }
                    })
                    .addOnFailureListener((Activity) mActivityContext, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult((Activity) mActivityContext, GPS_ON_PERMISSION);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.i(TAG, "PendingIntent unable to execute request.");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    String errorMessage = "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings.";
                                    Log.e(TAG, errorMessage);
                                    Toast.makeText((Activity) mActivityContext, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
    public interface onGpsListener {
        void gpsStatus(boolean isGPSEnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_ON_PERMISSION) {
                isGPS = true; // flag maintain before get location
                askLocationPermissionsStart();
                Log.d("asisi", "onActivityResult GPS_ON_PERMISSION = "+isGPS);
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED){
            if (requestCode == GPS_ON_PERMISSION) {
                OnGPS();
                JoshApplication.customisedtoast(mActivityContext, getResources().getString(R.string.gps_needed), Toast.LENGTH_LONG);
                Log.d("asisi", "onActivityResult GPS_ON_PERMISSION Failed = "+isGPS);
            }
        }
    }

    private boolean isSublayoutOpen(){
        if(groupJoinATable.getVisibility() == View.VISIBLE){
            groupJoinATable.setVisibility(View.GONE);
            return true;
        }
        else if(groupPlaywithFriends.getVisibility() == View.VISIBLE){
            groupPlaywithFriends.setVisibility(View.GONE);
            return true;
        }
        else if(grpOpengames.getVisibility() == View.VISIBLE){
            grpOpengames.setVisibility(View.GONE);
            txtNoOpenGames.setVisibility(View.GONE);
            return true;
        }
        else if(grpBuyInConfirm.getVisibility() == View.VISIBLE){
            grpBuyInConfirm.setVisibility(View.GONE);
            return true;
        }
        return false;
    }
}