package com.example.jacek.simplyfootball;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.jacek.simplyfootball.adapters.NewsAdapter;
import com.example.jacek.simplyfootball.helper.AppConfig;
import com.example.jacek.simplyfootball.helper.NetworkHelper;
import com.example.jacek.simplyfootball.services.MyService;
import com.example.jacek.simplyfootball.viewmodel.NewsItem;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Activity class that represents business model of a Home Screen Activity and binds to its layout file.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Static TAG variable used for debugging (logging)
    private static final String TAG = "HomeScreenActivity";

    private final String JSON_URL = AppConfig.SKY_API_URL;

    // For accessing data inside Drawer header
    private TextView txtName;

    // Instance of a progress dialog
    ProgressDialog pDialog;

    // Declare the FirebaseAuth and AuthStateListener objects.
    private FirebaseAuth firebaseAuth;

    // List of news items
    List<NewsItem> newsList;

    // Adapter for list of news items to be used with RecyclerView
    NewsAdapter newsAdapter;

    RecyclerView mRecyclerView;

    // Variable for flagging network connectivity
    private boolean networkOk;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Array of news items retrieved from an intent
            NewsItem[] newsItems = (NewsItem[]) intent.getParcelableArrayExtra(MyService.MY_SERVICE_PAYLOAD);

            // Convert newsItems array into List<NewsItem>
            newsList = Arrays.asList(newsItems);

            // Initialize NewsAdapter and bind it to mRecyclerView using displayDataItems method
            displayDataItems(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Initialize FirebaseAuth object
        firebaseAuth = FirebaseAuth.getInstance();

        // Set up toolbar and navigation drawer when activity starts
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize app drawer and bind it to the layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Initialize drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        // Initialize navigationView and bind it to the corresponding element
        // inside activity_home_screen layout and set onItemSelectedListener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the header view on the drawer
        View header = navigationView.getHeaderView(0);

        // Set name field on the drawer
        txtName = (TextView) header.findViewById(R.id.tv_name);

        // Display currently logged in user inside the drawer
        txtName.setText(firebaseAuth.getCurrentUser().getEmail());

        // Get menu from navigationView
        Menu menu = navigationView.getMenu();

        // Bind mRecyclerView variable to corresponding layout
        mRecyclerView = (RecyclerView) findViewById(R.id.rvItems);

        // Create a progress dialog
        pDialog = new ProgressDialog(this);

        // Check network connectivity
        networkOk = NetworkHelper.hasNetworkStatus(this);

        // Either load data from the API or inform the user that device is offline and stop execution
        if(networkOk)
        {
            Intent intent = new Intent(this, MyService.class);
            intent.setData(Uri.parse(JSON_URL));
            startService(intent);
        }
        else
        {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }

        // Set a message for the progress dialog
        pDialog.setMessage("Loading...");
        // Show the progress dialog
        pDialog.show();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver, new IntentFilter(MyService.MY_SERVICE_MESSAGE));
    }


    /** Initializes NewsAdapter and binds it to the RecyclerView */
    private void displayDataItems(String param)
    {
        pDialog.dismiss();

        if (newsList != null)
        {
            newsAdapter = new NewsAdapter(this, newsList);
            mRecyclerView.setAdapter(newsAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    /** Handles selection of different options from the top-right menu of the app bar */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update_details) {
            startActivity(new Intent(HomeScreenActivity.this, UpdateDetailsActivity.class));
        }
        else if (id == R.id.action_logout) {
            //End user session
            firebaseAuth.signOut();
            //Go back to home page
            startActivity(new Intent(HomeScreenActivity.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /** Handles selection of different menu items inside the drawer */
    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item)
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news)
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (id == R.id.nav_squad)
        {
            startActivity(new Intent(HomeScreenActivity.this, PlayersActivity.class));
            finish();
        }
        else if (id == R.id.nav_fixtures)
        {
            startActivity(new Intent(HomeScreenActivity.this, FixturesActivity.class));
            finish();
        }
        else if (id == R.id.nav_table)
        {
            startActivity(new Intent(HomeScreenActivity.this, LeagueTableActivity.class));
        }
        else if (id == R.id.nav_share)
        {
            final String URL = "https://play.google.com/store?hl=en_GB";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, URL);

            startActivity(Intent.createChooser(share, "Share the app using"));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mBroadcastReceiver);
    }
}
