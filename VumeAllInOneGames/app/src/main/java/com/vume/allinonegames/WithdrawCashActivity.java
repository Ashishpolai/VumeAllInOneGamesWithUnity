package com.vume.allinonegames;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.cottacush.android.currencyedittext.CurrencyInputWatcher;
import com.vume.allinonegames.Adapters.WithdrawalTransactionHistoryCustomAdapter;
import com.vume.allinonegames.Model.JoshAllWithdrawalTransactionsResponseData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshWithdrawRaiseOrcancelResponse;
import com.vume.allinonegames.Model.JoshWithdrawalTransactionsResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;
import com.vume.allinonegames.Util.PaymentUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawCashActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = WithdrawCashActivity.class.getName();
    Context mActivityContext;
    private ImageView btnBack, imgKycVerifiedIndicator, imgBankAccountVerifiedIndicator;
    private CurrencyEditText edtWithdrawcashAmount;
    private TextView txtWithdrawTxnsHeading, txtWithdrawableBalance, btnShowTxnsFullScreen, txtCurrentWithdrawStatusIndicator,
    txtCurrentWithdrawRequestAmount;
    private RecyclerView mWithdrawTransactionsRecyclerview;
    private Group withdrawcashGroup, cancelWithdrawcashGroup;
    private Button btnWithdrawCash, btnCancelWithdrawaCash;
    private JoshWithdrawalTransactionsResponseData mJoshWithdrawalTransactionsResponseData;
    private List<JoshAllWithdrawalTransactionsResponseData> allTransactions = new ArrayList<>();
    private boolean isLastWithdrawRequestCompleted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_withdraw_cash);
        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        initUI();
        loadWithdrawTransactionsList();
    }

    private void initUI() {
        btnBack = findViewById(R.id.btn_back);
        btnWithdrawCash = findViewById(R.id.btn_withdrawcash);
        btnCancelWithdrawaCash = findViewById(R.id.btn_cancel_withdrawcash);
        withdrawcashGroup = findViewById(R.id.withdraw_cash_group);
        cancelWithdrawcashGroup = findViewById(R.id.cancel_withdraw_cash_group);
        imgKycVerifiedIndicator = findViewById(R.id.kyc_updated_tick);
        imgBankAccountVerifiedIndicator = findViewById(R.id.bankaccount_updated_tick);
        txtWithdrawTxnsHeading = findViewById(R.id.withdraw_list_heading);
        txtWithdrawableBalance = findViewById(R.id.label_withdrawablebalance);
        txtCurrentWithdrawStatusIndicator = findViewById(R.id.txtCurrentWithdrawCashStatus);
        txtCurrentWithdrawRequestAmount = findViewById(R.id.label_current_withdrawingbalance);
        btnShowTxnsFullScreen = findViewById(R.id.btn_show_fullscreen);
        mWithdrawTransactionsRecyclerview = findViewById(R.id.withdrawtransactions_recyclerview);
        edtWithdrawcashAmount = findViewById(R.id.edt_withdrawamount);
        SpannableString content = new SpannableString(getResources().getString(R.string.withdraw_list_heading));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtWithdrawTxnsHeading.setText(content);

        edtWithdrawcashAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null && s.length()>0 && s.charAt(s.length()-1) == '.'){
                    edtWithdrawcashAmount.setText(edtWithdrawcashAmount.getText().toString().replace(".",""));
                }else {
                    edtWithdrawcashAmount.setSelection(edtWithdrawcashAmount.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtWithdrawcashAmount.addTextChangedListener(
                new CurrencyInputWatcher(edtWithdrawcashAmount,getResources().getString(R.string.rupee_sign), Locale.getDefault()));
        btnBack.setOnClickListener(this);
        btnShowTxnsFullScreen.setOnClickListener(this);
        btnWithdrawCash.setOnClickListener(this);
        btnCancelWithdrawaCash.setOnClickListener(this);
    }

    private void loadWithdrawTransactionsList(){
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.loading_withdraw_transactions));
            JoshApplication.getWithdrawTransactionsListApi(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext)).enqueue(new Callback<JoshWithdrawalTransactionsResponseData>() {
                @Override
                public void onResponse(Call<JoshWithdrawalTransactionsResponseData> call, Response<JoshWithdrawalTransactionsResponseData> response) {
                    if (response.isSuccessful()) {
                        mJoshWithdrawalTransactionsResponseData = response.body();
                        allTransactions = mJoshWithdrawalTransactionsResponseData.getmWithdrawalTxns();
                        Log.d(TAG, "getWithdrawTransactionsListApi Successfull - "+mJoshWithdrawalTransactionsResponseData.toString());
                        loadWithdrawTransactionsDataUI(mJoshWithdrawalTransactionsResponseData);
                    }
                    else{
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        Log.d(TAG, "getWithdrawTransactionsListApi UnSuccessfull - "+response.errorBody());
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg, errorCode;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            errorCode = errorResponseObj.getmErrorCode();
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.login_failed_title),
                                errorMsg);
                        JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                    }
                }
                @Override
                public void onFailure(Call<JoshWithdrawalTransactionsResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getWithdrawTransactionsListApi UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                            getResources().getString(R.string.server_not_responding));
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            });
        }
        else{
            //JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                    getResources().getString(R.string.no_internet_err));
            JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void loadWithdrawTransactionsDataUI(JoshWithdrawalTransactionsResponseData mJoshWithdrawalTransactionsResponseData){
        if(mJoshWithdrawalTransactionsResponseData.ismIsBankAcctVerified()){
            imgBankAccountVerifiedIndicator.setImageResource(R.mipmap.tick_green_round);
        }else{
            imgBankAccountVerifiedIndicator.setImageResource(R.mipmap.tick_disabled_round);
        }

        if(mJoshWithdrawalTransactionsResponseData.ismIsKycVerified()){
            imgKycVerifiedIndicator.setImageResource(R.mipmap.tick_green_round);
        }else{
            imgKycVerifiedIndicator.setImageResource(R.mipmap.tick_disabled_round);
        }

        txtWithdrawableBalance.setText(getResources().getString(R.string.withdrawable_balance_label)+
                mJoshWithdrawalTransactionsResponseData.getmCurrentWithdrawBalance());

        if(mJoshWithdrawalTransactionsResponseData.getmWithdrawalTxns().size()>0){
            isLastWithdrawRequestCompleted = PaymentUtils.
                    isWithdrawRequestCompleted(mJoshWithdrawalTransactionsResponseData.getmWithdrawalTxns().get(0).getmTxnStatus());
            mWithdrawTransactionsRecyclerview.setVisibility(View.VISIBLE);
            txtWithdrawTxnsHeading.setVisibility(View.VISIBLE);
            btnShowTxnsFullScreen.setVisibility(View.VISIBLE);
            SpannableString showInFullscreenContent = new SpannableString(getResources().getString(R.string.show_in_fullscreen));
            showInFullscreenContent.setSpan(new UnderlineSpan(), 0, showInFullscreenContent.length(), 0);
            btnShowTxnsFullScreen.setText(showInFullscreenContent);

            mWithdrawTransactionsRecyclerview.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mWithdrawTransactionsRecyclerview.setLayoutManager(layoutManager);
            DividerItemDecoration itemDecorator = new DividerItemDecoration(mActivityContext, DividerItemDecoration.VERTICAL);
            itemDecorator.setDrawable(ContextCompat.getDrawable(mActivityContext, R.drawable.item_divider_in_list));
            mWithdrawTransactionsRecyclerview.addItemDecoration(itemDecorator);
            WithdrawalTransactionHistoryCustomAdapter mAdapter =
                    new WithdrawalTransactionHistoryCustomAdapter(mActivityContext, mJoshWithdrawalTransactionsResponseData.getmWithdrawalTxns());
            mWithdrawTransactionsRecyclerview.setAdapter(mAdapter);
        }
        else{
            isLastWithdrawRequestCompleted = true;
            mWithdrawTransactionsRecyclerview.setVisibility(View.GONE);
            txtWithdrawTxnsHeading.setVisibility(View.GONE);
            btnShowTxnsFullScreen.setVisibility(View.GONE);
        }
        loadWithdrawCashUI();
    }

    private void loadWithdrawCashUI(){
        if(isLastWithdrawRequestCompleted){
            withdrawcashGroup.setVisibility(View.VISIBLE);
            cancelWithdrawcashGroup.setVisibility(View.GONE);
        }
        else{
            cancelWithdrawcashGroup.setVisibility(View.VISIBLE);
            withdrawcashGroup.setVisibility(View.INVISIBLE);
            txtCurrentWithdrawRequestAmount.setText(
                    getResources().getString(R.string.requested_withdrawbalance_label)+allTransactions.get(0).getmTxnAmount());
            switch (allTransactions.get(0).getmTxnStatus()){
                case PaymentUtils.WITHDRAWAL_TRANSACTION_TYPE_INITIATED:
                    txtCurrentWithdrawStatusIndicator.setText(getResources().getString(R.string.withdraw_request_initiated));
                    break;

                case PaymentUtils.WITHDRAWAL_TRANSACTION_TYPE_INPROGRESS:
                    txtCurrentWithdrawStatusIndicator.setText(getResources().getString(R.string.withdraw_request_inprogress));
                    break;
            }
        }
        JoshApplication.closeSpinnerProgress(mActivityContext);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_show_fullscreen:
                if(withdrawcashGroup.getVisibility() == View.VISIBLE || cancelWithdrawcashGroup.getVisibility() == View.VISIBLE) {
                    SpannableString hideFullscreenContent = new SpannableString(getResources().getString(R.string.hide_fullscreen));
                    hideFullscreenContent.setSpan(new UnderlineSpan(), 0, hideFullscreenContent.length(), 0);
                    btnShowTxnsFullScreen.setText(hideFullscreenContent);
                    ConstraintLayout constraintLayout = findViewById(R.id.parent_layout);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.withdraw_list_heading, ConstraintSet.TOP, R.id.heading_laypout, ConstraintSet.BOTTOM, 0);
                    constraintSet.applyTo(constraintLayout);

                    withdrawcashGroup.setVisibility(View.GONE);
                    cancelWithdrawcashGroup.setVisibility(View.GONE);
                }
                else{
                    SpannableString showInFullscreenContent = new SpannableString(getResources().getString(R.string.show_in_fullscreen));
                    showInFullscreenContent.setSpan(new UnderlineSpan(), 0, showInFullscreenContent.length(), 0);
                    btnShowTxnsFullScreen.setText(showInFullscreenContent);
                    ConstraintLayout constraintLayout = findViewById(R.id.parent_layout);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.withdraw_list_heading, ConstraintSet.TOP, R.id.btn_withdrawcash, ConstraintSet.BOTTOM, 0);
                    constraintSet.applyTo(constraintLayout);

                    loadWithdrawCashUI();
                }
                break;

            case R.id.btn_withdrawcash:
                if(mJoshWithdrawalTransactionsResponseData!=null){//Required Data received
                    String withdrawAmountString = edtWithdrawcashAmount.getText().toString().trim().
                            replace(getResources().getString(R.string.rupee_sign), "");
                    if(!(mJoshWithdrawalTransactionsResponseData.ismIsKycVerified() &&
                            mJoshWithdrawalTransactionsResponseData.ismIsBankAcctVerified())){
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.cannot_withdraw),
                                getResources().getString(R.string.kyc_bank_accountneeded_error));
                    }
                    else if(withdrawAmountString != null && (!withdrawAmountString.equalsIgnoreCase("")) &&
                            (Integer.parseInt(withdrawAmountString)>0)){
                        final int withdrawalBalance = Integer.parseInt(withdrawAmountString);
                        if(withdrawalBalance>=mJoshWithdrawalTransactionsResponseData.getmMinimumWithdrwableBalance()){
                            raiseWithdrawalRequest(withdrawalBalance);
                            //Toast.makeText(mActivityContext, "Initiate withdrawal", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.cannot_withdraw),
                                    getResources().getString(R.string.min_withdrawable_balance_error,
                                            String.valueOf(mJoshWithdrawalTransactionsResponseData.getmMinimumWithdrwableBalance())));
                        }
                    }
                    else{
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.invalid_entries_heading),
                                getResources().getString(R.string.fill_withdrawbalance_error));
                    }

                }
                else{
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                            getResources().getString(R.string.server_not_responding));
                    JoshApplication.getErrorDialogOkButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
                break;

            case R.id.btn_cancel_withdrawcash:
                JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                        getResources().getString(R.string.cancel_withdrawal_confirm));
                JoshApplication.getErrorDialogLeftButton(mActivityContext).setText(getResources().getString(R.string.no_confirmdialog));
                JoshApplication.getErrorDialogLeftButton(mActivityContext).setVisibility(View.VISIBLE);
                JoshApplication.getErrorDialogRightButton(mActivityContext).setText(getResources().getString(R.string.yes_confirmdialog));
                JoshApplication.getErrorDialogRightButton(mActivityContext).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelWithdrawalRequest();
                        JoshApplication.closeErrorDialog(mActivityContext);
                    }
                });
                break;
        }
    }

    private void raiseWithdrawalRequest(int withdrawAmount){
        JoshApplication.closeInputKeybrd(mActivityContext, edtWithdrawcashAmount);
        edtWithdrawcashAmount.setText("");
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.raising_withdraw_request));
            JoshApplication.getRaiseWithdrawRequestApi(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), withdrawAmount).enqueue(new Callback<JoshWithdrawRaiseOrcancelResponse>() {
                @Override
                public void onResponse(Call<JoshWithdrawRaiseOrcancelResponse> call, Response<JoshWithdrawRaiseOrcancelResponse> response) {
                    if (response.isSuccessful()) {
                        JoshWithdrawRaiseOrcancelResponse responseData =  response.body();
                        JoshApplication.saveCurrentBalance(responseData.getmCurrentBalanceData().getmTotalBalance());
                        JoshApplication.saveCurrentBonusMoney(responseData.getmCurrentBalanceData().getmBonus());
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        Log.d(TAG, "getRaiseWithdrawRequestApi Successfull - "+mJoshWithdrawalTransactionsResponseData.toString());
                        loadWithdrawTransactionsList();
                    }
                    else{
                        Log.d(TAG, "getRaiseWithdrawRequestApi UnSuccessfull - "+response.errorBody());
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg, errorCode;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            errorCode = errorResponseObj.getmErrorCode();
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.login_failed_title),
                                errorMsg);
                    }
                }
                @Override
                public void onFailure(Call<JoshWithdrawRaiseOrcancelResponse> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getRaiseWithdrawRequestApi UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                            getResources().getString(R.string.server_not_responding));
                }
            });
        }
        else{
            //JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                    getResources().getString(R.string.no_internet_err));
        }
    }

    private void cancelWithdrawalRequest(){
        int cancelTxnId = 0;
        if(allTransactions.size()>0)
            cancelTxnId = allTransactions.get(0).getmTxnid();

        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.canceling_withdraw_request));
            JoshApplication.getCancelWithdrawRequestApi(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), cancelTxnId).enqueue(new Callback<JoshWithdrawRaiseOrcancelResponse>() {
                @Override
                public void onResponse(Call<JoshWithdrawRaiseOrcancelResponse> call, Response<JoshWithdrawRaiseOrcancelResponse> response) {
                    if (response.isSuccessful()) {
                        JoshWithdrawRaiseOrcancelResponse responseData =  response.body();
                        JoshApplication.saveCurrentBalance(responseData.getmCurrentBalanceData().getmTotalBalance());
                        JoshApplication.saveCurrentBonusMoney(responseData.getmCurrentBalanceData().getmBonus());
                        Log.d(TAG, "getCancelWithdrawRequestApi Successfull - "+mJoshWithdrawalTransactionsResponseData.toString());
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        loadWithdrawTransactionsList();
                    }
                    else{
                        Log.d(TAG, "getCancelWithdrawRequestApi UnSuccessfull - "+response.errorBody());
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg, errorCode;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            errorCode = errorResponseObj.getmErrorCode();
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.login_failed_title),
                                errorMsg);
                    }
                }
                @Override
                public void onFailure(Call<JoshWithdrawRaiseOrcancelResponse> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getCancelWithdrawRequestApi UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.server_not_responding_title),
                            getResources().getString(R.string.server_not_responding));
                }
            });
        }
        else{
            //JoshApplication.closeSpinnerProgress(mActivityContext);
            JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.no_internet_err_title),
                    getResources().getString(R.string.no_internet_err));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}
