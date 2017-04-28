package com.example.jacek.simplyfootball;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.jacek.simplyfootball.helper.AppConfig;

/**
 * Activity class used to load URL containing match reports and display its contents in a WebView.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class MatchReportActivity extends AppCompatActivity
{

    private WebView webView;

    //final String URL = "http://www.stmirren.com/match/first-team/match-reports";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_report);

        webView = (WebView) findViewById(R.id.web_view_match_report);

        //webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(AppConfig.MATCH_REPORTS_URL);
    }
}
