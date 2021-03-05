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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
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
import com.vume.allinonegames.ApiCalls.JoshRummyServicesInterface;
import com.vume.allinonegames.ApiCalls.MyRetrofitInstance;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshKycResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.echodev.resizer.Resizer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Bundling of uploading both front and back side of kyc in a single server call will be difficult to handle for the common platform we have.
// As, We are using the same system for another web based product also.

//TODO: Once both front and back side of kyc uploading will be done in a single server call, have to clean and modify the code lot better

public class UpdateKycActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = UpdateKycActivity.class.getName();
    Context mActivityContext;

    private SharedPreferences permissionStatus;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 982;
    private static final int CAMERA_PERMISSION_CONSTANT = 421;
    private static final int REQUEST_PERMISSION_SETTING = 893;
    private static final int CAMERA_REQUEST_CODE = 753;
    private static final int GALLERY_REQUEST_CODE = 231;
    private static final String RUMMY_PERMISSION_STATUS = "joshrummypermissionstatus_callbreak";

    private static final String AADHAAR_FRONT_KYC_KEY = "aadhaar-front";
    private static final String AADHAAR_BACK_KYC_KEY = "aadhaar-back";
    private static final String PANCARD_FRONT_KYC_KEY = "pan-card-front";
    private static final String PANCARD_BACK_KYC_KEY = "pan-card-back";
    private static final String BANKSLIP_FRONT_KYC_KEY = "bank-slip-front";
    private static final String BANKSLIP_BACK_KYC_KEY = "bank-slip-back";
    private static final String VOTERID_FRONT_KYC_KEY = "voter-id-front";
    private static final String VOTERID_BACK_KYC_KEY = "voter-id-back";
    private static final String DRIVINGLICENCE_FRONT_KYC_KEY = "drivers-license-front";
    private static final String DRIVINGLICENCE_BACK_KYC_KEY = "drivers-license-back";
    private static final String PASSPORT_FRONT_KYC_KEY = "passport-front";
    private static final String PASSPORT_BACK_KYC_KEY = "passport-back";
    private static final String LOADING_FRONTKYCIMAGE = "front";
    private static final String LOADING_BACKKYCIMAGE = "back";

    String currentLoadingImageSide = LOADING_FRONTKYCIMAGE;
    Uri uriSavedImage;
    private String frontImagePath = null, backImagePath = null;
    private String[] availableChooseProfileImgOption;

    private ImageView btnBack, imgFrontOfKYC, imgBackOfKYC, imgViewToLoad;
    private Button btnUploadFrontKycImg, btnUploadBackKycImg, btnUploadKycToServer;
    private TextView btnShowUploadAadhaarContent,  btnShowUploadDrivingLicenceContent, btnShowUploadVoterIdContent, btnShowUploadPassportContent;
    private View btnEditFrontKYC, btnEditBackKYC;
    private Group grpFrontKYCEdit, grpBackKYCEdit;
    private TextView txtKycStatus, txtFrontViewHeading, txtBackViewHeading;

    private List<JoshKycResponseData> joshKycdataDataList = new ArrayList<>();

    public String frontkycIdTypeUploading = AADHAAR_FRONT_KYC_KEY;
    public String backkycIdTypeUploading = AADHAAR_BACK_KYC_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_update_kyc);

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
        permissionStatus = getSharedPreferences(RUMMY_PERMISSION_STATUS,MODE_PRIVATE);
        initUI();
        getKycDetails();
    }

    private void initUI(){
        availableChooseProfileImgOption = getResources().getStringArray(R.array.selectprofileimgoption_array);

        btnBack = findViewById(R.id.btn_back);
        imgFrontOfKYC = findViewById(R.id.front_originalimage);
        imgBackOfKYC = findViewById(R.id.back_originalimage);

        txtKycStatus = findViewById(R.id.txtKycStatus);
        txtFrontViewHeading = findViewById(R.id.frontview_heading);
        txtBackViewHeading = findViewById(R.id.backview_heading);

        btnUploadBackKycImg = findViewById(R.id.btn_choosebackphoto);
        btnUploadFrontKycImg = findViewById(R.id.btn_choosefrontphoto);
        btnEditBackKYC = findViewById(R.id.btn_edit_backkyc);
        btnEditFrontKYC = findViewById(R.id.btn_edit_frontkyc);
        btnUploadKycToServer = findViewById(R.id.btn_submitkyc);

        btnShowUploadAadhaarContent = findViewById(R.id.btn_showuploadaadhaarcontent);
        btnShowUploadDrivingLicenceContent = findViewById(R.id.btn_showuploaddrivinglicencecontent);
        btnShowUploadVoterIdContent = findViewById(R.id.btn_showuploadvoteridcontent);
        btnShowUploadPassportContent = findViewById(R.id.btn_showuploadpassportcontent);

        grpBackKYCEdit = findViewById(R.id.grpBackKycEdit);
        grpFrontKYCEdit = findViewById(R.id.grpFrontKycEdit);

        btnBack.setOnClickListener(this);
        btnUploadFrontKycImg.setOnClickListener(this);
        btnUploadBackKycImg.setOnClickListener(this);
        imgFrontOfKYC.setOnClickListener(this);
        imgBackOfKYC.setOnClickListener(this);
        btnEditFrontKYC.setOnClickListener(this);
        btnEditBackKYC.setOnClickListener(this);
        btnUploadKycToServer.setOnClickListener(this);
        btnShowUploadAadhaarContent.setOnClickListener(this);
        btnShowUploadDrivingLicenceContent.setOnClickListener(this);
        btnShowUploadVoterIdContent.setOnClickListener(this);
        btnShowUploadPassportContent.setOnClickListener(this);
    }

    private void getKycDetails() {
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.loading_kyc_details));
        if (JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getKycDetailsApiCall(getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext)).
                    enqueue(new Callback<List<JoshKycResponseData>>() {
                        @Override
                        public void onResponse(Call<List<JoshKycResponseData>> call, Response<List<JoshKycResponseData>> response) {
                            if (response.isSuccessful()) {
                                joshKycdataDataList = response.body();
                                updateKycDetailsUI();
                                //JoshApplication.toast(mActivityContext, "Buyin success. got to webview");
                                //JoshApplication.closeSpinnerProgress(mActivityContext);

                            } else {
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                                String errorMsg, errorCode;
                                if (errorResponseObj != null) {
                                    errorMsg = errorResponseObj.getmMessage();
                                    errorCode = errorResponseObj.getmErrorCode();
                                    Log.d(TAG, "getKycDetailsApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                                } else {
                                    errorMsg = getResources().getString(R.string.server_not_responding);
                                    errorCode = String.valueOf(response.code());
                                }

                                if(!errorCode.equalsIgnoreCase(ErrorUtils.MISSING_KYC_ERRORCODE)) {//No error dialogs for no kyc found errors
                                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                            errorMsg);
                                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<JoshKycResponseData>> call, Throwable t) {
                            JoshApplication.closeSpinnerProgress(mActivityContext);
                            Log.d(TAG, "getChangePasswordApiCall UnSuccessfull - " + t.getLocalizedMessage());
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

    private void updateKycDetailsUI(){
        switch (frontkycIdTypeUploading){
            case AADHAAR_FRONT_KYC_KEY:
                makeKYCMenuSelection(AADHAAR_FRONT_KYC_KEY);
                updateEachKYCUI(AADHAAR_FRONT_KYC_KEY, AADHAAR_BACK_KYC_KEY);
                break;

            case VOTERID_FRONT_KYC_KEY:
                makeKYCMenuSelection(VOTERID_FRONT_KYC_KEY);
                updateEachKYCUI(VOTERID_FRONT_KYC_KEY, VOTERID_BACK_KYC_KEY);
                break;

            case DRIVINGLICENCE_FRONT_KYC_KEY:
                makeKYCMenuSelection(DRIVINGLICENCE_FRONT_KYC_KEY);
                updateEachKYCUI(DRIVINGLICENCE_FRONT_KYC_KEY, DRIVINGLICENCE_BACK_KYC_KEY);
                break;

            case PASSPORT_FRONT_KYC_KEY:
                makeKYCMenuSelection(PASSPORT_FRONT_KYC_KEY);
                updateEachKYCUI(PASSPORT_FRONT_KYC_KEY, PASSPORT_BACK_KYC_KEY);
                break;
        }
    }

    private void updateEachKYCUI(String frontKYCKey, String backKYCKey){
        boolean isKycUploaded = false;
        for(JoshKycResponseData kycResponseData : joshKycdataDataList){
            if(kycResponseData.getIdType().equalsIgnoreCase(frontKYCKey)){
                isKycUploaded = true;
                if(kycResponseData.isKYCApproved()){
                    txtKycStatus.setBackgroundColor(getResources().getColor(R.color.switch_green));
                    txtKycStatus.setText(getResources().getString(R.string.kyc_status_verified));
                }
                else{
                    txtKycStatus.setBackgroundColor(getResources().getColor(R.color.orangy_yellow_light));
                    txtKycStatus.setText(getResources().getString(R.string.kyc_status_beingverified));
                }
                txtKycStatus.setVisibility(View.VISIBLE);
                imgFrontOfKYC.setVisibility(View.VISIBLE);
                Glide.with(mActivityContext).load(kycResponseData.getIdFileURL())
                        .placeholder(R.mipmap.id_front)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                imgFrontOfKYC.setImageResource(JoshApplication.DEFAULT_KYC_FRONTPIC);
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                return false;
                            }
                        })
                        .into(imgFrontOfKYC);

            }
            else if(kycResponseData.getIdType().equalsIgnoreCase(backKYCKey)){
                isKycUploaded = true;
                if(kycResponseData.isKYCApproved()){
                    txtKycStatus.setBackgroundColor(getResources().getColor(R.color.switch_green));
                    txtKycStatus.setText(getResources().getString(R.string.kyc_status_verified));
                }
                else{
                    txtKycStatus.setBackgroundColor(getResources().getColor(R.color.orangy_yellow_light));
                    txtKycStatus.setText(getResources().getString(R.string.kyc_status_beingverified));
                }
                txtKycStatus.setVisibility(View.VISIBLE);
                imgBackOfKYC.setVisibility(View.VISIBLE);
                Glide.with(mActivityContext).load(kycResponseData.getIdFileURL())
                        .placeholder(R.mipmap.id_back)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                imgFrontOfKYC.setImageResource(JoshApplication.DEFAULT_KYC_BACKPIC);
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                return false;
                            }
                        })
                        .into(imgBackOfKYC);
            }
        }
        if(!isKycUploaded){ //IF THIS KYC IS NOT YET UPLOADED
            txtKycStatus.setVisibility(View.GONE);
            imgFrontOfKYC.setVisibility(View.GONE);
            imgBackOfKYC.setVisibility(View.GONE);
        }
        //EVRYTIME KYC IS SWITCHED ALL OF THIS SHOULD HIDE
        btnUploadKycToServer.setVisibility(View.GONE);
        grpFrontKYCEdit.setVisibility(View.GONE);
        grpBackKYCEdit.setVisibility(View.GONE);
        JoshApplication.closeSpinnerProgress(mActivityContext);
    }

    private void makeKYCMenuSelection(String currentFrontKycKey){
        switch (frontkycIdTypeUploading){
            case AADHAAR_FRONT_KYC_KEY:
                btnShowUploadAadhaarContent.setBackgroundResource(R.drawable.red_highroundedcorner_bg);
                btnShowUploadVoterIdContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadPassportContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadDrivingLicenceContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadAadhaarContent.setTextColor(getResources().getColor(R.color.white));
                btnShowUploadVoterIdContent.setTextColor(getResources().getColor(R.color.orangydark));
                btnShowUploadPassportContent.setTextColor(getResources().getColor(R.color.orangydark));
                btnShowUploadDrivingLicenceContent.setTextColor(getResources().getColor(R.color.orangydark));

                txtFrontViewHeading.setText(getResources().getText(R.string.frontof_aadhaar));
                txtBackViewHeading.setText(getResources().getText(R.string.backof_aadhaar));
                break;

            case VOTERID_FRONT_KYC_KEY:
                btnShowUploadAadhaarContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadVoterIdContent.setBackgroundResource(R.drawable.red_highroundedcorner_bg);
                btnShowUploadPassportContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadDrivingLicenceContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadAadhaarContent.setTextColor(getResources().getColor(R.color.orangydark));
                btnShowUploadVoterIdContent.setTextColor(getResources().getColor(R.color.white));
                btnShowUploadPassportContent.setTextColor(getResources().getColor(R.color.orangydark));
                btnShowUploadDrivingLicenceContent.setTextColor(getResources().getColor(R.color.orangydark));

                txtFrontViewHeading.setText(getResources().getText(R.string.frontof_voterid));
                txtBackViewHeading.setText(getResources().getText(R.string.backof_voterid));
                break;

            case DRIVINGLICENCE_FRONT_KYC_KEY:
                btnShowUploadAadhaarContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadVoterIdContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadPassportContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadDrivingLicenceContent.setBackgroundResource(R.drawable.red_highroundedcorner_bg);
                btnShowUploadAadhaarContent.setTextColor(getResources().getColor(R.color.orangydark));
                btnShowUploadVoterIdContent.setTextColor(getResources().getColor(R.color.orangydark));
                btnShowUploadPassportContent.setTextColor(getResources().getColor(R.color.orangydark));
                btnShowUploadDrivingLicenceContent.setTextColor(getResources().getColor(R.color.white));

                txtFrontViewHeading.setText(getResources().getText(R.string.frontof_drivinglicence));
                txtBackViewHeading.setText(getResources().getText(R.string.backof_drivinglicence));
                break;

            case PASSPORT_FRONT_KYC_KEY:
                btnShowUploadAadhaarContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadVoterIdContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadPassportContent.setBackgroundResource(R.drawable.red_highroundedcorner_bg);
                btnShowUploadDrivingLicenceContent.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
                btnShowUploadAadhaarContent.setTextColor(getResources().getColor(R.color.orangydark));
                btnShowUploadVoterIdContent.setTextColor(getResources().getColor(R.color.orangydark));
                btnShowUploadPassportContent.setTextColor(getResources().getColor(R.color.white));
                btnShowUploadDrivingLicenceContent.setTextColor(getResources().getColor(R.color.orangydark));

                txtFrontViewHeading.setText(getResources().getText(R.string.frontof_passport));
                txtBackViewHeading.setText(getResources().getText(R.string.backof_passport));
                break;
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(UpdateKycActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivityContext);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getResources().getString(R.string.ask_storage_permission_message));
                    builder.setPositiveButton(getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(UpdateKycActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
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
                    ActivityCompat.requestPermissions(UpdateKycActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(UpdateKycActivity.this, Manifest.permission.CAMERA)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivityContext);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getResources().getString(R.string.ask_camera_permission_message));
                    builder.setPositiveButton(getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(UpdateKycActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CONSTANT);
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
                    ActivityCompat.requestPermissions(UpdateKycActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CONSTANT);
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
        if(currentLoadingImageSide.equalsIgnoreCase(LOADING_FRONTKYCIMAGE)) {
            frontImagePath = image.getAbsolutePath();
        }
        else{
            backImagePath = image.getAbsolutePath();
        }
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
        imgViewToLoad.setVisibility(View.VISIBLE);
        final String loadImgPath;
        final int placeholderImg;
        if(currentLoadingImageSide.equalsIgnoreCase(LOADING_FRONTKYCIMAGE)){
            loadImgPath = frontImagePath;
            placeholderImg = R.mipmap.id_front;
        }
        else{
            loadImgPath = backImagePath;
            placeholderImg = R.mipmap.id_back;
        }
        Glide.with(mActivityContext).load(loadImgPath)
                .placeholder(placeholderImg)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "fail- " + e.getMessage());
                        imgViewToLoad.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "reosurce ready");
                        if(imgViewToLoad == imgFrontOfKYC){
                            grpFrontKYCEdit.setVisibility(View.VISIBLE);
                        }
                        else if(imgViewToLoad ==imgBackOfKYC){
                            grpBackKYCEdit.setVisibility(View.VISIBLE);
                        }
                        if(isBothSideKycLoaded()){ //BOTH KYC UPLOADED
                            btnUploadKycToServer.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }

                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgViewToLoad);
//        File file = new File(imagePath);
//        if (file.exists()) {
//            deleteAccntImage.setVisibility(View.VISIBLE);
//            viewFullAccntImage.setVisibility(View.VISIBLE);
//        }
    }

    private boolean isBothSideKycLoaded(){
        return (grpBackKYCEdit.getVisibility() == View.VISIBLE && grpFrontKYCEdit.getVisibility() == View.VISIBLE);
    }

    private void uploadKYCFile(Context ctx, String apiKey, String authResultUserIdToken, String idType, String kycImgFilePath, final boolean havetoUploadBackSide) {
        // create upload service client
        JoshRummyServicesInterface myApiInterface = MyRetrofitInstance.getRetrofitInstance().create(JoshRummyServicesInterface.class);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(kycImgFilePath,bmOptions);
        bitmap = Bitmap.createBitmap(bitmap);
        File fixRotationImg = JoshApplication.persistImage(mActivityContext,JoshApplication.fixOrientation(kycImgFilePath, bitmap),
                idType);

        File kycImgFile, kycImgUnresizedFile;
        if(fixRotationImg != null) {
            kycImgUnresizedFile = fixRotationImg;
        }
        else{
            kycImgUnresizedFile = new File(kycImgFilePath);
        }

        try {
            kycImgFile = new Resizer(this)
                    .setTargetLength(1080)
                    .setQuality(60)
                    .setOutputFilename(idType)
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
        MultipartBody.Part kycFile =
                MultipartBody.Part.createFormData("idFile", kycImgFile.getName(), requestFile);

        // add another part within the multipart request
        RequestBody idTypeRequestBody =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, idType);

        // finally, execute the request
        Call<ResponseBody> call = myApiInterface.uploadKycDocument(apiKey, authResultUserIdToken, idTypeRequestBody, kycFile);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    if(havetoUploadBackSide){//Coming here after front kyc uploaded
                        uploadKYCFile(mActivityContext, mActivityContext.getResources().getString(R.string.josh_server_api_key),
                                JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), backkycIdTypeUploading, backImagePath , false);
                    }
                    else{
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        btnUploadKycToServer.setVisibility(View.GONE);
                        btnEditFrontKYC.setVisibility(View.GONE);
                        btnEditBackKYC.setVisibility(View.GONE);
                        txtKycStatus.setVisibility(View.VISIBLE);
                        JoshApplication.toast(mActivityContext, getResources().getString(R.string.kyc_upload_done));
                        getKycDetails();
                    }
                    Log.v("Upload KYC", "success");
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
                    imgViewToLoad.setVisibility(View.GONE);
                    Log.v("Upload KYC failed", response +"---"+errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload KYC error:", t.getMessage());
                JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.upload_failed_heading),
                        getResources().getString(R.string.kyc_upload_failed_desc));

                JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                imgViewToLoad.setVisibility(View.GONE);
            }
        });
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
                    if(currentLoadingImageSide.equalsIgnoreCase(LOADING_FRONTKYCIMAGE)) {
                        Log.d(TAG, "getpath CAMERA Front Imagepath- " + frontImagePath);
                    }
                    else{
                        Log.d(TAG, "getpath CAMERA Front Imagepath- " + backImagePath);
                    }
                    //imagePath = (selectedImage.getPath());
                    setImageFromPath();
                }

                break;
            case GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    Log.d(TAG, "getpath GALLERY - " + getRealPathFromURI(selectedImage));
                    if(currentLoadingImageSide.equalsIgnoreCase(LOADING_FRONTKYCIMAGE)) {
                        frontImagePath = getRealPathFromURI(selectedImage);
                    }
                    else{
                        backImagePath = getRealPathFromURI(selectedImage);
                    }
                    setImageFromPath();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_choosefrontphoto:
                imgViewToLoad = imgFrontOfKYC;
                currentLoadingImageSide = LOADING_FRONTKYCIMAGE;
                showChooseImageOption();
                break;

            case R.id.btn_choosebackphoto:
                currentLoadingImageSide = LOADING_BACKKYCIMAGE;
                imgViewToLoad = imgBackOfKYC;
                showChooseImageOption();
                break;

            case R.id.btn_edit_frontkyc:
                currentLoadingImageSide = LOADING_FRONTKYCIMAGE;
                imgViewToLoad = imgFrontOfKYC;
                showChooseImageOption();
                break;

            case R.id.btn_edit_backkyc:
                currentLoadingImageSide = LOADING_BACKKYCIMAGE;
                imgViewToLoad = imgBackOfKYC;
                showChooseImageOption();
                break;

            case R.id.btn_submitkyc:
                if(JoshApplication.isInternetAvailable(mActivityContext)) {
                    JoshApplication.startSpinnerProgress(mActivityContext, getResources().getString(R.string.uploading_kyc_doc));
                    new uploadKYCAsync().execute();
//                    uploadKYCFile(mActivityContext, mActivityContext.getResources().getString(R.string.josh_server_api_key),
//                            JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), frontkycIdTypeUploading, frontImagePath, true);
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
                break;

            case R.id.btn_showuploadaadhaarcontent:
                frontkycIdTypeUploading = AADHAAR_FRONT_KYC_KEY;
                backkycIdTypeUploading = AADHAAR_BACK_KYC_KEY;
                updateKycDetailsUI();
                break;

            case R.id.btn_showuploaddrivinglicencecontent:
                frontkycIdTypeUploading = DRIVINGLICENCE_FRONT_KYC_KEY;
                backkycIdTypeUploading = DRIVINGLICENCE_BACK_KYC_KEY;
                updateKycDetailsUI();
                break;

            case R.id.btn_showuploadpassportcontent:
                frontkycIdTypeUploading = PASSPORT_FRONT_KYC_KEY;
                backkycIdTypeUploading = PASSPORT_BACK_KYC_KEY;
                updateKycDetailsUI();
                break;

            case R.id.btn_showuploadvoteridcontent:
                frontkycIdTypeUploading = VOTERID_FRONT_KYC_KEY;
                backkycIdTypeUploading = VOTERID_BACK_KYC_KEY;
                updateKycDetailsUI();
                break;
        }
    }

    private final class uploadKYCAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            uploadKYCFile(mActivityContext, mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), frontkycIdTypeUploading, frontImagePath, true);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}
