package com.vume.allinonegames;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.company.product.OverrideUnityActivity;
import com.vume.allinonegames.Util.JoshApplication;

public class MainUnityActivity extends OverrideUnityActivity {
    // Setup activity layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addControlsToUnityFrame();
        Intent intent = getIntent();
        handleIntent(intent);

        if(intent.getStringExtra(JoshApplication.UNITY_GAME_TYPE_KEY).equalsIgnoreCase(JoshApplication.UNITY_GAME_TYPE_LUDO)){
            mUnityPlayer.UnitySendMessage("Cube", "loadGame", JoshApplication.UNITY_GAME_TYPE_LUDO);
        }
        else if(intent.getStringExtra(JoshApplication.UNITY_GAME_TYPE_KEY).equalsIgnoreCase(JoshApplication.UNITY_GAME_TYPE_POKER)){
            mUnityPlayer.UnitySendMessage("Cube", "loadGame", JoshApplication.UNITY_GAME_TYPE_POKER);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        setIntent(intent);
    }

    void handleIntent(Intent intent) {
        if(intent == null || intent.getExtras() == null) return;

        if(intent.getExtras().containsKey("doQuit"))
            if(mUnityPlayer != null) {
                finish();
            }
    }

    @Override
    protected void showMainActivity(String setToColor) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("setColor", setToColor);
        startActivity(intent);
    }

    @Override public void onUnityPlayerUnloaded() {
        showMainActivity("");
    }

    public void addControlsToUnityFrame() {
        FrameLayout layout = mUnityPlayer;
        {
            Button myButton = new Button(this);
            myButton.setText("Show Main");
            myButton.setX(10);
            myButton.setY(500);

            myButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   showMainActivity("");
                }
            });
            layout.addView(myButton, 300, 200);
        }

        {
            Button myButton = new Button(this);
            myButton.setText("Send Msg");
            myButton.setX(320);
            myButton.setY(500);
            myButton.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    mUnityPlayer.UnitySendMessage("Cube", "ChangeColor", "yellow");
                    //mUnityPlayer.UnitySendMessage("Cube", "loadGame", "ludo");
                }
            });
            layout.addView(myButton, 300, 200);
        }

        {
            Button myButton = new Button(this);
            myButton.setText("Unload");
            myButton.setX(630);
            myButton.setY(500);

            myButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mUnityPlayer.unload();
                }
            });
            layout.addView(myButton, 300, 200);
        }

        {
            Button myButton = new Button(this);
            myButton.setText("Finish");
            myButton.setX(630);
            myButton.setY(800);

            myButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
            layout.addView(myButton, 300, 200);
        }
    }

    @Override
    protected void showToast(String toastmsg) {
        Toast.makeText(this, ""+toastmsg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void finishMyActivity() {
        finish();
    }
}
