package com.example.jacek.simplyfootball;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Activity class used to load URL containing football news and display its contents in a WebView.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class NewsActivity extends AppCompatActivity
{
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Initialize new Bundle object and get extra data stored inside it
        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            // Retrieve value of "url" String from Bundle object
            String url = extras.getString("url");

            // Load retrieved URL inside the WebView
            loadUrlInWebView(url);
        }
    }

    //* Loads contents of a URL from the parameter and displays them in a WebView */
    private void loadUrlInWebView(String url)
    {
        webView = (WebView) findViewById(R.id.web_view_news);

        // Initialize new WebViewClient that will retrieve contents of the URL
        webView.setWebViewClient(new WebViewClient());

        // Load URL from the parameter using webView
        webView.loadUrl(url);
    }

}

