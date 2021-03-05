package com.vume.allinonegames;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.constraintlayout.widget.Group;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vume.allinonegames.Model.JoshBankAccDetailsResponseData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshGenericOnlyStatusResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;
import com.vume.allinonegames.Util.InputValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpdateBankAccActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = UpdateBankAccActivity.class.getName();
    Context mActivityContext;

    private Button btnSubmitBankDetails;
    private Group editBankdetailsLayout, showBankdetailsLayout, allLabels;

    private EditText edtAccHolderName, edtAccNumber, edtBranch, edtIfscCode;
    private AppCompatAutoCompleteTextView edtBankName;
    private TextView txtAccHolderName, txtBankName, txtAccNumber, txtBranch, txtIfscCode;
    private View btnEditAccHolderName, btnEditBankName, btnEditAccNo, btnEditBranch, btnEditIfscCode;
    private ImageView btnBack;

    private ArrayList<String> bankCodeList, bankNameList, bankShowToSelectList;
    private String selectedBankCode;
    private JSONObject bankJSONObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_update_bank_acc);

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        initUI();
        loadBankNamesForAutocomplete();
        fetchBankAccountDetails();
}

    private void initUI(){
        btnSubmitBankDetails = findViewById(R.id.btn_submitbankdetails);

        btnSubmitBankDetails.setOnClickListener(this);

        editBankdetailsLayout = findViewById(R.id.edit_bank_details_layout);
        showBankdetailsLayout = findViewById(R.id.show_bank_details_layout);
        allLabels = findViewById(R.id.all_labels);

        edtAccHolderName = findViewById(R.id.edt_accounholdername);
        edtBankName = findViewById(R.id.edt_bankname);
        edtAccNumber = findViewById(R.id.edt_accountno);
        edtBranch = findViewById(R.id.edt_branch);
        edtIfscCode = findViewById(R.id.edt_ifsccode);

        txtAccHolderName = findViewById(R.id.lbl_accholdername_val);
        txtBankName = findViewById(R.id.lbl_banknae_val);
        txtAccNumber = findViewById(R.id.lbl_accno_val);
        txtBranch = findViewById(R.id.lbl_branch_val);
        txtIfscCode = findViewById(R.id.lbl_ifsc_val);

        btnEditAccHolderName = findViewById(R.id.btn_editaccholdername);
        btnEditBankName = findViewById(R.id.btn_editbankname);
        btnEditAccNo = findViewById(R.id.btn_editaccno);
        btnEditBranch = findViewById(R.id.btn_editbranch);
        btnEditIfscCode = findViewById(R.id.btn_editifsc);

        btnBack = findViewById(R.id.btn_back);

        btnEditAccHolderName.setOnClickListener(this);
        btnEditBankName.setOnClickListener(this);
        btnEditAccNo.setOnClickListener(this);
        btnEditBranch.setOnClickListener(this);
        btnEditIfscCode.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        //edtBankName.setOnClickListener(this);

        editBankdetailsLayout.setVisibility(View.INVISIBLE);
        showBankdetailsLayout.setVisibility(View.INVISIBLE);
        allLabels.setVisibility(View.INVISIBLE);
    }

    private void loadBankNamesForAutocomplete(){
        bankCodeList = new ArrayList<>();
        bankNameList = new ArrayList<>();
        bankShowToSelectList = new ArrayList<>();
        String bankJson = getBankNamesString();
        try {
            bankJSONObject = new JSONObject(bankJson);
            for (Iterator<String> it = bankJSONObject.keys(); it.hasNext(); ) {
                String bankCode = it.next();
                bankCodeList.add(bankCode);
                bankNameList.add(bankJSONObject.getString(bankCode));
                bankShowToSelectList.add(bankCode +" - "+bankJSONObject.getString(bankCode));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, R.layout.joshselect_dialog_item, bankShowToSelectList);
        edtBankName.setThreshold(1); //show option on 1st character
        edtBankName.setAdapter(adapter);
        edtBankName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                selectedBankCode = item.split(" - ")[0];
                edtBankName.setText(item.split(" - ")[1]);
            }
        });
    }

    private void updateBankAccData(final JoshBankAccDetailsResponseData joshBankAccDetails){
        edtAccHolderName.setText(joshBankAccDetails.getmAcccountHolderName());
        try {
            if(bankJSONObject!=null) {
                edtBankName.setText(bankJSONObject.getString(joshBankAccDetails.getmBankCode()));
                txtBankName.setText(bankJSONObject.getString(joshBankAccDetails.getmBankCode()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        edtAccNumber.setText(String.valueOf(joshBankAccDetails.getmAccountNumber()));
        edtBranch.setText(joshBankAccDetails.getmBranch());
        edtIfscCode.setText(joshBankAccDetails.getmIfsc());

        txtAccHolderName.setText(joshBankAccDetails.getmAcccountHolderName());
        txtAccNumber.setText(String.valueOf(joshBankAccDetails.getmAccountNumber()));
        txtBranch.setText(joshBankAccDetails.getmBranch());
        txtIfscCode.setText(joshBankAccDetails.getmIfsc());

        if(joshBankAccDetails.getmAcccountHolderName()==null || joshBankAccDetails.getmAcccountHolderName().equalsIgnoreCase("")){
            showEditBankAccDetailsLayout();
        }
        else{
            showViewBankAccDetailsLayout();
        }
    }

    private void fetchBankAccountDetails(){
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.laoding_bank_acc_details));
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getBankAccDetailsApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext)).enqueue(new Callback<JoshBankAccDetailsResponseData>() {
                @Override
                public void onResponse(Call<JoshBankAccDetailsResponseData> call, Response<JoshBankAccDetailsResponseData> response) {
                    if (response.isSuccessful()) {
                        final JoshBankAccDetailsResponseData mJoshBankAccDetailsResponseData = response.body();

                        updateBankAccData(mJoshBankAccDetailsResponseData);
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
                            Log.d(TAG, "getBankAccDetailsApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
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
                public void onFailure(Call<JoshBankAccDetailsResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getBankAccDetailsApiCall UnSuccessfull - " + t.getLocalizedMessage());
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

    private void submitBankAccountDetails(){
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.submitting_bank_acc_details));
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.updateBankAccDetailsApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), edtAccHolderName.getText().toString(),
                    selectedBankCode, edtAccNumber.getText().toString(), edtIfscCode.getText().toString(),
                    edtBranch.getText().toString()).enqueue(new Callback<JoshGenericOnlyStatusResponseData>() {
                @Override
                public void onResponse(Call<JoshGenericOnlyStatusResponseData> call, Response<JoshGenericOnlyStatusResponseData> response) {
                    if (response.isSuccessful()) {
                        final JoshGenericOnlyStatusResponseData mJoshBankAccDetailsResponseData = response.body();
                        if(mJoshBankAccDetailsResponseData.getmStatus().equalsIgnoreCase(JoshGenericOnlyStatusResponseData.OK_STATUS)) {
                            final JoshBankAccDetailsResponseData joshBankAccDetailsResponseData =
                                    new JoshBankAccDetailsResponseData(edtAccHolderName.getText().toString().trim(),
                                            selectedBankCode,
                                            Long.valueOf(edtAccNumber.getText().toString().trim()), edtBranch.getText().toString().trim(),
                                            edtIfscCode.getText().toString().trim());
                            updateBankAccData(joshBankAccDetailsResponseData);
                            showViewBankAccDetailsLayout();
                        }
                        else{
                            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.something_went_wromg),
                                    mJoshBankAccDetailsResponseData.getmStatus());
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
                            Log.d(TAG, "getLobbyBalanceApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
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
                    Log.d(TAG, "getBankAccDetailsApiCall UnSuccessfull - " + t.getLocalizedMessage());
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
            JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JoshApplication.closeErrorDialog(mActivityContext);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submitbankdetails:
                if(validateBankAccDetails()){
                    submitBankAccountDetails();
                }
                break;

            case R.id.btn_editaccholdername:
            case R.id.btn_editaccno:
            case R.id.btn_editbankname:
            case R.id.btn_editbranch:
            case R.id.btn_editifsc:
                showEditBankAccDetailsLayout();
                break;

            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void showEditBankAccDetailsLayout(){
        showBankdetailsLayout.setVisibility(View.GONE);
        showBankdetailsLayout.setVisibility(View.INVISIBLE);
        editBankdetailsLayout.setVisibility(View.VISIBLE);
        allLabels.setVisibility(View.VISIBLE);
    }

    private void showViewBankAccDetailsLayout(){
        editBankdetailsLayout.setVisibility(View.GONE);
        editBankdetailsLayout.setVisibility(View.INVISIBLE);
        showBankdetailsLayout.setVisibility(View.VISIBLE);
        allLabels.setVisibility(View.VISIBLE);
    }

    private boolean validateBankAccDetails(){
        if(edtAccHolderName.getText().toString().trim().equalsIgnoreCase("") ||
            edtBranch.getText().toString().trim().equalsIgnoreCase("") ||
            edtAccNumber.getText().toString().trim().equalsIgnoreCase("") ||
            edtBankName.getText().toString().trim().equalsIgnoreCase("") ||
            edtIfscCode.getText().toString().trim().equalsIgnoreCase("")){
            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.fill_all_entries_error_title),
                    mActivityContext.getResources().getString(R.string.fill_all_entries_error));
            return false;
        }
        else if(!InputValidator.isValidAccHolderName(edtAccHolderName.getText().toString().trim())){
            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.invalid_bankname),
                    mActivityContext.getResources().getString(R.string.invalid_accountholdername));
            return false;
        }
        else if(selectedBankCode == null    ||
                bankJSONObject == null ||
                (!bankNameList.contains(edtBankName.getText().toString()))){
            JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.invalid_bankname),
                    mActivityContext.getResources().getString(R.string.select_from_autocomplete_bankname));
            return false;
        }
        return true;
    }

    private String getBankNamesString() {
        StringBuilder termsString = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("banknames.json")));

            String str;
            while ((str = reader.readLine()) != null) {
                termsString.append(str);
            }

            reader.close();
            return termsString.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}
