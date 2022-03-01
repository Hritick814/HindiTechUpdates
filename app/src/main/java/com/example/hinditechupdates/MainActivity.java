package com.example.hinditechupdates;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

public class MainActivity extends AppCompatActivity {

    String websiteURL = "https://www.hinditechupdates.tech"; // sets web url
    private WebView webview;
    SwipeRefreshLayout mySwipeRefreshLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://raw.githubusercontent.com/Hritick814/HindiTechUpdates/master/updates/update-changelog.json")
                .start();

        if (!CheckNetwork.isInternetAvailable(this)) //returns true if internet available
        {
            //if there is no internet do this
            setContentView(R.layout.activity_main);
            //Toast.makeText(this,"No Internet Connection, Chris",Toast.LENGTH_LONG).show();

            new AlertDialog.Builder(this) //alert the person knowing they are about to close
                    .setTitle("No internet connection available")
                    .setMessage("Please Check you're Mobile data or Wifi network.")
                    .setPositiveButton("Ok", (dialog, which) -> finish())
                    //.setNegativeButton("No", null)
                    .show();

        } else {
            //Webview stuff
            webview = findViewById(R.id.webView);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setDomStorageEnabled(true);
            webview.getSettings().setAppCacheEnabled(true);
            webview.getSettings().setLoadsImagesAutomatically(true);
            webview.getSettings().setBuiltInZoomControls(true);
            webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
            webview.loadUrl(websiteURL);
            webview.setWebViewClient(new WebViewClientDemo());

        }
        //Swipe to refresh functionality
        mySwipeRefreshLayout = this.findViewById(R.id.swipeContainer);

        mySwipeRefreshLayout.setOnRefreshListener(
                () -> webview.reload()
        );
    }


    private class WebViewClientDemo extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //Log.i("URLClick","PASS");
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "You have not installed the required app", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }


    //set back button functionality
    @Override
    public void onBackPressed() { //if user presses the back button do this
        if (webview.isFocused() && webview.canGoBack()) { //check if in webview and the user can go back
            webview.goBack(); //go back in webview
        } else { //do this if the webview cannot go back any further

            new AlertDialog.Builder(this) //alert the person knowing they are about to close
                    .setTitle("EXIT")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}

class CheckNetwork {

    private static final String TAG = CheckNetwork.class.getSimpleName();

    public static boolean isInternetAvailable(Context context) {
        NetworkInfo info = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null) {
            Log.d(TAG, "no internet connection");
            return false;
        } else {
            if (info.isConnected()) {
                Log.d(TAG, " internet connection available...");
            } else {
                Log.d(TAG, " internet connection");
            }
            return true;

        }
    }
}
