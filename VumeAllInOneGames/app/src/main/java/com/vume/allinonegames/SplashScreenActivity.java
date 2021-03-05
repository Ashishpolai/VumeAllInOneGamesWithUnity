package com.vume.allinonegames;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.vume.allinonegames.Model.FirebaseAnalyticsCustomKeys;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshFcmTokenUpdateResponseData;
import com.vume.allinonegames.Model.JoshInstallResponseData;
import com.vume.allinonegames.Model.JoshLoginResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIMEOUT = 20; //TODO: 100 is actual value. MADE 5 FOR BYPASSING TO LOBBY
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Context mActivityContext;
    private static String TAG = SplashScreenActivity.class.getName();
    private static final String CHECK_NEW_INSTALL = "check_new_install";
    private static final String GET_CURRENT_ACCESSTOKEN_AND_UPDATEIFCHANGED = "get_current_accesstoken_and_updateifchanged";
    private static final String REGISTER_SIGNIN_TO_APP = "register_sign_into_app";
    private static final String GO_TO_NEXTACTIVITY = "go_to_next_activity";
    private String postTaskKey = CHECK_NEW_INSTALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        long time1 = System.currentTimeMillis();
        setContentView(R.layout.activity_splash_screen);
        long time2 = System.currentTimeMillis();
        Log.i(TAG, "onCreate: Load time = " + (time2 - time1));

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        init();
        new DoTaskWithProgress().execute(20);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void init(){
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        JoshApplication.localeUtils(mActivityContext).initialize();
    }


    class DoTaskWithProgress extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            //Setting Crashlytics and Analytics identifiers and user properties
            JoshApplication.setCrashlyticsUserPropertyIdentifiers(mActivityContext);
            JoshApplication.setAnalyticsUserPropertyIdentifers(mActivityContext);

            for (; progressStatus <= params[0]; progressStatus++) {
                try {
                    Thread.sleep(SPLASH_SCREEN_TIMEOUT / 100);
                    publishProgress(progressStatus);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            doPostExecute();
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }

    private void doPostExecute(){
        switch (postTaskKey){
            case CHECK_NEW_INSTALL:
                if(JoshApplication.isInternetAvailable(mActivityContext)) {
                    if(JoshApplication.isNewInstall(mActivityContext)){//NEW INSTALL
                            JoshApplication.getRegisterInstallApiCall(getResources().getString(R.string.josh_server_api_key)).enqueue(new Callback<JoshInstallResponseData>() {
                                @Override
                                public void onResponse(Call<JoshInstallResponseData> call, Response<JoshInstallResponseData> response) {
                                    Log.d(TAG, "getRegisterInstallApiCall Success - " + response.body().toString());
                                    if (response.isSuccessful()) {
                                        final JoshInstallResponseData joshResponseData = response.body();
                                        JoshApplication.saveInstallKey(joshResponseData.getmInstallKey());
                                        JoshApplication.saveInstallId(joshResponseData.getmInstallId());
                                        JoshApplication.registerNewInstall();
                                        postTaskKey = REGISTER_SIGNIN_TO_APP;
                                        new DoTaskWithProgress().execute(60);//PROCEED TO NEXT STEP

                                        JoshApplication.sendNewInstallEvent(mActivityContext);
                                    } else {
                                        Log.d(TAG, "getRegisterInstallApiCall Unsuccessfull - " + response.errorBody());
                                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                                                getResources().getString(R.string.server_not_responding));
                                        JoshApplication.getErrorDialog().setCancelable(false);
                                        JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);
                                        JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                onErrorDialogOkClicked();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<JoshInstallResponseData> call, Throwable t) {
                                    Log.d(TAG, "getRegisterInstallApiCall Failure - " + t.getLocalizedMessage());
                                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding),
                                            getResources().getString(R.string.server_not_responding));
                                    JoshApplication.getErrorDialog().setCancelable(false);
                                    JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);
                                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            onErrorDialogOkClicked();
                                        }
                                    });
                                }
                            });
                    }
                    else{//NOT NEW INSTALL
                        postTaskKey = REGISTER_SIGNIN_TO_APP;
                        new DoTaskWithProgress().execute(50);
                    }
                }
                else{
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                            getResources().getString(R.string.no_internet_err));
                    JoshApplication.getErrorDialog().setCancelable(false);
                    JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onErrorDialogOkClicked();
                        }
                    });
                }
                break;

            case REGISTER_SIGNIN_TO_APP:
                if(JoshApplication.isInternetAvailable(mActivityContext)) {
                    if (JoshApplication.isLoggedIn(mActivityContext)) {//Auto Logged in, Register Sign IN
                        JoshApplication.getRegisterLoginApiCall(getResources().getString(R.string.josh_server_api_key),
                                JoshApplication.username(mActivityContext), JoshApplication.password(mActivityContext), JoshApplication.installKey(mActivityContext)).enqueue(new Callback<JoshLoginResponseData>() {
                            @Override
                            public void onResponse(Call<JoshLoginResponseData> call, Response<JoshLoginResponseData> response) {
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "getRegisterLoginApiCall Success - " + response.body().toString());
                                    final JoshLoginResponseData joshLoginResponseData = response.body();
                                    JoshApplication.saveCurrentLoginAuthTokenFromServer(joshLoginResponseData.getmLoginToken());
                                    JoshApplication.saveUsername(joshLoginResponseData.getmUsername());
                                    JoshApplication.saveUserId(joshLoginResponseData.getmUserId());

                                    postTaskKey = GET_CURRENT_ACCESSTOKEN_AND_UPDATEIFCHANGED;
                                    new DoTaskWithProgress().execute(70);

                                    JoshApplication.sendLoginEvent(mActivityContext, FirebaseAnalyticsCustomKeys.LOGIN_PARAMETER_METHOD_VALUE_AUTOSIGNIN);
                                } else {
                                    JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                                    String errorMsg, errorCode;
                                    if(errorResponseObj != null){
                                        errorMsg = errorResponseObj.getmMessage();
                                        errorCode = errorResponseObj.getmErrorCode();
                                        Log.d(TAG, "getRegisterLoginApiCall UnSuccessfull - " + response.errorBody());
                                    }
                                    else{
                                        errorMsg = getResources().getString(R.string.server_not_responding);
                                        errorCode = String.valueOf(response.code());
                                    }
                                    JoshApplication.sendLoginErrorEvent(mActivityContext, errorMsg,
                                            errorCode, FirebaseAnalyticsCustomKeys.LOGIN_PARAMETER_METHOD_VALUE_AUTOSIGNIN);
                                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                                            getResources().getString(R.string.server_not_responding));
                                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            JoshApplication.closeErrorDialog(mActivityContext);
                                            JoshApplication.signOut(mActivityContext);
                                            postTaskKey = GET_CURRENT_ACCESSTOKEN_AND_UPDATEIFCHANGED;
                                            new DoTaskWithProgress().execute(70);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<JoshLoginResponseData> call, Throwable t) {
                                Log.d(TAG, "getRegisterLoginApiCall UnSuccessfull - " + t.getLocalizedMessage());
                                JoshApplication.sendLoginErrorEvent(mActivityContext, t.getLocalizedMessage(),  "", FirebaseAnalyticsCustomKeys.LOGIN_PARAMETER_METHOD_VALUE_AUTOSIGNIN);
                                JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                                        getResources().getString(R.string.server_not_responding));
                                JoshApplication.getErrorDialog().setCancelable(false);
                                JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);
                                JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onErrorDialogOkClicked();
                                    }
                                });
                            }
                        });
                        //TODO: BYPASSING TO LOBBY NOW FOR FASTER LOGIN
//                        postTaskKey = GET_CURRENT_ACCESSTOKEN_AND_UPDATEIFCHANGED;
//                        new DoTaskWithProgress().execute(70);
                    } else {//Not Logged in
                        postTaskKey = GET_CURRENT_ACCESSTOKEN_AND_UPDATEIFCHANGED;
                        new DoTaskWithProgress().execute(70);
                    }
                } else{
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                                getResources().getString(R.string.no_internet_err));
                    JoshApplication.getErrorDialog().setCancelable(false);
                    JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);
                        JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onErrorDialogOkClicked();
                            }
                        });
                }
                break;

            case GET_CURRENT_ACCESSTOKEN_AND_UPDATEIFCHANGED:
                if(JoshApplication.isInternetAvailable(mActivityContext)) {
                    saveFCMTokenToServer(mActivityContext);
                }else{
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                            getResources().getString(R.string.no_internet_err));
                    JoshApplication.getErrorDialog().setCancelable(false);
                    JoshApplication.getErrorDialog().setCanceledOnTouchOutside(false);
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onErrorDialogOkClicked();
                        }
                    });
                }
                break;

            case GO_TO_NEXTACTIVITY:
                    Intent i;

                    if(JoshApplication.isLoggedIn(mActivityContext)){//Logged in
                        Log.d(TAG, "Current user - "+ JoshApplication.username(mActivityContext));
                        i = new Intent(SplashScreenActivity.this, HomeActivity.class);
                    }
                    else{//Not Logged in
                        Log.d(TAG, "Remembered user - "+ JoshApplication.username(mActivityContext));
                        i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    }
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                    overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                    finish();
                break;
        }
    }

    //FCM SAVE
    private void saveFCMTokenToServer(final Context ctx) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String storedFcmToken = JoshApplication.fcmToken(ctx);

                if (storedFcmToken==null) {
                    try {
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( (Activity) ctx,  new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                final String newToken = instanceIdResult.getToken();
                                Log.d(TAG,"NEW FCM TOKEN = "+newToken);

                                JoshApplication.getFcmTokenUpdateApiCall(getResources().getString(R.string.josh_server_api_key),
                                    JoshApplication.installKey(mActivityContext), JoshApplication.FCM_ID_TYPE, newToken).enqueue(new Callback<JoshFcmTokenUpdateResponseData>() {
                                @Override
                                public void onResponse(Call<JoshFcmTokenUpdateResponseData> call, Response<JoshFcmTokenUpdateResponseData> response) {
                                    if (response.isSuccessful()) {
                                        Log.d(TAG, "getFcmTokenUpdateApiCall Success - " + response.body().toString());
                                        final JoshFcmTokenUpdateResponseData joshFcmTokenUpdateResponseData = response.body();
                                        JoshApplication.saveFcmToken(newToken);

                                        //Send to next phase
                                        postTaskKey = GO_TO_NEXTACTIVITY;
                                        new DoTaskWithProgress().execute(100);
                                    } else {
                                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                                        Log.d(TAG, "getFcmTokenUpdateApiCall UnSuccessfull - " + response.errorBody());
                                        //Send to next phase without blocking the app for this reason
                                        postTaskKey = GO_TO_NEXTACTIVITY;
                                        new DoTaskWithProgress().execute(100);
                                    }
                                }

                                @Override
                                public void onFailure(Call<JoshFcmTokenUpdateResponseData> call, Throwable t) {
                                    Log.d(TAG, "getFcmTokenUpdateApiCall UnSuccessfull - " + t.getLocalizedMessage());

                                    //Send to next phase without blocking the app for this reason
                                    postTaskKey = GO_TO_NEXTACTIVITY;
                                    new DoTaskWithProgress().execute(100);
                                }
                            });
                        }
                        }).addOnFailureListener((Activity) ctx, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "FCM FAILED - "+e.getMessage());

                                //Send to next phase without blocking the app for this reason
                                postTaskKey = GO_TO_NEXTACTIVITY;
                                new DoTaskWithProgress().execute(100);
                            }
                        }).addOnCanceledListener((Activity) ctx, new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Log.e(TAG, "FCM CANCELLED");

                                //Send to next phase without blocking the app for this reason
                                postTaskKey = GO_TO_NEXTACTIVITY;
                                new DoTaskWithProgress().execute(100);
                            }
                        });
                    } catch (final Exception ex) {
                        Log.e(TAG, "Could not get new FCM registraion id / Error in sending FCM token to server", ex);

                        //Send to next phase without blocking the app for this reason
                        postTaskKey = GO_TO_NEXTACTIVITY;
                        new DoTaskWithProgress().execute(100);
                    }
                }
            }
        }).start();
    }

    private void onErrorDialogOkClicked(){
        JoshApplication.closeErrorDialog(mActivityContext);
        postTaskKey = GO_TO_NEXTACTIVITY;
        new DoTaskWithProgress().execute(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
    }
}
