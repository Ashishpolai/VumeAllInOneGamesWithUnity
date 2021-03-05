package com.vume.allinonegames;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.vume.allinonegames.ApiCalls.JoshRummyServicesInterface;
import com.vume.allinonegames.ApiCalls.MyRetrofitInstance;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshGenericOnlyStatusResponseData;
import com.vume.allinonegames.Model.JoshProfileDetailsResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;
import com.vume.allinonegames.Util.InputValidator;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

import me.echodev.resizer.Resizer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = ProfileActivity.class.getName();
    Context mActivityContext;

    private SharedPreferences permissionStatus;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 2925;
    private static final int CAMERA_PERMISSION_CONSTANT = 29753;
    private static final int REQUEST_PERMISSION_SETTING = 22531;
    private static final int CAMERA_REQUEST_CODE = 04301;
    private static final int GALLERY_REQUEST_CODE = 043402;
    private static final String RUMMY_PERMISSION_STATUS = "joshrummypermissionstatus_callbreak";

    String prevImgPath = "";
    Uri uriSavedImage;
    private String imagePath = null;
    private String[] availableChooseProfileImgOption;

    private ImageView imgCamera;

    private View btnEditUsername, btnEditPassword, btnEditEmailID, btnUpdateBankAcc, btnUpdateKYC, btnChooseProfile;
    private Group layoutEdtProfile;
    private Button btnCancelUpdaetLayout, btnUpdateProfile;
    private TextView txtEdtProfileHeading, lblUsernameVal, lblPasswordVal, lblEmailIDVal,
            lblMobileNoValue, lblBankAccountValue, lblKycDetailsValue, lblEditBankAcctext, lblEdtKycText;
    private EditText edtProfileData;
    private ImageView btnBack, btnTogglePasswordVisibility, imgProfile;

    private int editLayoutIndex = 0; //0 for username, 1 for Password, 2 for emailid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_profile);

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        permissionStatus = getSharedPreferences(RUMMY_PERMISSION_STATUS,MODE_PRIVATE);
        loadProfileDetails();
    }

    private void initUI(){
        imgCamera = findViewById(R.id.img_camera);
        imgProfile = findViewById(R.id.img_profileavatar);
        imgCamera.bringToFront();

        btnEditUsername = findViewById(R.id.btn_edtUsername);
        btnEditPassword = findViewById(R.id.btn_edtpassword);
        btnEditEmailID = findViewById(R.id.btn_edtmailid);
        btnUpdateBankAcc = findViewById(R.id.btn_updatebankaccount);
        btnUpdateKYC = findViewById(R.id.btn_updatekyc);
        btnBack = findViewById(R.id.btn_back);
        btnChooseProfile = findViewById(R.id.btn_chooseprofileimg);
        btnTogglePasswordVisibility = findViewById(R.id.btn_togglepasswordvisibility);
        btnTogglePasswordVisibility.setImageResource(R.mipmap.password_visible);
        btnTogglePasswordVisibility.setVisibility(View.INVISIBLE);

        lblUsernameVal = findViewById(R.id.lbl_username_value);
        lblUsernameVal.setText(JoshApplication.username(mActivityContext));
        lblPasswordVal = findViewById(R.id.lbl_password_value);
        setPasswordText(lblPasswordVal, JoshApplication.password(mActivityContext).length());
        lblEmailIDVal = findViewById(R.id.lbl_emailid_value);
        lblEmailIDVal.setText(JoshApplication.email(mActivityContext));
        lblMobileNoValue = findViewById(R.id.lbl_mobileno_value);
        lblMobileNoValue.setText(JoshApplication.phoneno(mActivityContext));
        lblBankAccountValue = findViewById(R.id.lbl_bankaccount_value);
        lblKycDetailsValue = findViewById(R.id.lbl_kycdetails_value);
        lblEditBankAcctext = findViewById(R.id.lbl_bankaccount_edit);
        lblEdtKycText = findViewById(R.id.lbl_kycdetails_edit);

        txtEdtProfileHeading = findViewById(R.id.editlayout_heading);
        edtProfileData = findViewById(R.id.edt_profiledata);
        btnCancelUpdaetLayout = findViewById(R.id.btn_canceledit);
        btnUpdateProfile = findViewById(R.id.btn_updatedata);

        availableChooseProfileImgOption = getResources().getStringArray(R.array.selectprofileimgoption_array);

        btnEditUsername.setOnClickListener(this);
        btnEditPassword.setOnClickListener(this);
        btnEditEmailID.setOnClickListener(this);
        btnUpdateBankAcc.setOnClickListener(this);
        btnUpdateKYC.setOnClickListener(this);
        btnCancelUpdaetLayout.setOnClickListener(this);
        btnUpdateProfile.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnTogglePasswordVisibility.setOnClickListener(this);
        btnChooseProfile.setOnClickListener(this);

        layoutEdtProfile = findViewById(R.id.editlayout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_edtUsername:
                editLayoutIndex = 0;
                txtEdtProfileHeading.setText(getResources().getString(R.string.edt_username));
                btnTogglePasswordVisibility.setVisibility(View.INVISIBLE);
                edtProfileData.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                edtProfileData.setText(JoshApplication.username(mActivityContext));
                layoutEdtProfile.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_edtpassword:
                editLayoutIndex = 1;
                txtEdtProfileHeading.setText(getResources().getString(R.string.edt_password));
                btnTogglePasswordVisibility.setVisibility(View.VISIBLE);
                edtProfileData.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                edtProfileData.setSelection(edtProfileData.getText().length());
                edtProfileData.setText(JoshApplication.password(mActivityContext));
                layoutEdtProfile.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_edtmailid:
                editLayoutIndex = 2;
                txtEdtProfileHeading.setText(getResources().getString(R.string.edt_emailid));
                btnTogglePasswordVisibility.setVisibility(View.INVISIBLE);
                edtProfileData.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                if(!lblEmailIDVal.getText().toString().equalsIgnoreCase(getResources().getString(R.string.not_available))){
                    edtProfileData.setText(lblEmailIDVal.getText().toString());
                }
                else{
                    edtProfileData.setText("");
                }
                layoutEdtProfile.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_togglepasswordvisibility:
                JoshApplication.togglePasswordTextVisibility(edtProfileData, btnTogglePasswordVisibility);
                break;

            case R.id.btn_updatebankaccount:
                Intent updatebankActivity = new Intent(mActivityContext, UpdateBankAccActivity.class);
                updatebankActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(updatebankActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.btn_updatekyc:
                Intent updatekycActivity = new Intent(mActivityContext, UpdateKycActivity.class);
                updatekycActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(updatekycActivity);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;

            case R.id.btn_canceledit:
                if(layoutEdtProfile.getVisibility() == View.VISIBLE){
                    hideUpdateProfiledataLayout();

                }
                break;

            case R.id.btn_updatedata:
                if(editLayoutIndex == 0){//Update Username
                    if(isDataChangedAndNotNull(editLayoutIndex)) {
                        if(InputValidator.isValidUsername(edtProfileData.getText().toString().trim())) {
                            updateProfileusernameDataApiCall(edtProfileData.getText().toString().trim());
                        }
                        else{
                            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.fill_all_entries_error_title),
                                    getResources().getString(R.string.invalid_username_error));
                        }
                    }
                }
                else if(editLayoutIndex == 1){//Update Password
                    if(isDataChangedAndNotNull(editLayoutIndex)) {
                        if(InputValidator.isValidPassword(edtProfileData.getText().toString().trim())) {
                            updatePasswordApiCall();
                        }
                        else{
                            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.fill_all_entries_error_title),
                                    getResources().getString(R.string.invalid_password_error));
                        }
                    }
                }
                else if(editLayoutIndex == 2){//Update EmailId
                    if(isDataChangedAndNotNull(editLayoutIndex)) {
                        if(InputValidator.isEmailValid(edtProfileData.getText().toString())) {
                            updateProfileEmailDataApiCall(edtProfileData.getText().toString());
                        }
                        else{
                            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.fill_all_entries_error_title),
                                    mActivityContext.getResources().getString(R.string.invalid_emailid));
                        }
                    }
                }
                else{
                    hideUpdateProfiledataLayout();
                }
                break;

            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_chooseprofileimg:
                showChooseImageOption();
                break;
        }
    }

    private void setPasswordText(final TextView txtPassword, final int passwordLength){
        String hiddenPass = "";
        for(int i = 0; i<passwordLength; i++)
            hiddenPass+=JoshApplication.PASSWORD_KEY;
        txtPassword.setText(hiddenPass);
    }

    private boolean isDataChangedAndNotNull(int layoutType){
        final String newValue = edtProfileData.getText().toString().trim();
        String noentries_message = "", same_entriesmessage= "", compareValue = "";
        if(layoutType == 0){
            noentries_message = mActivityContext.getResources().getString(R.string.enter_new_username_error);
            same_entriesmessage = mActivityContext.getResources().getString(R.string.same_username_entered_error);
            compareValue = JoshApplication.username(mActivityContext);
        }
        else if(layoutType == 1){
            noentries_message = mActivityContext.getResources().getString(R.string.enter_new_password_error);
            same_entriesmessage = mActivityContext.getResources().getString(R.string.same_password_entered_error);
            compareValue = JoshApplication.password(mActivityContext);
        }
        else if(layoutType == 2){
            noentries_message = mActivityContext.getResources().getString(R.string.enter_new_email_error);
            same_entriesmessage = mActivityContext.getResources().getString(R.string.same_email_entered_error);
            compareValue = JoshApplication.email(mActivityContext);
        }
        if(newValue.equalsIgnoreCase("")){
            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.fill_all_entries_error_title),
                    noentries_message);
            return false;
        }
        else if(layoutType !=1 && newValue.equalsIgnoreCase(compareValue)){
            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.fill_all_entries_error_title),
                    same_entriesmessage);
            return false;
        }
        else if(layoutType ==1 && newValue.equals(compareValue)){
            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.fill_all_entries_error_title),
                    same_entriesmessage);
            return false;
        }
        return true;
    }

    private void hideUpdateProfiledataLayout(){
        layoutEdtProfile.setVisibility(View.GONE);
        btnTogglePasswordVisibility.setVisibility(View.GONE);
    }

    private void updatePasswordApiCall(){
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.updating_password));
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getChangePasswordApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), JoshApplication.password(mActivityContext), edtProfileData.getText().toString().trim()).enqueue(new Callback<JoshGenericOnlyStatusResponseData>() {
                @Override
                public void onResponse(Call<JoshGenericOnlyStatusResponseData> call, Response<JoshGenericOnlyStatusResponseData> response) {
                    if (response.isSuccessful()) {
                        final JoshGenericOnlyStatusResponseData mChangePasswordResponseData = response.body();
                        if(mChangePasswordResponseData.getmStatus().equalsIgnoreCase(JoshGenericOnlyStatusResponseData.OK_STATUS)) {
                            JoshApplication.savePassword(edtProfileData.getText().toString().trim());
                            hideUpdateProfiledataLayout();
                            setPasswordText(lblPasswordVal, JoshApplication.password(mActivityContext).length());
                        }
                        else{
                            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.something_went_wromg),
                                    mChangePasswordResponseData.getmStatus());
                        }
                        //JoshApplication.toast(mActivityContext, "Buyin success. got to webview");
                        JoshApplication.closeSpinnerProgress(mActivityContext);

                    }
                    else{
                        JoshApplication.closeSpinnerProgress(mActivityContext);
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
                        JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                errorMsg);

                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                    }
                }
                @Override
                public void onFailure(Call<JoshGenericOnlyStatusResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getChangePasswordApiCall UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                            mActivityContext.getResources().getString(R.string.server_not_responding));

                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JoshApplication.closeErrorDialog(mActivityContext);
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
                        JoshApplication.closeErrorDialog(mActivityContext);
                }
            });
        }
    }

    private void updateProfileusernameDataApiCall(final String username){
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.updating_profiledata));
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getUpdateProfileUsernameApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext),  username).enqueue(new Callback<JoshGenericOnlyStatusResponseData>() {
                @Override
                public void onResponse(Call<JoshGenericOnlyStatusResponseData> call, Response<JoshGenericOnlyStatusResponseData> response) {
                    if (response.isSuccessful()) {
                        final JoshGenericOnlyStatusResponseData mChangeUsernameResponseData = response.body();
                        if(mChangeUsernameResponseData.getmStatus().equalsIgnoreCase(JoshGenericOnlyStatusResponseData.OK_STATUS)) {
                            JoshApplication.saveUsername(username);
                            hideUpdateProfiledataLayout();
                            lblUsernameVal.setText(JoshApplication.username(mActivityContext));
                        }
                        else{
                            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.something_went_wromg),
                                    mChangeUsernameResponseData.getmStatus());
                        }
                        //JoshApplication.toast(mActivityContext, "Buyin success. got to webview");
                        JoshApplication.closeSpinnerProgress(mActivityContext);

                    }
                    else{
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg, errorCode;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            errorCode = errorResponseObj.getmErrorCode();
                            Log.d(TAG, "getUpdateProfileUsernameApiCall UnSuccessfull - " + response.errorBody());
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                errorMsg);

                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                    }
                }
                @Override
                public void onFailure(Call<JoshGenericOnlyStatusResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getUpdateProfileUsernameApiCall UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                            mActivityContext.getResources().getString(R.string.server_not_responding));

                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JoshApplication.closeErrorDialog(mActivityContext);
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
                    JoshApplication.closeErrorDialog(mActivityContext);
                }
            });
        }
    }

    private void updateProfileEmailDataApiCall(final String email){
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.updating_profiledata));
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getUpdateProfileEmailApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext),  email).enqueue(new Callback<JoshGenericOnlyStatusResponseData>() {
                @Override
                public void onResponse(Call<JoshGenericOnlyStatusResponseData> call, Response<JoshGenericOnlyStatusResponseData> response) {
                    if (response.isSuccessful()) {
                        final JoshGenericOnlyStatusResponseData mChangeEmailIdResponseData = response.body();
                        if(mChangeEmailIdResponseData.getmStatus().equalsIgnoreCase(JoshGenericOnlyStatusResponseData.OK_STATUS)) {
                            JoshApplication.saveEmail(email);
                            hideUpdateProfiledataLayout();
                            lblEmailIDVal.setText(JoshApplication.email(mActivityContext));
                        }
                        else{
                            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.something_went_wromg),
                                    mChangeEmailIdResponseData.getmStatus());
                        }
                        //JoshApplication.toast(mActivityContext, "Buyin success. got to webview");
                        JoshApplication.closeSpinnerProgress(mActivityContext);

                    }
                    else{
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg, errorCode;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            errorCode = errorResponseObj.getmErrorCode();
                            Log.d(TAG, "getUpdateProfileEmailApiCall UnSuccessfull - " + response.errorBody());
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                errorMsg);

                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                    }
                }
                @Override
                public void onFailure(Call<JoshGenericOnlyStatusResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getUpdateProfileEmailApiCall UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                            mActivityContext.getResources().getString(R.string.server_not_responding));

                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JoshApplication.closeErrorDialog(mActivityContext);
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
                    JoshApplication.closeErrorDialog(mActivityContext);
                }
            });
        }
    }

    private void loadProfileDetails(){
        if(JoshApplication.progressDialog!=null && JoshApplication.progressDialog.isShowing()){
            JoshApplication.changeSpinnerProgressDialogMessage( mActivityContext.getResources().getString(R.string.loading_profile_details));
        }
        else {
            JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.loading_profile_details));
        }
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getProfileDatasApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext)).enqueue(new Callback<JoshProfileDetailsResponseData>() {
                @Override
                public void onResponse(Call<JoshProfileDetailsResponseData> call, Response<JoshProfileDetailsResponseData> response) {
                    if (response.isSuccessful()) {
                        final JoshProfileDetailsResponseData mJoshProfileDetailsResponseData = response.body();
                        JoshApplication.savePhoneno(mJoshProfileDetailsResponseData.getmPhoneNumber());
                        JoshApplication.saveEmail(mJoshProfileDetailsResponseData.getmEmail());
                        JoshApplication.saveProfilePicUrl(mJoshProfileDetailsResponseData.getmProfilePicUrl());
                        JoshApplication.saveIsBankAccountVerfied(mJoshProfileDetailsResponseData.ismIsBankAcctVerified());
                        JoshApplication.saveIsKYCVerfied(mJoshProfileDetailsResponseData.ismIsKycVerified());
                        initUI();
                        if(mJoshProfileDetailsResponseData.getmBankAccountNo() != null) {
                            lblBankAccountValue.setText(mJoshProfileDetailsResponseData.getmBankAccountNo());
                            lblEditBankAcctext.setText(getResources().getString(R.string.view));
                        }
                        else{
                            lblBankAccountValue.setText(getResources().getString(R.string.not_available));
                            lblEditBankAcctext.setText(getResources().getString(R.string.add));
                        }
                        if(mJoshProfileDetailsResponseData.getmKycs() != null && mJoshProfileDetailsResponseData.getmKycs().size()>0) {
                            lblKycDetailsValue.setText(mJoshProfileDetailsResponseData.getmKycs().get(0).split("-")[0]);
                            lblEdtKycText.setText(getResources().getString(R.string.view));
                        }
                        else{
                            lblKycDetailsValue.setText(getResources().getString(R.string.not_available));
                            lblEdtKycText.setText(getResources().getString(R.string.add));
                        }
                        if(mJoshProfileDetailsResponseData.getmProfilePicUrl()!=null){
                            Glide.with(mActivityContext).load(mJoshProfileDetailsResponseData.getmProfilePicUrl())
                                    .addListener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            Log.d(TAG, "fail- " + e.getMessage());
                                            imgProfile.setImageResource(JoshApplication.DEFAULT_PROFILE_PIC);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            Log.d(TAG, "reosurce ready");
                                            //enableUI();
                                            return false;
                                        }
                                    })
                                    .placeholder(JoshApplication.DEFAULT_PROFILE_PIC)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(imgProfile);
                        }
                        else{
                            imgProfile.setImageResource(JoshApplication.DEFAULT_PROFILE_PIC);
                        }
                        //JoshApplication.toast(mActivityContext, "Buyin success. got to webview");
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                    }
                    else{
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg, errorCode;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            errorCode = errorResponseObj.getmErrorCode();
                            Log.d(TAG, "getProfileDatasApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                errorMsg);

                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                        JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                    }
                }
                @Override
                public void onFailure(Call<JoshProfileDetailsResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getProfileDatasApiCall UnSuccessfull - " + t.getLocalizedMessage());
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

    private void showChooseImageOption(){
        AlertDialog.Builder setImage = new AlertDialog.Builder(mActivityContext);
        // setImage.setTitle("Image for Transaction");
        setImage.setItems(availableChooseProfileImgOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){ //camera
                    askCameraPermissions();
                }
                else if(which==1){ //Gallery
                    askStoragePermissions();
                }
            }
        });
        setImage.create();
        setImage.show();
    }

    public void askStoragePermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mActivityContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivityContext);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getResources().getString(R.string.ask_storage_permission_message));
                    builder.setPositiveButton(getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivityContext);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getResources().getString(R.string.ask_storage_permission_message));
                    builder.setPositiveButton(getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", mActivityContext.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            JoshApplication.customisedtoast(mActivityContext, getResources().getString(R.string.go_to_permissions_to_grant_storage), Toast.LENGTH_LONG);
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                }

                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                editor.commit();


            } else {
                //You already have the permission, just go ahead.
                selectImageFromGallery();
                //Intent mapIntent = new Intent(MainActivity.this,MapActivity.class);
                //startActivityForRe;sult(mapIntent,0);
            }
        }
        else{
            //Not needed for asking permissions below MarshMallow
            selectImageFromGallery();
        }
    }

    public void askCameraPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mActivityContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.CAMERA)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivityContext);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getResources().getString(R.string.ask_camera_permission_message));
                    builder.setPositiveButton(getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CONSTANT);
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (permissionStatus.getBoolean(Manifest.permission.CAMERA, false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivityContext);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getResources().getString(R.string.ask_camera_permission_message));
                    builder.setPositiveButton(getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", mActivityContext.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            JoshApplication.customisedtoast(mActivityContext, getResources().getString(R.string.go_to_permissions_to_grant_storage), Toast.LENGTH_LONG);
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CONSTANT);
                }

                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(Manifest.permission.CAMERA, true);
                editor.commit();


            } else {
                //You already have the permission, just go ahead.
                captureImage();
                //Intent mapIntent = new Intent(MainActivity.this,MapActivity.class);
                //startActivityForRe;sult(mapIntent,0);
            }
        }
        else{
            //Not needed for asking permissions below MarshMallow
            captureImage();
        }
    }

    private void selectImageFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , GALLERY_REQUEST_CODE);//one can be replaced with any action code
    }

    private void captureImage(){
        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
        imagesFolder.mkdirs();
        File image = new File(imagesFolder, "joshcallbreak_"+System.currentTimeMillis()+"profileimage.jpg");
        // uriSavedImage = Uri.fromFile(image);
        //FOr above Android 24...below code
        imagePath = image.getAbsolutePath();
        uriSavedImage = FileProvider.getUriForFile(mActivityContext, "com.vume.allinonegames" + ".GenericFileProvider", image);;
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(mActivityContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void setImageFromPath(){

        Glide.with(mActivityContext).load(imagePath)
                .placeholder(R.drawable.icon_mushprofile)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "fail- " + e.getMessage());
                        imgProfile.setImageResource(JoshApplication.DEFAULT_PROFILE_PIC);
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
                .into(imgProfile);
//        File file = new File(imagePath);
//        if (file.exists()) {
//            deleteAccntImage.setVisibility(View.VISIBLE);
//            viewFullAccntImage.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults[0] == 0){
            if(permissions[0].equalsIgnoreCase("android.permission.CAMERA")){
                captureImage();
            }
            else if(permissions[0].equalsIgnoreCase("android.permission.WRITE_EXTERNAL_STORAGE")){
                selectImageFromGallery();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //Uri selectedImage = uriSavedImage;
                    Log.d(TAG, "getpath CAMERA - " + imagePath);
                    //imagePath = (selectedImage.getPath());
                    JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.updating_profiledata));
                    new uploadProfilePicAsync().execute();
                    //uploadProfilePicture();
                }

                break;
            case GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    Log.d(TAG, "getpath GALLERY - " + getRealPathFromURI(selectedImage));
                    imagePath = getRealPathFromURI(selectedImage);
                    JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.updating_profiledata));
                    new uploadProfilePicAsync().execute();
                    //uploadProfilePicture();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
    }

    @Override
    public void onBackPressed() {
        if(layoutEdtProfile.getVisibility() == View.VISIBLE){
            hideUpdateProfiledataLayout();
        }
        else {
            super.onBackPressed();
            overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
        }
    }

    private void uploadProfilePicture() {
        // create upload service client
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath,bmOptions);
        bitmap = Bitmap.createBitmap(bitmap);
        File fixRotationImg = JoshApplication.persistImage(mActivityContext,JoshApplication.fixOrientation(imagePath, bitmap),
                JoshApplication.username(mActivityContext)+"_ProfilePic");

        File kycImgFile, kycImgUnresizedFile;
        if(fixRotationImg != null) {
            kycImgUnresizedFile = fixRotationImg;
        }
        else{
            kycImgUnresizedFile = new File(imagePath);
        }

        try {
            kycImgFile = new Resizer(this)
                    .setTargetLength(1080)
                    .setQuality(60)
                    .setOutputFilename(JoshApplication.username(mActivityContext)+"_ProfilePic")
                    .setSourceImage(kycImgUnresizedFile)
                    .getResizedFile();
        } catch (IOException e) {
            kycImgFile = kycImgUnresizedFile;
            e.printStackTrace();
        }

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/png"), kycImgFile);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part profilePic =
                MultipartBody.Part.createFormData("profilePic", kycImgFile.getName(), requestFile);

        // add another part within the multipart request
        RequestBody idTypeRequestBody =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, JoshApplication.username(mActivityContext)+"_ProfilePic");

        // finally, execute the request
        Call<ResponseBody> call = myApiInterface.uploadProfilePic(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                JoshApplication.currentLoginAuthTokenFromServer(mActivityContext),  profilePic);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    loadProfileDetails();
                    Log.v("Upload Profie Pic:", "success");
                }
                else{
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                    String errorMsg;
                    if(errorResponseObj!=null){
                        errorMsg = errorResponseObj.getmMessage();
                    }
                    else{
                        errorMsg = getResources().getString(R.string.kyc_upload_failed_desc);
                    }
                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.upload_failed_heading),
                            errorMsg);

                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                    Log.v("Upload Profie Pic:", response +"---"+errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload Profie Pic:", t.getMessage());
                JoshApplication.closeSpinnerProgress(mActivityContext);
                JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.upload_failed_heading),
                        getResources().getString(R.string.kyc_upload_failed_desc));

                JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
            }
        });
    }

    public class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        public RotateTransformation(Context context, float rotateRotationAngle) {
            super();

            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();

            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
            messageDigest.update(("rotate" + rotateRotationAngle).getBytes());
        }
    }

    private final class uploadProfilePicAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            uploadProfilePicture();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

}
