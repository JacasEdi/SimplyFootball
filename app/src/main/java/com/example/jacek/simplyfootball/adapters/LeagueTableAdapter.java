package com.example.jacek.simplyfootball.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jacek.simplyfootball.R;
import com.example.jacek.simplyfootball.viewmodel.TableDataItem;

import java.util.List;

/**
 * Adapter class for binding league table data to be displayed to the ListView that will contain it.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class LeagueTableAdapter extends ArrayAdapter<TableDataItem>
{

    public LeagueTableAdapter(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    // Constructor that takes context, layout file and list of TableDataItem as parameters
    public LeagueTableAdapter(Context context, int resource, List<TableDataItem> items)
    {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;

        // Inflate ListView layout
        if (v == null)
        {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_table, null);
        }

        // Get current item within the list
        TableDataItem tableItem = getItem(position);

        if (tableItem != null)
        {
            Log.d("LEAGUE TABLE ADAPTER", "INITIALIZING TEXT VIEWS");

            // Initialize all views inside the list item (row)
            TextView leaguePosition = (TextView) v.findViewById(R.id.tv_team_position);
            TextView name = (TextView) v.findViewById(R.id.tv_team_name_table);
            TextView played = (TextView) v.findViewById(R.id.tv_matches_played);
            TextView wins = (TextView) v.findViewById(R.id.tv_wins);
            TextView draws = (TextView) v.findViewById(R.id.tv_draws);
            TextView losses = (TextView) v.findViewById(R.id.tv_losses);
            TextView goalsScored = (TextView) v.findViewById(R.id.tv_goals_scored);
            TextView goalsConceded = (TextView) v.findViewById(R.id.tv_goals_conceded);
            TextView goalDifference = (TextView) v.findViewById(R.id.tv_goal_difference);
            TextView points = (TextView) v.findViewById(R.id.tv_points);

            // Set text on each view using data from tableItem
            leaguePosition.setText(String.valueOf(tableItem.getPosition()));
            name.setText(tableItem.getName());
            played.setText(String.valueOf(tableItem.getMatchesPlayed()));
            wins.setText(String.valueOf(tableItem.getWins()));
            draws.setText(String.valueOf(tableItem.getDraws()));
            losses.setText(String.valueOf(tableItem.getLosses()));
            goalsScored.setText(String.valueOf(tableItem.getGoalsScored()));
            goalsConceded.setText(String.valueOf(tableItem.getGoalsConceded()));
            goalDifference.setText(String.valueOf(tableItem.getGoalDifference()));
            points.setText(String.valueOf(tableItem.getPoints()));

        }

        return v;
    }

}