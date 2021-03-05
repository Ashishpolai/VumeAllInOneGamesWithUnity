package com.vume.allinonegames;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;
import com.vume.allinonegames.Util.JoshApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class GameWebviewActivity extends AppCompatActivity {
    private static final String TAG = GameWebviewActivity.class.getName();
    public static final String LAUNCHING_FROM_MYOPENGAMES =  "opengameslaunchgame";
    private Context mActivityContext;
    private WebView mGameWebview;

    private String buyinTicketId, wagerId;
    private String launchingFrom;
    boolean doubleBackToExitPressedOnce = false;
    private ReviewInfo reviewInfo;
    private ReviewManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoshApplication.hideStatusBarWhenTappedOnEdittext(this);
        setContentView(R.layout.activity_game_webview);

        mActivityContext = this;
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);

        final Bundle args = getIntent().getExtras();
        buyinTicketId = args.getString(GameSelectPriceActivity.ARGS_BUYIN_TABLE_TICKETID);
        wagerId = args.getString(GameSelectPriceActivity.ARGS_BUYIN_TABLE_WAGERID);

        if(args.getString(GameSelectPriceActivity.ARGS_LAUNCHINGFROM)!=null){
            launchingFrom = args.getString(GameSelectPriceActivity.ARGS_LAUNCHINGFROM);
        }

        //Start Progressbar
        JoshApplication.startSpinnerProgress(mActivityContext, mActivityContext.getResources().getString(R.string.loading_game));

        mGameWebview = (WebView) findViewById(R.id.turup_rummygame_webview);
        mGameWebview.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("MyGamewebview", url);
                //view.loadUrl(url);
                return handleUrl(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                JoshApplication.closeSpinnerProgress(mActivityContext);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                finish();
                JoshApplication.showErrorDialog(mActivityContext, "Game Error", "Game not Loading!");
            }
        });

        mGameWebview.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("MyGamewebview", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId() );
                return true;
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                // TODO Auto-generated method stub
                Log.v("MyGamewebview", "invoked: onConsoleMessage() - " + sourceID + ":"
                        + lineNumber + " - " + message);
                super.onConsoleMessage(message, lineNumber, sourceID);
            }
        });

        if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mGameWebview.getSettings().setJavaScriptEnabled(true);
        mGameWebview.addJavascriptInterface(new MyGameJavaScriptInterface(mActivityContext), "Android");
        //mGameWebview.loadUrl("https://www.google.com/");
        if(launchingFrom !=null && launchingFrom.equalsIgnoreCase(LAUNCHING_FROM_MYOPENGAMES)){
            mGameWebview.loadUrl(JoshApplication.GAME_SERVER_BASE_URL + "/launch?wagerId=" + wagerId + "&ticketId=" + buyinTicketId + "&accessToken=" +
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext) + "&skinId=2"+
                    "&apiKey="+getResources().getString(R.string.josh_server_api_key)+"&gameType="+JoshApplication.getCurrentGameType());

        }else {
            mGameWebview.loadUrl(JoshApplication.GAME_SERVER_BASE_URL + "/launch?wagerId=" + wagerId + "&accessToken=" +
                    JoshApplication.currentLoginAuthTokenFromServer(mActivityContext) + "&skinId=2"+
                    "&apiKey="+getResources().getString(R.string.josh_server_api_key)+"&gameType="+JoshApplication.getCurrentGameType());
        }

        if(JoshApplication.isFirstTimeOpenMadeForGame){
            JoshApplication.sendFromInstallToGameAtFirstTimeEvent(mActivityContext,
                    JoshApplication.installKey(mActivityContext), ""+JoshApplication.userId(mActivityContext));
            JoshApplication.isFirstTimeOpenMadeForGame = false;
        }

        initReviewSystem();
    }

    private void initReviewSystem(){
        manager = ReviewManagerFactory.create(mActivityContext);
        manager.requestReviewFlow().addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if(task.isSuccessful()){
                    reviewInfo = task.getResult();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "In-App Request Failed");
                //goToLobby();
            }
        });
    }

    public class MyGameJavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        MyGameJavaScriptInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToastNative(String toastmsg) {
            Toast.makeText(mContext, toastmsg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void showHtmlPageLogsNative(String logmsg) {
            Log.d("HTMLPAGELOGS",logmsg );
        }

        @JavascriptInterface
        public void goToLobbyNative() {
            if (reviewInfo != null) {
                manager.launchReviewFlow(GameWebviewActivity.this, reviewInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(mContext, "Review Completed, Thank You!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.d(TAG, "Review Task Failed!");
                        }
                        goToLobby();
                    }
                });
            }
            else{
                goToLobby();
            }
        }

        @JavascriptInterface
        public void goToAddCashScreen() {
            goToAddCashScreenFromGame();
        }
    }

    private void goToLobby(){
        Intent intent = new Intent(GameWebviewActivity.this, LobbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
        finish();
    }

    private boolean handleUrl(String url){
        if (true) {/* any condition */
            // Returning false means that you are going to load this url in the webView itself
            return false;
        } else {
            // Returning true means that you need to handle what to do with the url
            // e.g. open web page in a Browser
            JoshApplication.toast(mActivityContext, "Failed to open url!");
            finish();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        mGameWebview.loadUrl("javascript:onBackButtonPressed();");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        JoshApplication.hideNavigationBarWithStatusBar(mActivityContext, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameWebview.loadUrl("javascript:onPause();");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameWebview.loadUrl("javascript:onResume();");
    }

    private void goToAddCashScreenFromGame(){
        finish();
        Intent addCashIntent = new Intent(mActivityContext, AddCashActivity.class);
        Bundle addCashIntentBundles = new Bundle();
        addCashIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        addCashIntentBundles.putString(GameSelectPriceActivity.ARGS_LAUNCHINGFROM, AddCashActivity.LAUNCHING_FROM_GAMEWEBVIEW);
        addCashIntent.putExtras(addCashIntentBundles);
        addCashIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mActivityContext.startActivity(addCashIntent);
        overridePendingTransition(JoshApplication.activityAnimationIn, JoshApplication.activityAnimationOut);
    }
}
