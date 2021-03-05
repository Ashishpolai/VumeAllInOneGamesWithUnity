package com.vume.allinonegames;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vume.allinonegames.Model.JoshIsMobileNoRegisteredExistsResponseData;
import com.vume.allinonegames.Util.JoshApplication;
import com.vume.allinonegames.Util.JoshLocaleUtils;
import com.vume.allinonegames.Util.InputValidator;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = RegisterActivity.class.getName();
    private Context mActivityContext;

    private static final int LANGUAGESELECTION_SCREEN = 10;
    private static final int MOBILENO_SCREEN = 20;
    private static final int ENTEROTP_SCREEN = 30;
    private static final int ENTERNEWPASSWORD_SCREEN = 40;

    private int currentScreen = LANGUAGESELECTION_SCREEN;
    private String currentLanguage;

    private ViewStub stub;
    private ConstraintLayout currentViewStubView;
    private TextView heading, lblEnglish, lblHindi, lblTelugu, lblTamil, lblMalayalam, lblKannada, lblCountryCode, btn_gotoeditmobileno, lblOtpTimer, btn_resendotp, lbl_heading;
    private ImageView tickEnglish, tickHindi, tickTelugu, tickTamil, tickMalayalam, tickKannada, btn_togglepassword;
    private Button btn_confirmlang, btn_getotp, btn_confirmotp, btn_gottologin, btn_register;
    private EditText edt_mobileno, edt_referralcode, edt_otp, edt_password, edt_confirmpassworf;
    private View btn_changecountrycode;
    private String referralCode;

    // [START Firebase Authentication]
    private String mVerificationId, mRegisteringPhoneNo;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String firebaseAuthResultIdToken;
    private AuthCredential authCrednetial;
    private  CountDownTimer countdownTimer;
    private final static int resendOtpTime = 60; //In Seconds//TODO:MAke the value 60 in real
    private int counter = resendOtpTime;
    private boolean isVerifyingPhoneNo = false;
    // [END Firebase Authentication]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        final long time1 = System.currentTimeMillis();//START LOAD TIME
        setContentView(R.layout.activity_regsitration);
        stub = (ViewStub) findViewById(R.id.register_viewstub);
        stub.setLayoutResource(R.layout.register_chooselang);
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
        heading.setText(getResources().getString(R.string.login_screen_msg));

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
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.error_title),
                            getResources().getString(R.string.quota_exceeded));
                    //JoshApplication.customisedtoast(mActivityContext, getResources().getString(R.string.quota_exceeded));
                }else if (e instanceof FirebaseAuthException) {
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.error_title),
                            e.getMessage());
                    //JoshApplication.customisedtoast(mActivityContext, "Invalid Configuration!");
                }
                else{
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.error_title),
                            e.getMessage());
                    //JoshApplication.customisedtoast(mActivityContext, "Verification Failed!");
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
        if(currentScreen == LANGUAGESELECTION_SCREEN) {
            btn_confirmlang = findViewById(R.id.btn_chooselanguage);
            btn_confirmlang.setOnClickListener(this);

            lblEnglish = findViewById(R.id.lbl_English);
            tickEnglish = findViewById(R.id.img_selected_english);
            lblHindi = findViewById(R.id.lbl_hindi);
            tickHindi = findViewById(R.id.img_selected_hindi);
            lblTelugu = findViewById(R.id.lbl_telugu);
            tickTelugu = findViewById(R.id.img_selected_telugu);
            lblTamil = findViewById(R.id.lbl_tamil);
            tickTamil = findViewById(R.id.img_selected_tamil);
            lblMalayalam = findViewById(R.id.lbl_malayalam);
            tickMalayalam = findViewById(R.id.img_selected_malayalam);
            lblKannada = findViewById(R.id.lbl_kannada);
            tickKannada = findViewById(R.id.img_selected_kannada);

            lblEnglish.setOnClickListener(this);
            lblHindi.setOnClickListener(this);
            lblTelugu.setOnClickListener(this);
            lblTamil.setOnClickListener(this);
            lblMalayalam.setOnClickListener(this);
            lblKannada.setOnClickListener(this);

            heading.setText(getResources().getString(R.string.login_screen_msg));
            currentLanguage = JoshApplication.localeUtils(mActivityContext).getSelectedLanguage();;
            setLanguageUI(currentLanguage);
        }
        else if(currentScreen == MOBILENO_SCREEN){
            btn_getotp = findViewById(R.id.btn_getotp);
            btn_gottologin = findViewById(R.id.btn_gotologin);
            btn_changecountrycode = findViewById(R.id.btn_changecountrycode);
            btn_getotp.setOnClickListener(this);
            btn_gottologin.setOnClickListener(this);
            btn_changecountrycode.setOnClickListener(this);

            edt_mobileno = findViewById(R.id.edt_mobileno);
            edt_referralcode = findViewById(R.id.edt_referralcode);
            lblCountryCode = findViewById(R.id.lbl_countrycode);

            heading.setText(getResources().getString(R.string.earn_extra_heading));
            lblCountryCode.setText("+" + JoshApplication.getCurrentCountryCode(mActivityContext));
        }
        else if(currentScreen == ENTEROTP_SCREEN){
            btn_confirmotp = findViewById(R.id.btn_submitotp);
            btn_gotoeditmobileno = findViewById(R.id.btn_edtmobileno);
            btn_resendotp = findViewById(R.id.lbl_resendotp);
            btn_resendotp.setVisibility(View.INVISIBLE);
            lbl_heading = findViewById(R.id.lbl_otpsentheading);
            lbl_heading.setText(getResources().getString(R.string.enter_otp_heading, mRegisteringPhoneNo));

            btn_confirmotp.setOnClickListener(this);
            btn_gotoeditmobileno.setOnClickListener(this);
            btn_resendotp.setOnClickListener(this);

            edt_otp = findViewById(R.id.edt_otp);
            lblOtpTimer = findViewById(R.id.lbl_timer);

            heading.setText(getResources().getString(R.string.earn_extra_heading));
        }
        else if(currentScreen == ENTERNEWPASSWORD_SCREEN){
            edt_password = findViewById(R.id.edt_new_password);
            edt_confirmpassworf = findViewById(R.id.edt_confirmpassword);
            btn_togglepassword = findViewById(R.id.btn_togglepasswordvisibility);
            btn_register = findViewById(R.id.btn_register);

            btn_register.setOnClickListener(this);

            JoshApplication.togglePasswordTextVisibility(edt_password, btn_togglepassword);
            heading.setText(getResources().getString(R.string.earn_extra_heading));
        }
    }

    private void updateNextDynamicScreenUIWithAnimation(){
        JoshApplication.closeSpinnerProgress(mActivityContext);
        switch (currentScreen){
            case LANGUAGESELECTION_SCREEN:
                if(true) { //Data Validated
                    JoshApplication.loadDynamicLayoutInViewStubView(mActivityContext, currentViewStubView, R.layout.register_entermobileno);
                    currentScreen = MOBILENO_SCREEN;
                    initDynamicUI();
                }
                break;

            case MOBILENO_SCREEN:
                if(true) { //Data Validated
                    JoshApplication.loadDynamicLayoutInViewStubView(mActivityContext, currentViewStubView, R.layout.register_enterotp);
                    currentScreen = ENTEROTP_SCREEN;
                    initDynamicUI();
                }
                break;

            case ENTEROTP_SCREEN:
                if(true) { //Data Validated
                    JoshApplication.loadDynamicLayoutInViewStubView(mActivityContext, currentViewStubView, R.layout.register_enternewpass);
                    currentScreen = ENTERNEWPASSWORD_SCREEN;
                    initDynamicUI();
                }
                break;
        }
    }

    private TextView getSelectedLanguageTextview(String selectedLanguage){
        switch(selectedLanguage){
            case JoshLocaleUtils.HINDI:
                return lblHindi;

            case JoshLocaleUtils.TELUGU:
                return lblTelugu;

            case JoshLocaleUtils.TAMIL:
                return lblTamil;

            case JoshLocaleUtils.KANNADA:
                return lblKannada;

            case JoshLocaleUtils.MALAYALAM:
                return lblMalayalam;

            case JoshLocaleUtils.ENGLISH:
            default:
                return lblEnglish;

        }
    }

    private ImageView getSelectedLanguageTickImage(String selectedLanguage){
        switch(selectedLanguage){
            case JoshLocaleUtils.HINDI:
                return tickHindi;

            case JoshLocaleUtils.TELUGU:
                return tickTelugu;

            case JoshLocaleUtils.TAMIL:
                return tickTamil;

            case JoshLocaleUtils.KANNADA:
                return tickKannada;

            case JoshLocaleUtils.MALAYALAM:
                return tickMalayalam;

            case JoshLocaleUtils.ENGLISH:
            default:
                return tickEnglish;

        }
    }

    private void setLanguageUI(String selectedLanguage){
        getSelectedLanguageTextview(selectedLanguage).setTypeface(ResourcesCompat.getFont(mActivityContext, R.font.rubikmedium));
        getSelectedLanguageTextview(selectedLanguage).setTextColor(getResources().getColor(R.color.orangydark));
        getSelectedLanguageTickImage(selectedLanguage).setVisibility(View.VISIBLE);
        btn_confirmlang.setText(getResources().getString(R.string.confirm_langauge, getSelectedLanguageTextview(selectedLanguage).getText().toString()));

        if(currentLanguage != selectedLanguage){//Make selected langauge as current and refresh the ui
            getSelectedLanguageTextview(currentLanguage).setTypeface(ResourcesCompat.getFont(mActivityContext, R.font.rubikregular));
            getSelectedLanguageTextview(currentLanguage).setTextColor(getResources().getColor(R.color.orangydark));
            getSelectedLanguageTextview(currentLanguage).setAlpha(0.6f);
            getSelectedLanguageTickImage(currentLanguage).setVisibility(View.INVISIBLE);
            currentLanguage = selectedLanguage;
        }
    }

    @Override
    public void onBackPressed() {
        switch (currentScreen){
            case LANGUAGESELECTION_SCREEN:
                if(getIntent().hasExtra(JoshApplication.NAVIGATED_FROM) && getIntent().getStringExtra(JoshApplication.NAVIGATED_FROM).equalsIgnoreCase(ResetPasswordActivity.class.getName())){
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
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

            case MOBILENO_SCREEN:
                JoshApplication.loadDynamicLayoutInViewStubView(mActivityContext, currentViewStubView, R.layout.register_chooselang);
                currentScreen = LANGUAGESELECTION_SCREEN;
                initDynamicUI();
                break;

            case ENTEROTP_SCREEN:
                if(countdownTimer!=null)
                    countdownTimer.cancel();
                JoshApplication.loadDynamicLayoutInViewStubView(mActivityContext, currentViewStubView, R.layout.register_entermobileno);
                currentScreen = MOBILENO_SCREEN;
                initDynamicUI();
                break;

            case ENTERNEWPASSWORD_SCREEN:
                JoshApplication.loadDynamicLayoutInViewStubView(mActivityContext, currentViewStubView, R.layout.register_entermobileno);
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
            case R.id.btn_chooselanguage:
                JoshApplication.localeUtils(mActivityContext).setCurrentLanguage(currentLanguage);
                updateNextDynamicScreenUIWithAnimation();
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

            case R.id.btn_getotp:
                referralCode = edt_referralcode.getText().toString().trim();
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
                            getResources().getString(R.string.invalid_mobile_no));
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

            case R.id.btn_edtmobileno:
                onBackPressed();
                break;

            case R.id.btn_gotologin:
                finish();
                break;

            case R.id.btn_register:
                final String passwordTxt = edt_password.getText().toString().trim();
                final String confirmPassword = edt_confirmpassworf.getText().toString().trim();

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
                    Intent signUpBonusIntent = new Intent(RegisterActivity.this, RegisterSignUpBonusActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("password", passwordTxt);
                    bundle.putString("authCredential", authCrednetial == null?null:authCrednetial.toString());
                    bundle.putString("firebaseAuthResultIdToken", firebaseAuthResultIdToken);
                    signUpBonusIntent.putExtras(bundle);
                    signUpBonusIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(signUpBonusIntent);
                    overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                }
                break;

            case R.id.lbl_English:
                setLanguageUI(JoshLocaleUtils.ENGLISH);
                break;

            case R.id.lbl_hindi:
                setLanguageUI(JoshLocaleUtils.HINDI);
                break;

            case R.id.lbl_telugu:
                setLanguageUI(JoshLocaleUtils.TELUGU);
                break;

            case R.id.lbl_tamil:
                setLanguageUI(JoshLocaleUtils.TAMIL);
                break;

            case R.id.lbl_malayalam:
                setLanguageUI(JoshLocaleUtils.MALAYALAM);
                break;

            case R.id.lbl_kannada:
                setLanguageUI(JoshLocaleUtils.KANNADA);
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
        currentScreen = ENTEROTP_SCREEN; //Skipping the otp screen//OTP AutoVerified
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
                    lblOtpTimer.setText(String.format(getResources().getString(R.string.otp_countdowntimerwithzero), String.valueOf(counter)));
                }
                else {
                    lblOtpTimer.setText(String.format(getResources().getString(R.string.otp_countdowntimer), String.valueOf(counter)));
                }
                counter--;
            }
            public  void onFinish(){
                lblOtpTimer.setVisibility(View.INVISIBLE);
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
                            final String username = JoshApplication.hideVowelsInWord(responseIsMobileNoAlreadyExistsData.getmUsername());
                            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.registration_failed_title),
                                    getResources().getString(R.string.mobile_no_alreadyexist_error, username));
                            JoshApplication.getErrorDialogRightButton(mActivityContext).setText(getResources().getString(R.string.login_label));
                            JoshApplication.getErrorDialogRightButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                            JoshApplication.getErrorDialogLeftButton(mActivityContext).setText(getResources().getString(R.string.reset_password_btn_text));
                            //JoshApplication.getErrorDialogLeftButton(mActivityContext).setTextSize(15f);
                            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(JoshApplication.getErrorDialogLeftButton(mActivityContext), 1, 17, 1,
                                    TypedValue.COMPLEX_UNIT_DIP);
                            JoshApplication.getErrorDialogLeftButton(mActivityContext).setVisibility(View.VISIBLE);
                            JoshApplication.getErrorDialogLeftButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(RegisterActivity.this, ResetPasswordActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra(JoshApplication.NAVIGATED_FROM, TAG);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(i);
                                    overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                                    finish();
                                }
                            });
                            Log.d(TAG, "getIsMobileNoAlreadyExistApi Successfull - Already Exists! - "+responseIsMobileNoAlreadyExistsData.getmUsername());
                        }
                        else{
                            JoshApplication.customisedtoast(mActivityContext, getResources().getString(R.string.mobile_verification_successfull));
                            goToRegisterUsernamePasswordScreen();
                            Log.d(TAG, "getIsMobileNoAlreadyExistApi Successfull - Already Exists but Response is null!");
                        }
                    }
                    else{
                        JoshApplication.customisedtoast(mActivityContext, getResources().getString(R.string.mobile_verification_successfull));
                        goToRegisterUsernamePasswordScreen();
                        Log.d(TAG, "getIsMobileNoAlreadyExistApi UnSuccessfull - Doesn't exist");
                    }
                }

                @Override
                public void onFailure(Call<JoshIsMobileNoRegisteredExistsResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    JoshApplication.customisedtoast(mActivityContext, getResources().getString(R.string.mobile_verification_successfull));
                    goToRegisterUsernamePasswordScreen();
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
}
