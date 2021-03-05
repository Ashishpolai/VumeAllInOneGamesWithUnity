package com.vume.allinonegames;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.vume.allinonegames.Adapters.CustomExpandableListview;
import com.vume.allinonegames.Model.ExpandableListViewData;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.HashMap;
import java.util.List;

public class FaqDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = ContactUsActivity.class.getName();
    Context mActivityContext;

    private ImageView btnBack;
    private TextView txtFaqHeading;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_faq_details);

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        initUI();
        loadFaqDetails();
    }

    private void initUI() {
        btnBack = findViewById(R.id.btn_back);
        txtFaqHeading = findViewById(R.id.faq_heading);
        txtFaqHeading.setText(getIntent().getStringExtra(ContactUsActivity.FAQ_TOPIC_KEY));

        btnBack.setOnClickListener(this);
    }

    private void loadFaqDetails(){
        expandableListView = (ExpandableListView) findViewById(R.id.faqExpandableListview);
        expandableListDetail = getCurrentFaqListForTopic();
        expandableListTitle = getCurrentFaqQuestnListForTopic();
        expandableListAdapter = new CustomExpandableListview(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
    }

    private HashMap<String, List<String>> getCurrentFaqListForTopic(){
        if((getIntent().getStringExtra(ContactUsActivity.FAQ_TOPIC_KEY)).equalsIgnoreCase(getResources().getString(R.string.faq_one))){
            return ExpandableListViewData.getFaqOneData((Activity) mActivityContext);
        }
        else if((getIntent().getStringExtra(ContactUsActivity.FAQ_TOPIC_KEY)).equalsIgnoreCase(getResources().getString(R.string.faq_two))){
            return ExpandableListViewData.getFaqTwoData((Activity) mActivityContext);
        }
        else if((getIntent().getStringExtra(ContactUsActivity.FAQ_TOPIC_KEY)).equalsIgnoreCase(getResources().getString(R.string.faq_three))){
            return ExpandableListViewData.getFaqThreeData((Activity) mActivityContext);
        }
        else if((getIntent().getStringExtra(ContactUsActivity.FAQ_TOPIC_KEY)).equalsIgnoreCase(getResources().getString(R.string.faq_four))){
            return ExpandableListViewData.getFaqFourData((Activity) mActivityContext);
        }
        else if((getIntent().getStringExtra(ContactUsActivity.FAQ_TOPIC_KEY)).equalsIgnoreCase(getResources().getString(R.string.faq_five))){
            return ExpandableListViewData.getFaqFiveData((Activity) mActivityContext);
        }
        return null;
    }

    private List<String> getCurrentFaqQuestnListForTopic(){
        if((getIntent().getStringExtra(ContactUsActivity.FAQ_TOPIC_KEY)).equalsIgnoreCase(getResources().getString(R.string.faq_one))){
            return ExpandableListViewData.getFaqOneQuestionList((Activity) mActivityContext);
        }
        else if((getIntent().getStringExtra(ContactUsActivity.FAQ_TOPIC_KEY)).equalsIgnoreCase(getResources().getString(R.string.faq_two))){
            return ExpandableListViewData.getFaqTwoQuestionList((Activity) mActivityContext);
        }
        else if((getIntent().getStringExtra(ContactUsActivity.FAQ_TOPIC_KEY)).equalsIgnoreCase(getResources().getString(R.string.faq_three))){
            return ExpandableListViewData.getFaqThreeQuestionList((Activity) mActivityContext);
        }
        else if((getIntent().getStringExtra(ContactUsActivity.FAQ_TOPIC_KEY)).equalsIgnoreCase(getResources().getString(R.string.faq_four))){
            return ExpandableListViewData.getFaqFourQuestionList((Activity) mActivityContext);
        }
        else if((getIntent().getStringExtra(ContactUsActivity.FAQ_TOPIC_KEY)).equalsIgnoreCase(getResources().getString(R.string.faq_five))){
            return ExpandableListViewData.getFaqFiveQuestionList((Activity) mActivityContext);
        }
        return null;
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}
