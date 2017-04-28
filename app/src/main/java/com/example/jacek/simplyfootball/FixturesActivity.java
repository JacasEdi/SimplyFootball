package com.example.jacek.simplyfootball;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jacek.simplyfootball.adapters.FixturesListAdapter;
import com.example.jacek.simplyfootball.helper.AppConfig;
import com.example.jacek.simplyfootball.viewmodel.MatchDetails;

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
import java.util.Map;

/**
 * Activity class that represents business model of a Fixture Activity and binds to its layout file.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class FixturesActivity extends AppCompatActivity {

    // Static TAG variable used for debugging (logging)
    private static final String TAG = "FixturesActivity";

    //ArrayList for storing fixtures from API
    ArrayList<HashMap<String, String>> fixturesList;

    //HashMap for storing Team ID - Team Name pairs from the API
    HashMap<Integer, String> teamsMap;

    // Variables for listView and its adapter
    ExpandableListView expandableListView;
    FixturesListAdapter listAdapter;

    //Variables for listView content - header and child
    List<String> listDataHeader;
    List<List<MatchDetails>> listDataChild;

    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Map ID of each Scottish Championship team in the API to the name of that team
        mapApiData();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixtures);

        // Initialize fixturesList for holding all match data
        fixturesList = new ArrayList<>();

        // Initialize expandable list
        expandableListView = (ExpandableListView) findViewById(R.id.expandable_listview_fixtures);

        // Setting group indicator null for custom indicator
        expandableListView.setGroupIndicator(null);

        // Create a progress dialog
        pDialog = new ProgressDialog(this);

    }

    /** Retrieves fixtures from Soccerama API and adds each to the list of fixtures */
    private void getFixtures()
    {
        // Tag used to cancel the request
        String tag_string_req = "req_fixtures";

        // Set a message for the progress dialog
        pDialog.setMessage("Loading...");
        // Show the progress dialog
        pDialog.show();

        // Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.SOCCERAMA_API_MATCHES,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        //Log.d(TAG, "Response from Soccerama: " + response.toString());

                        try
                        {
                            Calendar c = Calendar.getInstance();
                            Date currentDate = c.getTime();

                            JSONObject rootObj = new JSONObject(response);

                            // Getting JSON Array node
                            JSONObject matchesObj = rootObj.getJSONObject("matches");
                            JSONArray fixturesArr = matchesObj.getJSONArray("data");

                            // looping through all fixtures in the array
                            for (int i = 0; i < fixturesArr.length(); i++)
                            {
                                JSONObject match = fixturesArr.getJSONObject(i);
                                JSONObject venue = fixturesArr.getJSONObject(i).getJSONObject("venue");

                                int homeTeamId = match.getInt("home_team_id");
                                int awayTeamId = match.getInt("away_team_id");
                                int homeTeamGoals = match.getInt("home_score");
                                int awayTeamGoals = match.getInt("away_score");
                                String venueName = venue.getString("name");
                                String matchDate = match.getString("starting_date");
                                String matchTime = match.getString("starting_time");

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date matchDateFormat = sdf.parse(matchDate);
                                Log.d(TAG, "MATCH DATE: " + matchDateFormat);

                                // temporary HashMap for storing single fixture while looping through JSONArray response
                                HashMap<String, String> fixture = new HashMap<>();

                                // adding each child node to HashMap key => value
                                fixture.put("home name", teamsMap.get(homeTeamId));
                                fixture.put("away name", teamsMap.get(awayTeamId));
                                if(matchDateFormat.before(currentDate))
                                {
                                    fixture.put("home goals", String.valueOf(homeTeamGoals));
                                    fixture.put("away goals", String.valueOf(awayTeamGoals));
                                }
                                else
                                {
                                    fixture.put("home goals", "?");
                                    fixture.put("away goals", "?");
                                }
                                fixture.put("venue name", venueName);
                                fixture.put("match date", matchDate);
                                fixture.put("match time", matchTime);

                                // adding fixture to list of fixtures
                                fixturesList.add(fixture);
                            }

                            Collections.sort(fixturesList, new MapComparator("match date"));

                            // Prepare data to be displayed in ExpandableListView
                            prepareListData();

                            // Create FixturesListAdapter object
                            listAdapter = new FixturesListAdapter(FixturesActivity.this, listDataHeader, listDataChild);

                            // Bind listAdapter to ExpandableListView
                            expandableListView.setAdapter(listAdapter);

                            setListener();

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.e(TAG, "JSON Error: " + e.getMessage());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e(TAG, "Retrieving Error for getFixtures: " + error.getMessage());
                        //TODO retry if fails so it doesn't freeze

                        getFixtures();
                    }
                }) {
        };

        // Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /** Prepares the header and child data to be displayed inside ExpandableListView */
    private void prepareListData()
    {
        listDataHeader = new ArrayList<String>();
        listDataChild = new ArrayList<List<MatchDetails>>();

        // HEADER SET UP //

        // Adding header data (teams and score)
        for (int i = 0; i < fixturesList.size(); i++)
        {
            // Get current object data from the list
            String homeTeam = fixturesList.get(i).get("home name");
            String awayTeam = fixturesList.get(i).get("away name");
            String homeGoals = fixturesList.get(i).get("home goals");
            String awayGoals = fixturesList.get(i).get("away goals");

            // Set header on a ListView
            listDataHeader.add(homeTeam + "   " + homeGoals + "-" + awayGoals + "   " + awayTeam);
        }


        // CONTENT (CHILD) SET UP //

        // List for storing all MatchDetails objects which will be then used as ListView children
        List<MatchDetails> matchDetailsList = new ArrayList<>();

        // Instance of single MatchDetails object for storing details of separate matches
        MatchDetails matchDetails;


        // Fill matchDetailsList with match data for every fixture from the API
        for (int i = 0; i < fixturesList.size(); i++)
        {
            // Get current object data from the list
            String date = fixturesList.get(i).get("match date");
            String time = fixturesList.get(i).get("match time");
            String venue = fixturesList.get(i).get("venue name");
            Log.d(TAG, "prepareListData VENUE NAME" + venue);

            // Create new MatchDetails object and pass all the data in its constructor
            matchDetails = new MatchDetails(date, time, venue);

            // Add this object to the list of MatchDetails objects
            matchDetailsList.add(matchDetails);
        }

        // Fill each child with relevant to its header item from matchDetailsList
        for(int i = 0; i < listDataHeader.size(); i++)
        {
            // add corresponding child to its header based on current position inside the loop
            listDataChild.add(i, matchDetailsList.subList(i, i+1));
        }

        // Close the progress dialog once finished with loading match data
        pDialog.dismiss();
    }


    /** Sets listener on ExpandableListView */
    private void setListener()
    {
        // Listener for clicking on the group (header) in the ListView
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(final ExpandableListView listview, View view,
                                        final int group_pos, long id) {
                listview.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        // ListView will automatically scroll to the position of selected group
                        listview.smoothScrollToPositionFromTop(group_pos, 0);
                    }
                });

                return false;
            }
        });

        // Listener for expanding group upon clicking on its header
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
        {
                    // Default position
                    int previousGroup = -1;

                    @Override
                    public void onGroupExpand(int groupPosition)
                    {
                        if (groupPosition != previousGroup)
                        {
                            // Collapse the expanded group
                            expandableListView.collapseGroup(previousGroup);
                        }

                        previousGroup = groupPosition;
                    }

        });

        // Listener for clicking on a child once the group is expanded
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView listview, View view,
                                        int groupPos, int childPos, long id) {
/*                Toast.makeText(
                        FixturesActivity.this,
                        "You clicked : " + adapter.getChild(groupPos, childPos),
                        Toast.LENGTH_SHORT).show();*/
                return false;
            }
        });
    }


    /** Retrieves list of teams from the API and adds their respective IDs and names to a HashMap */
    private void mapApiData()
    {
        // Tag used to cancel the request
        String tag_string_req = "req_teams";

        teamsMap = new HashMap<>();

        // Create a string request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.SOCCERAMA_API_TEAMS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject rootObj = new JSONObject(response);

                            // Getting JSON Array node
                            JSONArray teamsArr = rootObj.getJSONArray("data");

                            // Loop through all teams in the array
                            for (int i = 0; i < teamsArr.length(); i++)
                            {
                                JSONObject team = teamsArr.getJSONObject(i);

                                int teamId = team.getInt("id");
                                String teamName = team.getString("name");

                                teamsMap.put(teamId, teamName);
                            }

                            // Get match data from API once mapping is finished
                            getFixtures();
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
                        Log.e(TAG, "Retrieving Error for map API data: " + error.getMessage());
                        mapApiData();
                    }
                }) {
        };

        // Add the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /** Helper class for comparing two HashMaps for the purpose of sorting data inside ListView */
    private class MapComparator implements Comparator<Map<String, String>>
    {
        private final String key;

        public MapComparator(String key)
        {
            this.key = key;
        }

        public int compare(Map<String, String> first,
                           Map<String, String> second)
        {
            String firstValue = first.get(key);
            String secondValue = second.get(key);

            return firstValue.compareTo(secondValue);
        }
    }

}
