package com.vume.allinonegames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.vume.allinonegames.Util.JoshApplication;

public class CreatePrivateTableActivity extends AppCompatActivity {
    private static String TAG = CreatePrivateTableActivity.class.getName();
    Context mActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_create_private_table);

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}
