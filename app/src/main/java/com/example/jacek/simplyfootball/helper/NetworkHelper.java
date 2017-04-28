package com.example.jacek.simplyfootball.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Helper class for determining network connection status on the device.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class NetworkHelper
{

    /** Determines Internet connection status on the device */
    public static boolean hasNetworkStatus(Context context)
    {
        // Get instance of ConnectivityManager class
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try
        {
            // Determine whether there is an active network connection using ConnectivityManager
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
