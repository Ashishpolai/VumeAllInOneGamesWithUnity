package com.vume.allinonegames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.vume.allinonegames.Util.JoshApplication;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = ContactUsActivity.class.getName();
    public static String FAQ_TOPIC_KEY = "faqtopickey";
    Context mActivityContext;

    private ImageView btnBack;
    private View btnFaqOne, btnFaqTwo, btnFaqThree, btnFaqFour, btnFaqFive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_contact_us);

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        initUI();
    }

    private void initUI() {
        btnBack = findViewById(R.id.btn_back);
        btnFaqOne = findViewById(R.id.btn_faqone);
        btnFaqTwo = findViewById(R.id.btn_faqtwo);
        btnFaqThree = findViewById(R.id.btn_faqthree);
        btnFaqFour = findViewById(R.id.btn_faqfour);
        btnFaqFive = findViewById(R.id.btn_faqfive);

        btnBack.setOnClickListener(this);
        btnFaqOne.setOnClickListener(this);
        btnFaqTwo.setOnClickListener(this);
        btnFaqThree.setOnClickListener(this);
        btnFaqFour.setOnClickListener(this);
        btnFaqFive.setOnClickListener(this);
    }

    private void openFaqDetails(String faqhead){
        Intent faqDetailSIntent = new Intent(ContactUsActivity.this, FaqDetailsActivity.class);
        faqDetailSIntent.putExtra(FAQ_TOPIC_KEY, faqhead);
        faqDetailSIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(faqDetailSIntent);
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_faqone:
                openFaqDetails(getResources().getString(R.string.faq_one));
                break;

            case R.id.btn_faqtwo:
                openFaqDetails(getResources().getString(R.string.faq_two));
                break;

            case R.id.btn_faqthree:
                openFaqDetails(getResources().getString(R.string.faq_three));
                break;

            case R.id.btn_faqfour:
                openFaqDetails(getResources().getString(R.string.faq_four));
                break;

            case R.id.btn_faqfive:
                openFaqDetails(getResources().getString(R.string.faq_five));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}
