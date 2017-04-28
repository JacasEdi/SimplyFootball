package com.example.jacek.simplyfootball;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jacek.simplyfootball.adapters.FixturesListAdapter;
import com.example.jacek.simplyfootball.adapters.LeagueTableAdapter;
import com.example.jacek.simplyfootball.helper.AppConfig;
import com.example.jacek.simplyfootball.viewmodel.TableDataItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Activity class that represents business model of a League Table Activity and binds to its layout file.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class LeagueTableActivity extends AppCompatActivity
{
    // Static TAG variable used for debugging (logging)
    private static final String TAG = "LeagueTableActivity";

    ListView listView;
    LeagueTableAdapter tableAdapter;

    List<TableDataItem> tableDataList;

    TextView tvLegend;

    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_table);

        listView = (ListView) findViewById(R.id.lv_table);

        tvLegend = (TextView) findViewById(R.id.tv_table_legend);
        tvLegend.setMovementMethod(new ScrollingMovementMethod());

        // Create a progress dialog
        pDialog = new ProgressDialog(this);

        tableDataList = new ArrayList<>();

        getTableData();

    }

    /** Retrieves table data from Soccerama API */
    private void getTableData()
    {
        // Tag used to cancel the request
        String tag_string_req = "req_table";

        // Set a message for the progress dialog
        pDialog.setMessage("Loading...");

        // Show the progress dialog
        pDialog.show();

        // Create a string request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.SOCCERAMA_API_TABLE,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d(TAG, "Response from Soccerama: " + response.toString());

                        try
                        {
                            JSONObject rootObj = new JSONObject(response);

                            // Get JSON Array node
                            JSONArray dataArr = rootObj.getJSONArray("data");
                            JSONObject someObj = dataArr.getJSONObject(0);
                            JSONObject standingsObj = someObj.getJSONObject("standings");
                            JSONArray teamsArr = standingsObj.getJSONArray("data");

                            Log.d(TAG, "TEAMS ARRAY SIZE: " + teamsArr.length());

                            // Loop through all the fixtures inside the array
                            for (int i = 0; i < teamsArr.length(); i++)
                            {
                                JSONObject team = teamsArr.getJSONObject(i);
                                JSONObject teamInfo = teamsArr.getJSONObject(i).getJSONObject("team");

                                // Retrieve required fields for the team
                                int position = team.getInt("position");
                                String name = teamInfo.getString("name");
                                int matchesPlayed = team.getInt("overall_played");
                                int wins = team.getInt("overall_win");
                                int draws = team.getInt("overall_draw");
                                int losses = team.getInt("overall_loose");
                                int goalsScored = team.getInt("overall_goals_scored");
                                int goalsConceded = team.getInt("overall_goals_attempted");
                                String goalDifference = team.getString("goal_difference");
                                int points = team.getInt("points");

                                // Create new TableDataItem and pass all the variables
                                TableDataItem tdi = new TableDataItem(position, name, matchesPlayed,
                                        wins, draws, losses, goalsScored, goalsConceded,
                                        Integer.parseInt(goalDifference), points);

                                // Add item to list of TableDataItems
                                tableDataList.add(tdi);
                            }

                            // Sort list of teams based on position using Comparator object
                            Collections.sort(tableDataList, new Comparator<TableDataItem>() {
                                @Override
                                public int compare(TableDataItem teamA, TableDataItem teamB)
                                {
                                    int posA = teamA.getPosition();
                                    int posB = teamB.getPosition();

                                    return Integer.compare(posA, posB);
                                }
                            });

                            // Create tableAdapter and pass it a layout file and list of table data
                            tableAdapter = new LeagueTableAdapter(LeagueTableActivity.this,
                                    R.layout.item_table, tableDataList);

                            listView.setAdapter(tableAdapter);

                            // Close progress dialog
                            pDialog.dismiss();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.e(TAG, "JSON Error: " + e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e(TAG, "Retrieving Error for league table: " + error.getMessage());

                        // Retry if error occurs while retrieving the data
                        getTableData();
                    }
                }) {
        };

        // Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
