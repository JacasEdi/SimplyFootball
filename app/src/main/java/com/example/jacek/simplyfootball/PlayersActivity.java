package com.example.jacek.simplyfootball;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.jacek.simplyfootball.adapters.PlayersListAdapter;
import com.example.jacek.simplyfootball.viewmodel.Player;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity class that represents business model of a Players Activity and binds to its layout file.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class PlayersActivity extends AppCompatActivity {

    // Variables for listView and its adapter
    ExpandableListView expandableListView;
    PlayersListAdapter listAdapter;

    //Variables for listView content - header and child
    List<String> listDataHeader;
    List<List<Player>> listDataChild;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        // Initialize expandable list
        expandableListView = (ExpandableListView) findViewById(R.id.expandable_listview_players);

        // Setting group indicator null for custom indicator
        expandableListView.setGroupIndicator(null);

        // Create a progress dialog
        pDialog = new ProgressDialog(this);

        // Start async task that scrapes the data from the webpage
        new MyTask().execute();
    }


    /**
     * Helper class that retrieves table data from an external URL, creates players objects
     * and adds each of them to the list players.
     */
    private class MyTask extends AsyncTask<Void, Void, List<Player>>
    {
        List<Player> players;

        @Override
        protected void onPreExecute() {
            // Set a message for the progress dialog
            pDialog.setMessage("Loading...");
            // Show the progress dialog
            pDialog.show();

            super.onPreExecute();
        }

        @Override
        protected List<Player> doInBackground(Void... params)
        {
            Log.d("PLAYERS ACTIVITY", "do in background started");

            players = new ArrayList<>();

            try
            {
                final Document doc = Jsoup.connect("http://uk.soccerway.com/teams/scotland/saint-mirren-fc/1916/squad/").get();

                for (Element table : doc.select("tbody"))
                {
                    for (Element row : table.select("tr"))
                    {
                        Elements tds = row.select("td");

                        if(tds.size() >= 17)
                        {
                            Player p = new Player();

                            p.setName(tds.get(2).text());
                            if(!tds.get(4).text().isEmpty())
                                p.setAge(Integer.parseInt(tds.get(4).text()));
                            else
                                p.setAge(0);
                            if(!tds.get(0).text().isEmpty())
                                p.setNumber(Integer.parseInt(tds.get(0).text()));
                            else
                                p.setNumber(0);
                            p.setPosition(expandPosition(tds.get(5).text()));
                            if(!tds.get(7).text().isEmpty())
                                p.setAppearances(Integer.parseInt(tds.get(7).text()));
                            else
                                p.setAppearances(0);
                            if(!tds.get(12).text().isEmpty())
                                p.setGoals(Integer.parseInt(tds.get(12).text()));
                            else
                                p.setGoals(0);
                            if(!tds.get(13).text().isEmpty())
                                p.setAssists(Integer.parseInt(tds.get(13).text()));
                            else
                                p.setAssists(0);
                            if(!tds.get(14).text().isEmpty())
                                p.setYellowCards(Integer.parseInt(tds.get(14).text()));
                            else
                                p.setYellowCards(0);
                            if(!tds.get(15).text().isEmpty())
                                p.setRedCards(Integer.parseInt(tds.get(15).text()) +
                                            Integer.parseInt(tds.get(16).text()));
                            else
                                p.setRedCards(0);

                            Log.d("PLAYERS ACTIVITY", "Current Player: " + p.toString());

                            if(p.getNumber() !=0 && p.getAge() != 0)
                            {
                                players.add(p);
                            }
                        }
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            Log.d("PLAYERS ACTIVITY", "do in background finished");

            return players;
        }


        @Override
        protected void onPostExecute(List<Player> playersList)
        {
            prepareListData(playersList);

            // Create PlayersListAdapter object
            listAdapter = new PlayersListAdapter(PlayersActivity.this, listDataHeader, listDataChild);

            // Bind listAdapter to ExpandableListView
            expandableListView.setAdapter(listAdapter);

            setListener();

            pDialog.dismiss();
        }
    }

    /** Prepares data to be displayed inside the ListView */
    private void prepareListData(List<Player> playersList)
    {
        listDataHeader = new ArrayList<String>();
        listDataChild = new ArrayList<List<Player>>();

        // HEADER SET UP //

        // Adding header data (teams and score)
        for (int i = 0; i < playersList.size(); i++)
        {
            // Get current object data from the list
            int number = playersList.get(i).getNumber();
            String name = playersList.get(i).getName();

            // Set header on a ListView
            listDataHeader.add(number + ". " + name);
        }

        // Fill each child with relevant to its header item from playersList
        for(int i = 0; i < listDataHeader.size(); i++)
        {
            // add corresponding child to its header based on current position inside the loop
            listDataChild.add(i, playersList.subList(i, i+1));
        }
    }


    /** Sets listeners on ExpandableListView */
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

    /**
     * Receives a letter that describes player's position, expands it to full name of that position
     * and returns expanded name to the caller.
    */
    private String expandPosition(String shortPosition)
    {
        switch(shortPosition){
            case "G":
                return "Goalkeeper";
            case "D":
                return "Defender";
            case "M":
                return "Midfielder";
            case "A":
                return "Forward";
        }

        return "";
    }

}
