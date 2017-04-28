package com.example.jacek.simplyfootball.services;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.jacek.simplyfootball.helper.HttpHelper;
import com.example.jacek.simplyfootball.viewmodel.NewsItem;
import com.google.gson.Gson;

import java.io.IOException;

/**
 * Custom IntentService class for handling asynchronous requests in a single background thread.
 * It will be used to populate HomeScreenActivity's UI domain while offloading main application thread.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class MyService extends IntentService
{
    // Static TAG variable used for debugging (logging)
    private static final String TAG = "MyService";

    // Static variable used as a key for filtering and listening for the message in the activity
    public static final String MY_SERVICE_MESSAGE = "myServiceMessage";

    // Static variable used for identifying the message from the activity
    public static final String MY_SERVICE_PAYLOAD = "myServicePayload";


    public MyService()
    {
        super("MyService");
    }

    /** Receives an intent object and parses data from it to an array of NewsItem objects */
    protected void onHandleIntent(@Nullable Intent intent)
    {
        Uri uri = intent.getData();
        Log.i(TAG, "onHandleIntent: " + uri.toString());

        String response = null;
        try
        {
            response = HttpHelper.downloadUrl(uri.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        // Gson object for parsing JSON response to an array of POJO objects representing News Items
        Gson gson = new Gson();
        NewsItem[] newsItems = gson.fromJson(response, NewsItem[].class);


        Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
        messageIntent.putExtra(MY_SERVICE_PAYLOAD, newsItems);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(messageIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
