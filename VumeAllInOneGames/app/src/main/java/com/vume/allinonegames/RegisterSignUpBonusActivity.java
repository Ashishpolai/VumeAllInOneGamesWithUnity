package com.vume.allinonegames;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.vume.allinonegames.Util.JoshApplication;

import androidx.appcompat.app.AppCompatActivity;


public class RegisterSignUpBonusActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mActivityContext;
    private static String TAG = RegisterSignUpBonusActivity.class.getName();

    private Button btnProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        long time1 = System.currentTimeMillis();
        setContentView(R.layout.activity_signupbonus);
        long time2 = System.currentTimeMillis();
        Log.i(TAG, "onCreate: Load time = " + (time2 - time1));

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        init();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void init(){
        btnProceed = findViewById(R.id.btn_proceedfromsignupbonus);
        btnProceed.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_proceedfromsignupbonus:
                Intent signupprofileIntent = new Intent(RegisterSignUpBonusActivity.this, RegisterProfileDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("password", getIntent().getExtras().getString("password"));
                bundle.putString("authCredential", getIntent().getExtras().getString("authCredential"));
                bundle.putString("firebaseAuthResultIdToken", getIntent().getExtras().getString("firebaseAuthResultIdToken"));
                signupprofileIntent.putExtras(bundle);
                signupprofileIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(signupprofileIntent);
                overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}
