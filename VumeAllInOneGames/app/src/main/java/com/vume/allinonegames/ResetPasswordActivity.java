package com.vume.allinonegames;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshGenericOnlyStatusResponseData;
import com.vume.allinonegames.Model.JoshIsMobileNoRegisteredExistsResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.InputValidator;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = ResetPasswordActivity.class.getName();
    private Context mActivityContext;

    private static final int MOBILENO_SCREEN = 10;
    private static final int OTP_SCREEN = 20;
    private static final int NEWPASSWORD_SCREEN = 30;

    private int currentScreen = MOBILENO_SCREEN;

    private ViewStub stub;
    private ConstraintLayout currentViewStubView;

    private Button btnGetOtp, btnSubmitOtp, btnResetPassword;
    private TextView heading, lblCountryCode, lbl_otptimer, btn_resendotp, lbl_otp_heading, btnEditPhoneNo;
    private EditText edt_mobileno, edt_newpassword, edt_confirmpassword, edt_otp;
    private View btn_changecountrycode;
    private ImageView btn_togglepasswordvisibility;
    private Switch switch_rememberme;

    // [START Firebase Authentication]
    private String mVerificationId, mRegisteringPhoneNo;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String firebaseAuthResultIdToken;
    private AuthCredential authCrednetial;
    private  CountDownTimer countdownTimer;
    private final static int resendOtpTime = 60; //In Seconds//TODO:MAke the value 60 in real
    private int counter = resendOtpTime;
    private String registeredUsername;
    private boolean isVerifyingPhoneNo = false;
    // [END Firebase Authentication]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        final long time1 = System.currentTimeMillis();//START LOAD TIME
        setContentView(R.layout.activity_resetpass);
        stub = (ViewStub) findViewById(R.id.resetpass_viewstub);
        stub.setLayoutResource(R.layout.resetpass_entermobileno);
        currentViewStubView = (ConstraintLayout) stub.inflate(); // inflate Enter Mobile NO
        //JoshApplication.setVisibilityCrossfadeAnim(mActivityContext, currentViewStubView, true);
        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
        final long time2 = System.currentTimeMillis();//END LOAD TIME
        Log.i(TAG, "onCreate: Load time = " + (time2 - time1));

        initUI();
    }

    private void initUI(){
        //Set Main heading for reset passoword
        heading = findViewById(R.id.tv_heading);
        heading.setText(getResources().getString(R.string.earn_extra_heading));

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                //TurupApplication.closeSpinnerProgress(mActivityContext);
                //Send Continue Register event for register flow only once otp is verified
                JoshApplication.sendContinueRegistrationEvent(mActivityContext, mRegisteringPhoneNo);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.d(TAG, "onVerificationFailed", e);
                JoshApplication.closeSpinnerProgress(mActivityContext);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.fill_all_entries_error_title),
                            getResources().getString(R.string.invalid_mobile_no));
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    JoshApplication.customisedtoast(mActivityContext, getResources().getString(R.string.quota_exceeded));
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                JoshApplication.sendStartRegistrationEvent(mActivityContext, mRegisteringPhoneNo);

                gotToVerifyOtpScreen();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };


        initDynamicUI();
    }

    private void initDynamicUI(){
        if(currentScreen == MOBILENO_SCREEN) {
            btnGetOtp = findViewById(R.id.btn_getotp);
            btn_changecountrycode = findViewById(R.id.btn_changecountrycode);
            edt_mobileno = findViewById(R.id.edt_mobileno);

            lblCountryCode = findViewById(R.id.lbl_countrycode);

            btnGetOtp.setOnClickListener(this);
            btn_changecountrycode.setOnClickListener(this);
            lblCountryCode.setText("+" + JoshApplication.getCurrentCountryCode(mActivityContext));
        }
        else if(currentScreen == OTP_SCREEN){
            btnSubmitOtp = findViewById(R.id.btn_submitotp);
            btn_resendotp = findViewById(R.id.lbl_resendotp);
            lbl_otptimer = findViewById(R.id.lbl_timer);
            btnEditPhoneNo = findViewById(R.id.btn_edtmobileno);

            lbl_otp_heading = findViewById(R.id.lbl_otpsentheading);
            lbl_otp_heading.setText(getResources().getString(R.string.enter_otp_heading, mRegisteringPhoneNo));

            edt_otp = findViewById(R.id.edt_otp);

            btnSubmitOtp.setOnClickListener(this);
            btn_resendotp.setOnClickListener(this);
            btnEditPhoneNo.setOnClickListener(this);
        }
        else if(currentScreen == NEWPASSWORD_SCREEN){
            edt_newpassword = findViewById(R.id.edt_setnewpass);
            edt_confirmpassword = findViewById(R.id.edt_confirmpass);

            btnResetPassword = findViewById(R.id.btn_submitnewpass);
            btn_togglepasswordvisibility = findViewById(R.id.btn_togglepasswordvisibility);
            btnResetPassword.setOnClickListener(this);

            switch_rememberme = findViewById(R.id.switch_rememberme);

            switch_rememberme.setChecked(JoshApplication.isRememberMeTicked(mActivityContext));
            switch_rememberme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    JoshApplication.saveRememberMeTick(isChecked);
                    switch_rememberme.setChecked(isChecked);
                }
            });
            JoshApplication.togglePasswordTextVisibility(edt_newpassword, btn_togglepasswordvisibility);
        }
    }

    private void updateNextDynamicScreenUIWithAnimation(){
        JoshApplication.closeSpinnerProgress(mActivityContext);
        switch (currentScreen){
            case MOBILENO_SCREEN:
                if(true) { //Data Validated
                    JoshApplication.loadDynamicLayoutInViewStubView(mActivityContext, currentViewStubView, R.layout.resetpass_enterotp);
                    currentScreen = OTP_SCREEN;
                    initDynamicUI();
                }
                break;

            case OTP_SCREEN:
                if(true) { //Data Validated
                    JoshApplication.loadDynamicLayoutInViewStubView(mActivityContext, currentViewStubView, R.layout.resetpass_enternewpass);
                    currentScreen = NEWPASSWORD_SCREEN;
                    initDynamicUI();
                }
                break;

        }
    }

    @Override
    public void onBackPressed() {
        switch (currentScreen){
            case MOBILENO_SCREEN:
                if(getIntent().hasExtra(JoshApplication.NAVIGATED_FROM) && getIntent().getStringExtra(JoshApplication.NAVIGATED_FROM).equalsIgnoreCase(RegisterActivity.class.getName())){
                    Intent i = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(JoshApplication.NAVIGATED_FROM, TAG);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                    overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                    finish();
                }
                else {
                    super.onBackPressed();
                    overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                }
                break;

            case OTP_SCREEN:
                if(countdownTimer!=null)
                    countdownTimer.cancel();
                JoshApplication.loadDynamicLayoutInViewStubView(mActivityContext, currentViewStubView, R.layout.resetpass_entermobileno);
                currentScreen = MOBILENO_SCREEN;
                initDynamicUI();
                break;

            case NEWPASSWORD_SCREEN:
                JoshApplication.loadDynamicLayoutInViewStubView(mActivityContext, currentViewStubView, R.layout.resetpass_entermobileno);
                currentScreen = MOBILENO_SCREEN;
                initDynamicUI();
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_getotp:
                final String mRegisterPhoneNo = String.valueOf(edt_mobileno.getText()).trim();
                if (!(mRegisterPhoneNo.equalsIgnoreCase("")) && mRegisterPhoneNo.length() == 10) {
                    if (JoshApplication.isInternetAvailable(mActivityContext)) {
                        final String countryCode = lblCountryCode.getText().toString().trim();
                        Log.d(TAG, "Register Country code - " + Locale.getDefault().getDisplayCountry() + "---Register Country code--" + countryCode);
                        startPhoneNumberVerificationForLogin(countryCode + mRegisterPhoneNo);
                        JoshApplication.startSpinnerProgress(mActivityContext, getResources().getString(R.string.sending_verification_code));
                        //TurupApplication.customisedtoast(mActivityContext, "Continue register");
                    } else {
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                                getResources().getString(R.string.no_internet_err));
                    }
                } else {
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                            getResources().getString(R.string.fill_all_entries_error_title));
                }
                break;

            case R.id.btn_changecountrycode:
                final String[] countryCodeList = getResources().getStringArray(R.array.country_code);
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(mActivityContext);
                builder.setTitle(getResources().getString(R.string.select_country_code))
                        .setItems(getResources().getStringArray(R.array.country_code), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int itemIndex) {
                                String selectedCountryCode = countryCodeList[itemIndex].trim();
                                selectedCountryCode = selectedCountryCode.split("\\(")[1];
                                selectedCountryCode = selectedCountryCode.substring(0, selectedCountryCode.length()-1);

                                lblCountryCode.setText(selectedCountryCode);
                            }
                        });
                builder.create().show();
                break;

            case R.id.lbl_resendotp:
                if(JoshApplication.isInternetAvailable(mActivityContext)) {
                    resendVerificationCode(mRegisteringPhoneNo, mResendToken);
                    JoshApplication.startSpinnerProgress(mActivityContext, getResources().getString(R.string.resending_otp));
                }
                else{
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                            getResources().getString(R.string.no_internet_err));
                }
                break;

            case R.id.btn_submitotp:
                JoshApplication.closeInputKeybrd(mActivityContext, edt_otp);
                final String otp = String.valueOf(edt_otp.getText()).trim();
                if(JoshApplication.isInternetAvailable(mActivityContext)) {
                    //VERIFYING FOR CORRECT CODE
                    if (!otp.equalsIgnoreCase("")) {
                        verifyPhoneNumberWithCode(mVerificationId, otp);
                    } else {
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.fill_all_entries_error_title),
                                getResources().getString(R.string.no_otp_error));
                    }
                }
                else{
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                            getResources().getString(R.string.no_internet_err));
                }
                break;

            case R.id.btn_submitnewpass:
                final String passwordTxt = edt_newpassword.getText().toString().trim();
                final String confirmPassword = edt_confirmpassword.getText().toString().trim();

                if(passwordTxt.equalsIgnoreCase("") || confirmPassword.equalsIgnoreCase("")){
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.fill_all_entries_error_title),
                            getResources().getString(R.string.fill_all_entries_error));
                }
                else if(!passwordTxt.equals(confirmPassword)){
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.fill_all_entries_error_title),
                            getResources().getString(R.string.password_confirmpass_doesntmatch));
                }
                else if(!(InputValidator.isValidPassword(passwordTxt) && InputValidator.isValidPassword(confirmPassword))){
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.fill_all_entries_error_title),
                            getResources().getString(R.string.invalid_password_error));
                }
                else {
                    submitResetPassword();
                }
                break;

            case R.id.btn_edtmobileno:
                onBackPressed();
                break;

        }
    }

    private void startPhoneNumberVerificationForLogin(String phoneNumber) {
        mRegisteringPhoneNo = phoneNumber;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                resendOtpTime,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        if(!isVerifyingPhoneNo) {
            isVerifyingPhoneNo = true;
            JoshApplication.startSpinnerProgress(mActivityContext, getResources().getString(R.string.verifying_otp));
            JoshApplication.getMyFirebaseAuth(mActivityContext).signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> signintask) {
                            isVerifyingPhoneNo = false;
                            if (signintask.isSuccessful()) {
                                //Send Continue Register event for register flow only once otp is verified
                                JoshApplication.sendContinueRegistrationEvent(mActivityContext, mRegisteringPhoneNo);

                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithPhoneAuthCredential:success");
                                final FirebaseUser user = signintask.getResult().getUser();
                                authCrednetial = signintask.getResult().getCredential();

                                JoshApplication.getMyFirebaseUser(mActivityContext).getIdToken(false)
                                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                if (task.isSuccessful()) {
                                                    firebaseAuthResultIdToken = task.getResult().getToken();
                                                    Log.d("asisi REGISTER", "User UUID - \n\n" + JoshApplication.getMyFirebaseUser(mActivityContext).getUid()
                                                            + "\n\n--User idtoken -\n\n" + firebaseAuthResultIdToken
                                                            + "\n\n--authresult.credentials -\n\n" + authCrednetial
                                                            + "\n\n--Phone =\n\n" + JoshApplication.getMyFirebaseUser(mActivityContext).getPhoneNumber()
                                                            + "\n\n--username -\n\n" + JoshApplication.getMyFirebaseUser(mActivityContext).getDisplayName()
                                                            + "\n\n--userproviderid -\n\n" + JoshApplication.getMyFirebaseUser(mActivityContext).getProviderId());

                                                    sendIfMobileNoAlreadyRegistered(firebaseAuthResultIdToken, "" + authCrednetial);

                                                } else {
                                                    JoshApplication.closeSpinnerProgress(mActivityContext);
                                                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                                                            getResources().getString(R.string.something_went_wromg));
                                                }
                                            }
                                        });
                            } else {
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                Log.w(TAG, "signInWithPhoneAuthCredential:failure", signintask.getException());
                                JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                                        getResources().getString(R.string.invalid_otp));

                            }
                        }
                    });
        }
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                resendOtpTime,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);

        JoshApplication.customisedtoast(mActivityContext, "Code Resend...");
    }

    private void goToRegisterUsernamePasswordScreen(){
        currentScreen = OTP_SCREEN; //Skipping the otp screen//OTP AutoVerified
        updateNextDynamicScreenUIWithAnimation();
    }

    private void gotToVerifyOtpScreen(){
        currentScreen = MOBILENO_SCREEN;
        updateNextDynamicScreenUIWithAnimation();
        JoshApplication.closeSpinnerProgress(mActivityContext);
        JoshApplication.customisedtoast(mActivityContext, getResources().getString(R.string.no_otp_error));

        counter = resendOtpTime;
        countdownTimer = new CountDownTimer(resendOtpTime*1000, 1000){
            public void onTick(long millisUntilFinished){
                if(counter<10){
                    lbl_otptimer.setText(String.format(getResources().getString(R.string.otp_countdowntimerwithzero), String.valueOf(counter)));
                }
                else {
                    lbl_otptimer.setText(String.format(getResources().getString(R.string.otp_countdowntimer), String.valueOf(counter)));
                }
                counter--;
            }
            public  void onFinish(){
                lbl_otptimer.setVisibility(View.INVISIBLE);
                btn_resendotp.setVisibility(View.VISIBLE);
                countdownTimer.cancel();
            }
        };
        countdownTimer.start();
    }

    private void sendIfMobileNoAlreadyRegistered(String firebaseAuthResultIdToken, String credential){
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getIsMobileNoAlreadyExistApi(getResources().getString(R.string.josh_server_api_key),
                    firebaseAuthResultIdToken, JoshApplication.installKey(mActivityContext), credential).enqueue(new Callback<JoshIsMobileNoRegisteredExistsResponseData>() {
                @Override
                public void onResponse(Call<JoshIsMobileNoRegisteredExistsResponseData> call, Response<JoshIsMobileNoRegisteredExistsResponseData> response) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    if(response.isSuccessful()) {//Mobile No Already Exists
                        final JoshIsMobileNoRegisteredExistsResponseData responseIsMobileNoAlreadyExistsData = response.body();
                        if(responseIsMobileNoAlreadyExistsData!=null){
                            registeredUsername = responseIsMobileNoAlreadyExistsData.getmUsername();
                            JoshApplication.customisedtoast(mActivityContext, getResources().getString(R.string.mobile_verification_successfull));
                            goToRegisterUsernamePasswordScreen();
                            Log.d(TAG, "getIsMobileNoAlreadyExistApi Successfull - Already Exists! - "+responseIsMobileNoAlreadyExistsData.getmUsername());
                        }
                        else{
                            showMobileNoNotRegisteredError();
                            Log.d(TAG, "getIsMobileNoAlreadyExistApi Successfull - Already Exists but Response is null!");
                        }
                    }
                    else{
                        showMobileNoNotRegisteredError();
                        Log.d(TAG, "getIsMobileNoAlreadyExistApi UnSuccessfull - Doesn't exist");
                    }
                }

                @Override
                public void onFailure(Call<JoshIsMobileNoRegisteredExistsResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    showMobileNoNotRegisteredError();
                    Log.d(TAG, "getIsMobileNoAlreadyExistApi UnSuccessfull - "+t.getMessage());
                }
            });
        }
        else{
            JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                    getResources().getString(R.string.no_internet_err));
        }
    }

    private void showMobileNoNotRegisteredError(){
        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.registration_failed_title),
                getResources().getString(R.string.mobile_no_not_registered));
        JoshApplication.getErrorDialogRightButton(mActivityContext).setText(getResources().getString(R.string.register_label));
        JoshApplication.getErrorDialogRightButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResetPasswordActivity.this, RegisterActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(JoshApplication.NAVIGATED_FROM, TAG);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                finish();
            }
        });
        JoshApplication.getErrorDialogLeftButton(mActivityContext).setText(getResources().getString(R.string.login_label));
        JoshApplication.getErrorDialogLeftButton(mActivityContext).setVisibility(View.VISIBLE);
        JoshApplication.getErrorDialogLeftButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(JoshApplication.NAVIGATED_FROM, TAG);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                finish();
            }
        });
    }

    private void submitResetPassword(){
        JoshApplication.startSpinnerProgress(mActivityContext,getResources().getString(R.string.resetting_password));
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getResetPasswordApiCall(getResources().getString(R.string.josh_server_api_key),
                    edt_newpassword.getText().toString().trim(), firebaseAuthResultIdToken, ""+authCrednetial, registeredUsername).enqueue(new Callback<JoshGenericOnlyStatusResponseData>() {
                @Override
                public void onResponse(Call<JoshGenericOnlyStatusResponseData> call, Response<JoshGenericOnlyStatusResponseData> response) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    if(response.isSuccessful()) {
                        if(switch_rememberme.isChecked()){
                            JoshApplication.saveUsername(registeredUsername);
                            JoshApplication.savePassword(edt_newpassword.getText().toString().trim());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, "",
                                getResources().getString(R.string.reset_password_successful));
                        JoshApplication.getErrorDialogRightButton(mActivityContext).setText(getResources().getString(R.string.login_label));
                        JoshApplication.getErrorDialogRightButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra(JoshApplication.NAVIGATED_FROM, TAG);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(i);
                                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                                finish();
                            }
                        });
                        Log.d(TAG, "submitResetPassword Successfull");
                    }
                    else{
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg;
                        if(errorResponseObj!=null){
                            errorMsg = errorResponseObj.getmMessage();
                        }
                        else{
                            errorMsg = getResources().getString(R.string.reset_password_failed);
                        }
                        JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.error_title),
                                errorMsg);

                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));

                        Log.d(TAG, "submitResetPassword UnSuccessfull");
                    }
                }

                @Override
                public void onFailure(Call<JoshGenericOnlyStatusResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.error_title),
                            getResources().getString(R.string.reset_password_failed));
                    Log.d(TAG, "submitResetPassword UnSuccessfull - "+t.getMessage());
                }
            });
        }
        else{
            JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                    getResources().getString(R.string.no_internet_err));
        }
    }
}
