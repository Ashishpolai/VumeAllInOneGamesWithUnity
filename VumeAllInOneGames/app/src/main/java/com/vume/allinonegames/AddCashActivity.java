package com.vume.allinonegames;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.cottacush.android.currencyedittext.CurrencyInputWatcher;
import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.vume.allinonegames.Model.JoshCloseTransactionRequest;
import com.vume.allinonegames.Model.JoshCloseTransactionResponseData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshInitiateTransactionResponseData;
import com.vume.allinonegames.Model.JoshRazorpayTxnSuccessResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.vume.allinonegames.Util.PaymentUtils;

import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCashActivity extends AppCompatActivity implements View.OnClickListener, PaymentResultWithDataListener {
    private static String TAG = AddCashActivity.class.getName();
    Context mActivityContext;

    public static final String LAUNCHING_FROM_GAMEWEBVIEW =  "launchedaddcashfromgamewebview";

    private Group mTxnSuccessGroup, mTxnFailedGrp;

    private ImageView btnBack;
    private TextView btnSelectFiveHundred, btnSelectOneThousand, btnSelectTwoThousand,
                txtSuccessHeading, txtPaymentOrderId, txtPaymentMerchent, txtPaymentTime, btnBackToLobby, btnTryAddCashAgain;
    private CurrencyEditText edtAddcashAmount;
    private Button btnAddCash ;
    MyCountDownTimer myCountDownTimer;
    private String launchingFrom;
    int goToLobbyCountdown = PaymentUtils.PAYMENT_SUCCESS_GO_TOLOBBY_TIMERINSECS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_add_cash);
        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        final Bundle args = getIntent().getExtras();
        if(args != null && args.getString(GameSelectPriceActivity.ARGS_LAUNCHINGFROM)!=null){
            launchingFrom = args.getString(GameSelectPriceActivity.ARGS_LAUNCHINGFROM);
        }

        Checkout.preload(mActivityContext);

        PaymentUtils.setCurrentPaymentMethod(PaymentUtils.RAZORPAY_PAYMENTMETHOD);

        initUI();
    }

    private void initUI() {
        btnBack = findViewById(R.id.btn_back);
        btnAddCash = findViewById(R.id.btn_addcash);
        btnSelectFiveHundred = findViewById(R.id.btn_selectfivehundredrupees);
        btnSelectOneThousand = findViewById(R.id.btn_selectonethousandrupees);
        btnSelectTwoThousand = findViewById(R.id.btn_selecttwothousandrupees);

        txtSuccessHeading = findViewById(R.id.transactionsuccess_heading);
        txtPaymentOrderId = findViewById(R.id.payment_orderid);
        txtPaymentMerchent = findViewById(R.id.payment_gateway);
        txtPaymentTime = findViewById(R.id.payment_time);
        btnBackToLobby = findViewById(R.id.btn_backtolobby);
        btnTryAddCashAgain = findViewById(R.id.btn_tryaddcashagain);

        btnTryAddCashAgain.setOnClickListener(this);
        btnBackToLobby.setOnClickListener(this);

        mTxnSuccessGroup = findViewById(R.id.transaction_success_group);
        mTxnFailedGrp = findViewById(R.id.transaction_fail_group);

        edtAddcashAmount = findViewById(R.id.edt_enteraddcash_amount);
        edtAddcashAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null && s.length()>0 && s.charAt(s.length()-1) == '.'){
                    edtAddcashAmount.setText(edtAddcashAmount.getText().toString().replace(".",""));
                }else {
                    btnAddCash.setText(String.format(getResources().getString(R.string.pay_money_from_app), edtAddcashAmount.getText().toString(),
                            PaymentUtils.getCurrentPaymentMethod()));
                    edtAddcashAmount.setSelection(edtAddcashAmount.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtAddcashAmount.addTextChangedListener(
                new CurrencyInputWatcher(edtAddcashAmount,getResources().getString(R.string.rupee_sign), Locale.getDefault()));

        btnBack.setOnClickListener(this);
        btnAddCash.setOnClickListener(this);
        btnSelectFiveHundred.setOnClickListener(this);
        btnSelectOneThousand.setOnClickListener(this);
        btnSelectTwoThousand.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_addcash:
                final String priceStr = edtAddcashAmount.getText().toString()
                        .replace(getResources().getString(R.string.rupee_sign), "")
                        .replace(",","");
                if(!priceStr.equalsIgnoreCase("")) {
                    final double price = Double.valueOf(priceStr);
                    final String orderId = PaymentUtils.getCurrentPaymentMethodInShort()+UUID.randomUUID().toString();
                    if(PaymentUtils.getCurrentPaymentMethod().equalsIgnoreCase(PaymentUtils.PAYTM_PAYMENTMETHOD)) {
                        initTransaction(String.format("%.2f", price), orderId);
                    }
                    else if(PaymentUtils.getCurrentPaymentMethod().equalsIgnoreCase(PaymentUtils.MOBIKWIK_PAYMENTMETHOD)){
                        Toast.makeText(mActivityContext, "Mobikwik", Toast.LENGTH_SHORT).show();
                    }
                    else if(PaymentUtils.getCurrentPaymentMethod().equalsIgnoreCase(PaymentUtils.RAZORPAY_PAYMENTMETHOD)){
                        initTransaction(String.format("%.2f", price), orderId);
                    }
                }
                else{
                    JoshApplication.showErrorDialog(mActivityContext, getResources().getString(R.string.fill_addcash_entries_error_title),
                            getResources().getString(R.string.fill_addcash_entries_error));
                }
                break;

            case R.id.btn_selectfivehundredrupees:
                setUiSelection(btnSelectFiveHundred, btnSelectOneThousand, btnSelectTwoThousand,
                        getResources().getString(R.string.price_fivehundred));
                break;

            case R.id.btn_selectonethousandrupees:
                    setUiSelection(btnSelectOneThousand, btnSelectFiveHundred, btnSelectTwoThousand,
                            getResources().getString(R.string.price_onethousand));
                break;

            case R.id.btn_selecttwothousandrupees:
                setUiSelection(btnSelectTwoThousand, btnSelectFiveHundred, btnSelectOneThousand,
                        getResources().getString(R.string.price_twothousand));
                break;

            case R.id.btn_backtolobby:
                    goToLobbyScreen();
                break;

            case R.id.btn_tryaddcashagain:
                onBackPressed();
                btnAddCash.performClick();
                break;
        }
    }

    private void startRazorpayTransaction(final String price, final String orderId, final String txnToken){
        Checkout checkout = new Checkout();
        checkout.setKeyID(getResources().getString(R.string.razorpay_keyid));

        double priceSubUnits = Double.valueOf(price)*100;

        /**
         * Set your logo here
         */
        checkout.setImage(R.mipmap.ic_launcher);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", getResources().getString(R.string.app_name));
            options.put("description", getResources().getString(R.string.add_cash_heading));
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", txnToken);//from response of step 3.
            options.put("theme.color", "#1A70C7");
            options.put("currency", PaymentUtils.CURRENCY);
            options.put("amount", String.valueOf(priceSubUnits));//pass amount in currency subunits
            options.put("prefill.email", JoshApplication.email(mActivityContext));
            options.put("prefill.contact",JoshApplication.phoneno(mActivityContext));
            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
            paymentFail();
        }
    }

    private void initTransaction(final String price, final String orderId){
        Log.d(TAG, "initTrnasction - \n price - "+price+"\norderid - "+orderId);
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.initiating_transaction));
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getInitiatePaytmTransactionApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), Double.valueOf(price), orderId, PaymentUtils.getCurrentPaymentMethod().toLowerCase()).enqueue(new Callback<JoshInitiateTransactionResponseData>() {
                @Override
                public void onResponse(Call<JoshInitiateTransactionResponseData> call, Response<JoshInitiateTransactionResponseData> response) {
                    if (response.isSuccessful()) {
                        final JoshInitiateTransactionResponseData mJoshInitiateTransactionResponseData = response.body();
                        Log.d(TAG, "initTrnasction - Transaction token - "+ mJoshInitiateTransactionResponseData.getmTxnToken());
                        if (PaymentUtils.getCurrentPaymentMethod().equalsIgnoreCase(PaymentUtils.PAYTM_PAYMENTMETHOD)) {
                            startPaytmPayment(price, orderId, mJoshInitiateTransactionResponseData.getmTxnToken());
                        }
                        else if(PaymentUtils.getCurrentPaymentMethod().equalsIgnoreCase(PaymentUtils.RAZORPAY_PAYMENTMETHOD)){
                            startRazorpayTransaction(price, orderId, mJoshInitiateTransactionResponseData.getmTxnToken());
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
                            Log.d(TAG, "getInitiatePaytmTransactionApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                errorMsg);

                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                        //paymentFail();
                    }
                }
                @Override
                public void onFailure(Call<JoshInitiateTransactionResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getInitiatePaytmTransactionApiCall UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                            mActivityContext.getResources().getString(R.string.server_not_responding));

                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                    //paymentFail();
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

    private void closeTransaction(final JoshRazorpayTxnSuccessResponseData joshRazorpayResponse){
        Log.d(TAG, "closeTransaction - \n OrderidRazorpay - "+joshRazorpayResponse.getmRazorpayOrderId());
        JoshCloseTransactionRequest closeTrnscRequest = new JoshCloseTransactionRequest(joshRazorpayResponse.getmRazorpayPaymentId(),
                joshRazorpayResponse.getmRazorpayOrderId(), joshRazorpayResponse.getmRazorpaySignature(), PaymentUtils.getCurrentPaymentMethod().toLowerCase());
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.initiating_transaction));
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getClosePaytmTransactionApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext), closeTrnscRequest).enqueue(new Callback<JoshCloseTransactionResponseData>() {
                @Override
                public void onResponse(Call<JoshCloseTransactionResponseData> call, Response<JoshCloseTransactionResponseData> response) {
                    if (response.isSuccessful()) {
                        final JoshCloseTransactionResponseData mJoshInitiateTransactionResponseData = response.body();
                        Log.d(TAG, "closeTransactin - Transaction amoutn - "+ mJoshInitiateTransactionResponseData.getmTransactionAmount());
                        JoshApplication.saveCurrentBalance(mJoshInitiateTransactionResponseData.getmBalance());
                        paymentSuccess(mJoshInitiateTransactionResponseData.getmTransactionAmount(), mJoshInitiateTransactionResponseData.getmOrderId());
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
                            Log.d(TAG, "closeTransactin UnSuccessfull - " + errorResponseObj.getmErrorString());
                        }
                        else{
                            errorMsg = getResources().getString(R.string.server_not_responding);
                            errorCode = String.valueOf(response.code());
                        }
                        JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                                errorMsg);

                        JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                        paymentFail();
                    }
                }
                @Override
                public void onFailure(Call<JoshCloseTransactionResponseData> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "closeTransactin UnSuccessfull - " + t.getLocalizedMessage());
                    JoshApplication.showErrorDialog(mActivityContext, mActivityContext.getResources().getString(R.string.server_not_responding_title),
                            mActivityContext.getResources().getString(R.string.server_not_responding));

                    JoshApplication.getErrorDialogOkButton(mActivityContext).setText(mActivityContext.getResources().getString(R.string.ok_heading));
                    paymentFail();
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

    private void startPaytmPayment(final String price, final String orderId, final String txnToken){
        final String callbackurl = PaymentUtils.PAYTM_BASE_URL+"/theia/paytmCallback?ORDER_ID="+orderId;

        Log.d(TAG, "callbackurl - "+callbackurl);

        PaytmOrder paytmOrder = new PaytmOrder(orderId, getResources().getString(R.string.paytm_merchantid), txnToken, price, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback(){
            @Override
            public void onTransactionResponse(Bundle bundle) {
                Log.e(TAG, "Response (onTransactionResponse) : "+bundle.toString());
            }

            @Override
            public void networkNotAvailable() {
                Log.e(TAG, "network not available ");
            }

            @Override
            public void onErrorProceed(String s) {
                Log.e(TAG, " onErrorProcess "+s.toString());
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Log.e(TAG, "Clientauth "+s);
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.e(TAG, " UI error "+s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.e(TAG, " error loading web "+s+"--"+s1);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.e(TAG, "backPress ");
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(TAG, " transaction cancel "+s);
            }
        });
        transactionManager.setShowPaymentUrl(PaymentUtils.PAYTM_BASE_URL+"/theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, PaymentUtils.STARTPAYTM_TRXN_REQUESTCODE);
    }

    private void setUiSelection(TextView selectedBtn, TextView unselectedBtnOne, TextView unselectedBtnTwo, String price){
        selectedBtn.setBackgroundResource(R.drawable.red_highroundedcorner_bg);
        unselectedBtnOne.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
        unselectedBtnTwo.setBackgroundResource(R.drawable.brown_highlyroundedcorner);
        selectedBtn.setTextColor(getResources().getColor(R.color.white));
        unselectedBtnOne.setTextColor(getResources().getColor(R.color.orangydark));
        unselectedBtnTwo.setTextColor(getResources().getColor(R.color.orangydark));
        edtAddcashAmount.setText(price);
        btnAddCash.setText(String.format(getResources().getString(R.string.pay_money_from_app), edtAddcashAmount.getText().toString().trim(),
                PaymentUtils.getCurrentPaymentMethod()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG ," result code "+resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        if (requestCode == PaymentUtils.STARTPAYTM_TRXN_REQUESTCODE && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }
            }
            Log.e(TAG, " data "+  data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " data response - "+data.getStringExtra("response"));
            /*
             data response - {"BANKNAME":"WALLET","BANKTXNID":"1395841115",
             "CHECKSUMHASH":"7jRCFIk6mrep+IhnmQrlrL43KSCSXrmM+VHP5pH0hekXaaxjt3MEgd1N9mLtWyu4VwpWexHOILCTAhybOo5EVDmAEV33rg2VAS/p0PXdk\u003d",
             "CURRENCY":"INR","GATEWAYNAME":"WALLET","MID":"EAc0553138556","ORDERID":"100620202152",
             "PAYMENTMODE":"PPI","RESPCODE":"01","RESPMSG":"Txn Success","STATUS":"TXN_SUCCESS",
             "TXNAMOUNT":"2.00","TXNDATE":"2020-06-10 16:57:45.0","TXNID":"20200610111212800110168328631290118"}
              */
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage")
                    + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        }else{
            Log.e(TAG, " payment failed");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            JoshRazorpayTxnSuccessResponseData razrpayResponsedata = new Gson().fromJson(paymentData.getData().toString(), JoshRazorpayTxnSuccessResponseData.class);
            Log.d(TAG, "Razorpay success - " + paymentData.getData().toString() + "---\nOrder Id = " + razrpayResponsedata.getmRazorpayOrderId());
            closeTransaction(razrpayResponsedata);
        }
        catch (Exception e){
            paymentFail();
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.d(TAG, "Razorpay failure - "+paymentData.getData().toString());
        paymentFail();

    }

    private void startGoToLobbyTimer(){
        stopTimer();
        goToLobbyCountdown = PaymentUtils.PAYMENT_SUCCESS_GO_TOLOBBY_TIMERINSECS;
        myCountDownTimer = new MyCountDownTimer(10000, 1000);
        myCountDownTimer.start();
    }

    private void stopTimer(){
        if(myCountDownTimer!=null)
        myCountDownTimer.cancel();
    }

    private void paymentSuccess(float transactionAmount, String orderId){
        mTxnFailedGrp.setVisibility(View.INVISIBLE);
        mTxnSuccessGroup.setVisibility(View.VISIBLE);

        txtPaymentMerchent.setText(PaymentUtils.getCurrentPaymentMethod());
        txtPaymentOrderId.setText(orderId);
        txtSuccessHeading.setText(getResources().getString(R.string.trxn_success_heading, String.valueOf(transactionAmount)));

        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        txtPaymentTime.setText(currentDateTimeString);

        startGoToLobbyTimer();
    }

    private void paymentFail(){
        mTxnFailedGrp.setVisibility(View.VISIBLE);
        mTxnSuccessGroup.setVisibility(View.INVISIBLE);
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(goToLobbyCountdown > 0 ) {
                goToLobbyCountdown--;
                btnBackToLobby.setText(getResources().getString(R.string.back_to_lobby_in_secs, String.valueOf(goToLobbyCountdown)));
            }
            else{
                goToLobbyScreen();
            }
        }

        @Override
        public void onFinish() {
            btnBackToLobby.setText(getResources().getString(R.string.back_to_lobby_in_secs, String.valueOf(0)));
            goToLobbyScreen();
        }
    }

    @Override
    public void onBackPressed() {
        if(mTxnSuccessGroup.getVisibility() == View.VISIBLE){
            mTxnSuccessGroup.setVisibility(View.GONE);
            stopTimer();
        }
        else if(mTxnFailedGrp.getVisibility() == View.VISIBLE){
            mTxnFailedGrp.setVisibility(View.GONE);
        }
        else {
            if(launchingFrom !=null && launchingFrom.equalsIgnoreCase(LAUNCHING_FROM_GAMEWEBVIEW)){
                goToLobbyScreen();
            }
            else {
                super.onBackPressed();
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private void goToLobbyScreen(){
        if(JoshApplication.getCurrentGameType().equalsIgnoreCase(JoshApplication.FANTASY_LEAGUE_GAME_TYPE)){
            Intent dashboardIntent = new Intent(AddCashActivity.this, FantasyLobbyActivity.class);
            dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(dashboardIntent);
            overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
            finish();
        }
        else {
            Intent dashboardIntent = new Intent(AddCashActivity.this, LobbyActivity.class);
            dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(dashboardIntent);
            overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
            finish();
        }
    }
}
