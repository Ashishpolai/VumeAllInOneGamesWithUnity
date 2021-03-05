package com.vume.allinonegames;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vume.allinonegames.Adapters.OffersCustomAdapter;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshOffersResponseData;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferralsAndOffersActivity extends AppCompatActivity implements View.OnClickListener, OffersCustomAdapter.OnTermsAndConditionsClickedListener {
    private static String TAG = AddCashActivity.class.getName();
    Context mActivityContext;

    private RecyclerView offersRecyclerView;
    private RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    private ImageView btnBack;
    private TextView txtReferralHeading, txtReferralDesc, txtReferralCode, txtNoOffersErroMsg;

    private Group grpOfferTermsandConditions;
    private TextView txtTermsAndConditionsTitle, txtTermsAndConditionsDesc, btnGotItTermsAndConditions, btnReadMoreTermsAndConditiosn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_referrals_and_offers);
        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        initUI();
        fetchAndUpdateJoshOffers();
    }

    private void initUI() {
        offersRecyclerView = findViewById(R.id.offers_recyclerview);

        btnBack = findViewById(R.id.btn_back);

        txtReferralCode = findViewById(R.id.referral_code_text);
        txtReferralHeading = findViewById(R.id.referral_ques);
        txtReferralDesc = findViewById(R.id.referral_msg);
        txtNoOffersErroMsg =  findViewById(R.id.no_offers_error);

        grpOfferTermsandConditions = findViewById(R.id.offers_termsandconcditions_layout);
        txtTermsAndConditionsTitle = findViewById(R.id.offer_termsandconditions_title);
        txtTermsAndConditionsDesc = findViewById(R.id.offer_termsandconditions_desc);
        btnGotItTermsAndConditions = findViewById(R.id.btn_gotit);
        btnReadMoreTermsAndConditiosn = findViewById(R.id.btn_readmore_termsandconditions);
        SpannableString readmoreContent = new SpannableString(getResources().getString(R.string.read_more_termsandconditions));
        readmoreContent.setSpan(new UnderlineSpan(), 0, readmoreContent.length(), 0);
        btnReadMoreTermsAndConditiosn.setText(readmoreContent);

        btnBack.setOnClickListener(this);
    }

    private void updateJoshOffers(List<JoshOffersResponseData> joshOffersResponseDataList){
        for(JoshOffersResponseData joshOffers : joshOffersResponseDataList){
            if(joshOffers.getmOfferType().equalsIgnoreCase(JoshOffersResponseData.OFFERTYPE_REFERRAL)){
                updateReferralUI(joshOffers);
                joshOffersResponseDataList.remove(joshOffers);
                break;
            }
        }

        offersRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        offersRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new OffersCustomAdapter(this, joshOffersResponseDataList);
        DividerItemDecoration itemDecorator = new DividerItemDecoration(mActivityContext, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(mActivityContext, R.drawable.item_divider_in_list));
        offersRecyclerView.addItemDecoration(itemDecorator);
        offersRecyclerView.setAdapter(mAdapter);
    }

    private void fetchAndUpdateJoshOffers(){
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.loading_offers));
        if(JoshApplication.isInternetAvailable(mActivityContext)) {
            JoshApplication.getOffersDetailsApiCall().enqueue(new Callback<List<JoshOffersResponseData>>() {
                @Override
                public void onResponse(Call<List<JoshOffersResponseData>> call, Response<List<JoshOffersResponseData>> response) {
                    if (response.isSuccessful()) {
                        final List<JoshOffersResponseData> joshOffersResponseDataList = response.body();
                        if(joshOffersResponseDataList.size()>0) {
                            txtNoOffersErroMsg.setVisibility(View.GONE);
                            updateJoshOffers(joshOffersResponseDataList);
                        }
                        else{
                            //NO Offers Found
                            txtNoOffersErroMsg.setVisibility(View.VISIBLE);
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
                public void onFailure(Call<List<JoshOffersResponseData>> call, Throwable t) {
                    JoshApplication.closeSpinnerProgress(mActivityContext);
                    Log.d(TAG, "getChangePasswordApiCall UnSuccessfull - " + t.getLocalizedMessage());
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

    private void updateReferralUI(JoshOffersResponseData referralRepsonseData){
        txtReferralHeading.setText(referralRepsonseData.getmOfferTitle());
        txtReferralDesc.setText(referralRepsonseData.getmOfferDescription().split("\n")[0]);
        txtReferralCode.setText(referralRepsonseData.getmOfferCode());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    public void sendTermsAndConditionsClick(final JoshOffersResponseData offerresponseData) {
        grpOfferTermsandConditions.setVisibility(View.VISIBLE);
        txtTermsAndConditionsTitle.setText(offerresponseData.getmOfferTitle());
        String termsandconditions_fulltext = "";
        String[] termsandconditionsList = offerresponseData.getmOfferDescription().split("\n");
        for(int x =0;x< termsandconditionsList.length; x++){
            termsandconditions_fulltext+="&#8226;  "+ termsandconditionsList[x];
            if(x < (termsandconditionsList.length-1)){
                termsandconditions_fulltext+="<br/><br/>";
            }
        }
        txtTermsAndConditionsDesc.setText(Html.fromHtml(termsandconditions_fulltext));
        btnGotItTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grpOfferTermsandConditions.setVisibility(View.GONE);
            }
        });
        btnReadMoreTermsAndConditiosn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openTermsAndConditionsIntent = new Intent(Intent.ACTION_VIEW);
                openTermsAndConditionsIntent.setData(Uri.parse(offerresponseData.getmTermsAndConditionsUrl()));
                openTermsAndConditionsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(openTermsAndConditionsIntent);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(grpOfferTermsandConditions.getVisibility() == View.VISIBLE){
            grpOfferTermsandConditions.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
            overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
        }
    }
}
