package com.vume.allinonegames;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import me.echodev.resizer.Resizer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.vume.allinonegames.ApiCalls.JoshRummyServicesInterface;
import com.vume.allinonegames.ApiCalls.MyRetrofitInstance;
import com.vume.allinonegames.Model.FirebaseAnalyticsCustomKeys;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshGenericOnlyStatusResponseData;
import com.vume.allinonegames.Model.JoshLoginResponseData;
import com.vume.allinonegames.Model.JoshRegisterResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;
import com.vume.allinonegames.Util.InputValidator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterProfileDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = RegisterProfileDetailsActivity.class.getName();
    private Context mActivityContext;

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

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener dateSelectedListener;

    private ImageView imgSelectMush, imgSelectGoogle, imgSelectSpecs, imgSelectTeen;
    private ImageView imgProfile;
    private EditText edtUsername, edtEmailId, edtDateOfBirth;
    private Button btnRegister;
    private Switch switchRememberme;

    private boolean isUIEnabled = true; //true by default as lock functionality is not needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_register_profile);

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        init();
        permissionStatus = getSharedPreferences(RUMMY_PERMISSION_STATUS,MODE_PRIVATE);
        //disableUIByDefault();
    }

    private void init(){
//        JoshApplication.toast(mActivityContext, getIntent().getExtras().getString("password"));
        myCalendar = Calendar.getInstance();
        imgProfile = findViewById(R.id.img_profileavatar);
        imgSelectMush = findViewById(R.id.img_avatarmush);
        imgSelectGoogle = findViewById(R.id.img_avatargoggle);
        imgSelectSpecs = findViewById(R.id.img_avatarspecs);
        imgSelectTeen = findViewById(R.id.img_avatarteen);

        edtUsername = findViewById(R.id.edt_username);
        edtEmailId = findViewById(R.id.edt_emailid);
        edtDateOfBirth = findViewById(R.id.edt_dateofbirth);

        btnRegister = findViewById(R.id.btn_submitregisterdetails);
        switchRememberme = findViewById(R.id.switch_rememberme);

        switchRememberme.setChecked(JoshApplication.isRememberMeTicked(mActivityContext));
        switchRememberme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JoshApplication.saveRememberMeTick(isChecked);
                switchRememberme.setChecked(isChecked);
            }
        });

        dateSelectedListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        availableChooseProfileImgOption = getResources().getStringArray(R.array.selectprofileimgoption_array);

        btnRegister.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        imgSelectMush.setOnClickListener(this);
        imgSelectGoogle.setOnClickListener(this);
        imgSelectSpecs.setOnClickListener(this);
        imgSelectTeen.setOnClickListener(this);
        edtDateOfBirth.setOnClickListener(this);
    }

    private void disableUIByDefault(){
        isUIEnabled = false;
        edtUsername.setEnabled(false);
        edtEmailId.setEnabled(false);
        edtDateOfBirth.setEnabled(false);
        switchRememberme.setEnabled(false);
        //btnRegister.setEnabled(false);

        btnRegister.setAlpha(0.5f);
    }

    private void enableUI(){
        isUIEnabled = true;
        edtUsername.setEnabled(true);
        edtEmailId.setEnabled(true);
        edtDateOfBirth.setEnabled(true);
        switchRememberme.setEnabled(true);
        btnRegister.setEnabled(true);

        btnRegister.setAlpha(1f);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.img_profileavatar:
                showChooseImageOption();
                break;

            case R.id.img_avatarmush:
                selectFromSelectedProfile(imgSelectMush);
                break;

            case R.id.img_avatargoggle:
                selectFromSelectedProfile(imgSelectGoogle);
                break;

            case R.id.img_avatarspecs:
                selectFromSelectedProfile(imgSelectSpecs);
                break;

            case R.id.img_avatarteen:
                selectFromSelectedProfile(imgSelectTeen);
                break;

            case R.id.btn_submitregisterdetails:
                if(isUIEnabled){
                    //JoshApplication.toast(mActivityContext, "fdsfds");
                    final String usernameTxt = edtUsername.getText().toString().trim();
                final String emailid = edtEmailId.getText().toString().trim();
                final String dateofbirth = edtDateOfBirth.getText().toString().trim();

                if(usernameTxt.equalsIgnoreCase("") || emailid.equalsIgnoreCase("") || dateofbirth.equalsIgnoreCase("")){
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.fill_all_entries_error_title),
                            getResources().getString(R.string.fill_all_entries_error));
                }
                else if(!InputValidator.isValidUsername(usernameTxt)){
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.fill_all_entries_error_title),
                            getResources().getString(R.string.invalid_username_error));
                }
                else if(!InputValidator.isEmailValid(emailid)){
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.enter_valid_email_error_title),
                            getResources().getString(R.string.enter_valid_email_error_msg));
                }
                else{ //VALID ENTRY
                    if(JoshApplication.isInternetAvailable(mActivityContext)) {
                        JoshApplication.startSpinnerProgress(mActivityContext, getResources().getString(R.string.registering_in));
                        final String finalauthCredential = getIntent().getExtras().getString("authCredential");
                        JoshApplication.getRegisterSignUpApiCall(getResources().getString(R.string.josh_server_api_key), getIntent().getExtras().getString("firebaseAuthResultIdToken"),
                                finalauthCredential, JoshApplication.installKey(mActivityContext), edtUsername.getText().toString().trim(), getIntent().getExtras().getString("password")).enqueue(new Callback<JoshRegisterResponseData>() {
                            @Override
                            public void onResponse(Call<JoshRegisterResponseData> call, Response<JoshRegisterResponseData> response) {
                                if(response.isSuccessful()) {
                                    final JoshRegisterResponseData responseRegisterData = response.body();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(responseRegisterData.getmUsername()).build();
                                    JoshApplication.getMyFirebaseUser(mActivityContext).updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){//Firebase user updated with latest name
                                                //JoshApplication.saveUsername(responseRegisterData.getmUsername());
                                                //JoshApplication.savePassword(getIntent().getExtras().getString("password"));
                                                JoshApplication.saveFirebaseCurrentAccessToken(getIntent().getExtras().getString("firebaseAuthResultIdToken"));
                                                JoshApplication.saveCredentials(finalauthCredential);
                                                JoshApplication.saveDeviceId(responseRegisterData.getmDeviceId());
                                                JoshApplication.saveOAuthUserId(responseRegisterData.getmOAuthUserId());
                                                JoshApplication.saveUserId(responseRegisterData.getmUserId());

                                                //TurupApplication.closeSpinnerProgress(mActivityContext);
                                                Log.d(TAG, "getRegisterSignUpApiCall Success - "+JoshApplication.getMyFirebaseUser(mActivityContext).getDisplayName());

                                                //CALLING LOGIN API CALL
                                                JoshApplication.changeSpinnerProgressDialogMessage(getResources().getString(R.string.logging_in_progress));
                                                JoshApplication.getRegisterLoginApiCall(getResources().getString(R.string.josh_server_api_key),
                                                        responseRegisterData.getmUsername(), getIntent().getExtras().getString("password"), JoshApplication.installKey(mActivityContext)).enqueue(new Callback<JoshLoginResponseData>() {
                                                    @Override
                                                    public void onResponse(Call<JoshLoginResponseData> call, Response<JoshLoginResponseData> response) {
                                                        if (response.isSuccessful()) {
                                                            final JoshLoginResponseData turupLoginResponseData = response.body();
                                                            JoshApplication.saveCurrentLoginAuthTokenFromServer(turupLoginResponseData.getmLoginToken());
                                                            JoshApplication.saveUsername(turupLoginResponseData.getmUsername());
                                                            JoshApplication.savePassword(getIntent().getExtras().getString("password"));
                                                            JoshApplication.saveUserId(turupLoginResponseData.getmUserId());

                                                            Log.d(TAG, "getRegisterLoginApiCall Success - " + response.body().toString());

                                                            JoshApplication.setAnalyticsUserPropertyIdentifers(mActivityContext);
                                                            JoshApplication.sendLoginEvent(mActivityContext, FirebaseAnalyticsCustomKeys.LOGIN_PARAMETER_METHOD_VALUE_USERNAMEPASS);
                                                            JoshApplication.sendRegisterEvent(mActivityContext);
                                                            JoshApplication.sendFinishRegistrationEvent(mActivityContext,""+ JoshApplication.userId(mActivityContext));

                                                            //JoshApplication.closeSpinnerProgress(mActivityContext);
                                                            //goToLobbyScreen();
                                                            JoshApplication.changeSpinnerProgressDialogMessage(getResources().getString(R.string.updating_profiledata));
                                                            if(imagePath != null) {
                                                                new uploadProfilePicAsync().execute(turupLoginResponseData.getmLoginToken());
                                                                //uploadProfilePicture(turupLoginResponseData.getmLoginToken());
                                                            }
                                                            else{
                                                                updateEmailApiCall(turupLoginResponseData.getmLoginToken());
                                                            }
                                                        } else {
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
                                                //goToLobbyScreen();
                                            }else{
                                                JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.registration_failed_title),
                                                        getResources().getString(R.string.registration_failed_msg));
                                                JoshApplication.signOut(mActivityContext);
                                                JoshApplication.closeSpinnerProgress(mActivityContext);
                                                Log.d(TAG, "getRegisterSignUpApiCall UnSuccessfull - "+"SignUp Failed - Firebase User Update failed!");
                                            }
                                        }
                                    });
                                }
                                else{
                                    JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                                    String errorMsg, errorCode;
                                    if(errorResponseObj != null){
                                        errorMsg = errorResponseObj.getmMessage();
                                        errorCode = errorResponseObj.getmErrorCode();
                                        Log.d(TAG, "getRegisterSignUpApiCall UnSuccessfull - "+errorResponseObj.getmErrorString());
                                    }
                                    else{
                                        errorMsg = getResources().getString(R.string.server_not_responding);
                                        errorCode = String.valueOf(response.code());
                                    }
                                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.registration_failed_title),
                                            errorMsg);
                                    JoshApplication.signOut(mActivityContext);
                                    JoshApplication.closeSpinnerProgress(mActivityContext);
                                }
                            }

                            @Override
                            public void onFailure(Call<JoshRegisterResponseData> call, Throwable t) {
                                JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.registration_failed_title),
                                        t.getLocalizedMessage());
                                JoshApplication.signOut(mActivityContext);
                                Log.d(TAG, "getRegisterSignUpApiCall UnSuccessfull - "+t.getMessage());
                            }
                        });
                    }
                    else{
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                                getResources().getString(R.string.no_internet_err));
                    }
                }
                }
                break;

            case R.id.edt_dateofbirth:
                Calendar calendarInstance = Calendar.getInstance();
                calendarInstance.add(Calendar.YEAR, -InputValidator.MIN_AGE);
                DatePickerDialog dialog = new DatePickerDialog(mActivityContext, dateSelectedListener, calendarInstance
                        .get(Calendar.YEAR), calendarInstance.get(Calendar.MONTH),
                        calendarInstance.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(calendarInstance.getTimeInMillis());
                dialog.show();
                break;
        }
    }

    private void selectFromSelectedProfile(ImageView selectedAvatar){
        if(selectedAvatar == imgSelectMush){
            imgProfile.setImageResource(R.drawable.icon_mushprofile);
            imgSelectMush.setAlpha(1f);
            imgSelectMush.setImageResource(R.drawable.selected_avatarmush);
            imgSelectTeen.setAlpha(0.6f);
            imgSelectTeen.setImageResource(R.drawable.icon_teenprofile);
            imgSelectSpecs.setAlpha(0.6f);
            imgSelectSpecs.setImageResource(R.drawable.icon_specsprofile);
            imgSelectGoogle.setAlpha(0.6f);
            imgSelectGoogle.setImageResource(R.drawable.icon_googleprofile);
        }
        else  if(selectedAvatar == imgSelectGoogle){
            imgProfile.setImageResource(R.drawable.icon_googleprofile);
            imgSelectGoogle.setAlpha(1f);
            imgSelectGoogle.setImageResource(R.drawable.selected_avatargoogle);
            imgSelectTeen.setAlpha(0.6f);
            imgSelectTeen.setImageResource(R.drawable.icon_teenprofile);
            imgSelectSpecs.setAlpha(0.6f);
            imgSelectSpecs.setImageResource(R.drawable.icon_specsprofile);
            imgSelectMush.setAlpha(0.6f);
            imgSelectMush.setImageResource(R.drawable.icon_mushprofile);
        }
        else  if(selectedAvatar == imgSelectSpecs){
            imgProfile.setImageResource(R.drawable.icon_specsprofile);
            imgSelectSpecs.setAlpha(1f);
            imgSelectSpecs.setImageResource(R.drawable.selected_avatarspecs);
            imgSelectTeen.setAlpha(0.6f);
            imgSelectTeen.setImageResource(R.drawable.icon_teenprofile);
            imgSelectMush.setAlpha(0.6f);
            imgSelectMush.setImageResource(R.drawable.icon_mushprofile);
            imgSelectGoogle.setAlpha(0.6f);
            imgSelectGoogle.setImageResource(R.drawable.icon_googleprofile);
        }
        else  if(selectedAvatar == imgSelectTeen){
            imgProfile.setImageResource(R.drawable.icon_teenprofile);
            imgSelectTeen.setAlpha(1f);
            imgSelectTeen.setImageResource(R.drawable.selected_avatarteen);
            imgSelectMush.setAlpha(0.6f);
            imgSelectMush.setImageResource(R.drawable.icon_mushprofile);
            imgSelectSpecs.setAlpha(0.6f);
            imgSelectSpecs.setImageResource(R.drawable.icon_specsprofile);
            imgSelectGoogle.setAlpha(0.6f);
            imgSelectGoogle.setImageResource(R.drawable.icon_googleprofile);
        }
        //enableUI();
    }

    private void goToLobbyScreen(){
        Intent dashboardIntent= new Intent(RegisterProfileDetailsActivity.this, HomeActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(dashboardIntent);
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
        finish();
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtDateOfBirth.setText(sdf.format(myCalendar.getTime()));
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterProfileDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivityContext);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getResources().getString(R.string.ask_storage_permission_message));
                    builder.setPositiveButton(getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(RegisterProfileDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
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
                    ActivityCompat.requestPermissions(RegisterProfileDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterProfileDetailsActivity.this, Manifest.permission.CAMERA)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivityContext);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getResources().getString(R.string.ask_camera_permission_message));
                    builder.setPositiveButton(getResources().getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(RegisterProfileDetailsActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CONSTANT);
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
                    ActivityCompat.requestPermissions(RegisterProfileDetailsActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CONSTANT);
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
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "fail- " + e.getMessage());
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
                    setImageFromPath();
                }

                break;
            case GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    Log.d(TAG, "getpath GALLERY - " + getRealPathFromURI(selectedImage));
                    imagePath = getRealPathFromURI(selectedImage);
                    setImageFromPath();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }

    private void uploadProfilePicture(final String loginToken) {
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
                loginToken,  profilePic);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    //loadProfileDetails();
                    Log.v("Upload Profie Pic:", "success");
                    updateEmailApiCall(loginToken);
                }
                else{
                    JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                    String errorMsg;
                    if(errorResponseObj!=null){
                        errorMsg = errorResponseObj.getmMessage();
                    }
                    else{
                        errorMsg = getResources().getString(R.string.kyc_upload_failed_desc);
                    }
//                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.upload_failed_heading),
//                            errorMsg);
//
//                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                    Log.v("Upload Profie Pic:", response +"---"+errorMsg);
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    goToLobbyScreen();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload Profie Pic:", t.getMessage());
                JoshApplication.closeSpinnerProgress(mActivityContext);
                goToLobbyScreen();
//                JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.upload_failed_heading),
//                        getResources().getString(R.string.kyc_upload_failed_desc));
//
//                JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
            }
        });
    }

    private void updateEmailApiCall(String loginToken){
        //UPDATING PROFILE EMAILID
        JoshApplication.getUpdateProfileEmailApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                loginToken,  edtEmailId.getText().toString()).enqueue(new Callback<JoshGenericOnlyStatusResponseData>() {
            @Override
            public void onResponse(Call<JoshGenericOnlyStatusResponseData> call, Response<JoshGenericOnlyStatusResponseData> response) {
                if (response.isSuccessful()) {
                    final JoshGenericOnlyStatusResponseData mChangeEmailIdResponseData = response.body();
                    if(mChangeEmailIdResponseData.getmStatus().equalsIgnoreCase(JoshGenericOnlyStatusResponseData.OK_STATUS)) {
                        JoshApplication.saveEmail(edtEmailId.getText().toString());
                    }
                    else{
//                                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.something_went_wromg),
//                                            mChangeEmailIdResponseData.getmStatus());
                    }
                    //JoshApplication.toast(mActivityContext, "Buyin success. got to webview");
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    goToLobbyScreen();
                }
                else{
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
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    goToLobbyScreen();
//                                JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
//                                        errorMsg);
//
//                                JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                }
            }
            @Override
            public void onFailure(Call<JoshGenericOnlyStatusResponseData> call, Throwable t) {
                Log.d(TAG, "getUpdateProfileEmailApiCall UnSuccessfull - " + t.getLocalizedMessage());
                JoshApplication.closeSpinnerProgress(mActivityContext);
                goToLobbyScreen();
//                            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
//                                    mActivityContext.getResources().getString(R.string.server_not_responding));
//
//                            JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
    }


    private final class uploadProfilePicAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            uploadProfilePicture(params[0]);
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