package com.vume.allinonegames;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vume.allinonegames.Adapters.TransactionHistoryCustomAdapter;
import com.vume.allinonegames.Model.JoshAllTransactionsResponseData;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = TransactionHistoryActivity.class.getName();
    Context mActivityContext;

    private RecyclerView transactionsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    private ImageView btnBack;
    private TextView txtCurrentBalance, txtNoTransactionError;

    List<JoshAllTransactionsResponseData> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_transaction_history);
        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        initUI();
        loadAllTransactions();
    }

    private void initUI() {
        txtCurrentBalance = findViewById(R.id.txt_currentbalance);
        txtNoTransactionError = findViewById(R.id.txt_notransactions_error);
        transactionsRecyclerView = findViewById(R.id.transactions_recyclerview);
        transactionsRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        transactionsRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecorator = new DividerItemDecoration(mActivityContext, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(mActivityContext, R.drawable.item_divider_in_list));
        transactionsRecyclerView.addItemDecoration(itemDecorator);
        btnBack = findViewById(R.id.btn_back);

        txtCurrentBalance.setText(getResources().getString(R.string.balance_value, String.valueOf(JoshApplication.currentbalance(mActivityContext))));

        btnBack.setOnClickListener(this);
    }

    private void loadAllTransactions(){
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.loading_transactions));
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getAllTransactionsApiCall(mActivityContext.getResources().getString(R.string.josh_server_api_key),
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext)).enqueue(new Callback<List<JoshAllTransactionsResponseData>>() {
                @Override
                public void onResponse(Call<List<JoshAllTransactionsResponseData>> call, Response<List<JoshAllTransactionsResponseData>> response) {
                    if (response.isSuccessful()) {
                        transactionList = response.body();

                        if(transactionList!=null && transactionList.size()>0) {
//                            ArrayList<JoshAllTransactionsResponseData> allTransactionsResponseData = new ArrayList<>();
//                            for (JoshAllTransactionsResponseData data : transactionList) {
//                                if ((data.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_WITHDRAWAL) ||
//                                        (data.getmTxnType() == PaymentUtils.TRANSACTION_TYPE_DEPOSIT)) {
//                                    allTransactionsResponseData.add(data);
//                                }
//                            }
                            mAdapter = new TransactionHistoryCustomAdapter(mActivityContext, transactionList);
                            transactionsRecyclerView.setAdapter(mAdapter);
                            transactionsRecyclerView.setVisibility(View.VISIBLE);
                            txtNoTransactionError.setVisibility(View.GONE);
                        }
                        else{
                            transactionsRecyclerView.setVisibility(View.GONE);
                            txtNoTransactionError.setVisibility(View.VISIBLE);
                        }
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                    }
                    else{
                        JoshApplication.closeSpinnerProgress(mActivityContext);
                        JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
                        String errorMsg, errorCode;
                        if(errorResponseObj != null){
                            errorMsg = errorResponseObj.getmMessage();
                            errorCode = errorResponseObj.getmErrorCode();
                            Log.d(TAG, "getAllTransactionsApiCall UnSuccessfull - " + errorResponseObj.getmErrorString());
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
                public void onFailure(Call<List<JoshAllTransactionsResponseData>> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getAllTransactionsApiCall UnSuccessfull - " + t.getLocalizedMessage());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
        }
    }

    class TransactionsComparatorAsPerDate implements Comparator<JoshAllTransactionsResponseData> {
        @Override
        public int compare(JoshAllTransactionsResponseData a, JoshAllTransactionsResponseData b) {
            return a.getmTxnTimestamp() < b.getmTxnTimestamp() ? -1 : a.getmTxnTimestamp() == b.getmTxnTimestamp() ? 0 : 1;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}
