package com.vume.allinonegames;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.vume.allinonegames.Model.FirebaseAnalyticsCustomKeys;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshLoginResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = LoginActivity.class.getName();
    private Context mActivityContext;

    Button btn_login, btn_register;
    TextView btn_resetpass;
    EditText edt_username, edt_password;
    Switch switch_rememberme;
    ImageView btn_togglepasswordvisisbility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        final long time1 = System.currentTimeMillis();
        setContentView(R.layout.activity_main);
        final long time2 = System.currentTimeMillis();
        //JoshApplication.setVisibilityCrossfadeAnim(mActivityContext, findViewById(R.id.mainLayout), true);
        Log.i(TAG, "onCreate: Load time = " + (time2 - time1));

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        initUI();
        initEvents();
    }

    private void initUI(){
        btn_resetpass = (TextView) findViewById(R.id.btn_resetpass);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        if(JoshApplication.isRememberMeTicked(mActivityContext)){
            edt_username.setText(JoshApplication.username(mActivityContext));
            edt_password.setText(JoshApplication.password(mActivityContext));
        }
        btn_togglepasswordvisisbility = findViewById(R.id.btn_togglepasswordvisibility);
        JoshApplication.togglePasswordTextVisibility(edt_password, btn_togglepasswordvisisbility);

        switch_rememberme = findViewById(R.id.switch_rememberme);
        switch_rememberme.setChecked(JoshApplication.isRememberMeTicked(mActivityContext));
    }

    private void initEvents(){
        btn_resetpass.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        switch_rememberme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JoshApplication.saveRememberMeTick(isChecked);
                switch_rememberme.setChecked(isChecked);
            }
        });
    }

    private void goToLobbyScreen(){
        Intent dashboardIntent= new Intent(LoginActivity.this, HomeActivity.class);
        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(dashboardIntent);
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
    }

    @Override
    public void onClick(View view) {
        if(view == btn_resetpass){
            Intent forgotpassIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            forgotpassIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(forgotpassIntent);
            overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
        }
        else if(view == btn_login){

            final String usernameTxtLogin = edt_username.getText().toString().trim();
            final String passwordTxtLogin = edt_password.getText().toString().trim();
            if(usernameTxtLogin.equalsIgnoreCase("") || passwordTxtLogin.equalsIgnoreCase("")){
                JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.fill_all_entries_error_title),
                        getResources().getString(R.string.fill_all_entries_error));
            }else{
                if(JoshApplication.isInternetAvailable(mActivityContext)) {
                    JoshApplication.startSpinnerProgress(mActivityContext, getResources().getString(R.string.logging_in_progress));
                    JoshApplication.getRegisterLoginApiCall(getResources().getString(R.string.josh_server_api_key),
                            edt_username.getText().toString().trim(), edt_password.getText().toString().trim(), JoshApplication.installKey(mActivityContext)).enqueue(new Callback<JoshLoginResponseData>() {
                        @Override
                        public void onResponse(Call<JoshLoginResponseData> call, Response<JoshLoginResponseData> response) {
                            if (response.isSuccessful()) {
                                final JoshLoginResponseData joshLoginResponseData = response.body();
                                JoshApplication.saveCurrentLoginAuthTokenFromServer(joshLoginResponseData.getmLoginToken());
                                JoshApplication.saveUsername(joshLoginResponseData.getmUsername());
                                JoshApplication.savePassword(edt_password.getText().toString().trim());
                                JoshApplication.saveUserId(joshLoginResponseData.getmUserId());
                                Log.d(TAG, "getRegisterLoginApiCall Success - " + response.body().toString());

                                JoshApplication.setAnalyticsUserPropertyIdentifers(mActivityContext);
                                JoshApplication.sendLoginEvent(mActivityContext, FirebaseAnalyticsCustomKeys.LOGIN_PARAMETER_METHOD_VALUE_USERNAMEPASS);

                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                goToLobbyScreen();
                            } else {
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                                String errorMsg, errorCode;
                                if(errorResponseObj != null){
                                    errorMsg = errorResponseObj.getmMessage();
                                    errorCode = errorResponseObj.getmErrorCode();
                                    Log.d(TAG, "getRegisterLoginApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                                }
                                else{
                                    errorMsg = getResources().getString(R.string.server_not_responding);
                                    errorCode = String.valueOf(response.code());
                                }
                                JoshApplication.sendLoginErrorEvent(mActivityContext, errorMsg,
                                        errorCode, FirebaseAnalyticsCustomKeys.LOGIN_PARAMETER_METHOD_VALUE_USERNAMEPASS);
                                JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.login_failed_title),
                                        errorMsg);
                            }
                        }

                        @Override
                        public void onFailure(Call<JoshLoginResponseData> call, Throwable t) {
                            JoshApplication.closeSpinnerProgress(mActivityContext);
                            Log.d(TAG, "getRegisterLoginApiCall UnSuccessfull - " + t.getLocalizedMessage());
                            JoshApplication.sendLoginErrorEvent(mActivityContext, t.getLocalizedMessage(),
                                    "", FirebaseAnalyticsCustomKeys.LOGIN_PARAMETER_METHOD_VALUE_USERNAMEPASS);
                            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                                    getResources().getString(R.string.server_not_responding));
                        }
                    });
                }
                else{
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                            getResources().getString(R.string.no_internet_err));
                }
            }
        }
        else if(view == btn_register){
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            registerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(registerIntent);
            overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}
