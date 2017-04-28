package com.example.jacek.simplyfootball.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.jacek.simplyfootball.R;
import com.example.jacek.simplyfootball.viewmodel.Player;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Adapter class for binding players data to be displayed to the ListView that will contain it.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class PlayersListAdapter extends BaseExpandableListAdapter
{
    // Static TAG variable used for debugging (logging)
    private static final String TAG = "PLAYERS_LIST_ADAPTER";

    private Context context;

    // List of String type headers
    private List<String> header;

    // List of MatchDetails class children
    private List<List<Player>> playersDetails;

    // Declare the FirebaseUser object
    private FirebaseUser user;

    // Instance of a Firebase database
    private DatabaseReference usersDatabase;


    public PlayersListAdapter (Context context, List<String> listDataHeader, List<List<Player>> listChildData)
    {
        this.context = context;
        this.header = listDataHeader;
        this.playersDetails = listChildData;
    }

    public Object getChild(int groupPosition, int childPosititon)
    {
        // Return current MatchDetails (child) object
        return playersDetails.get(groupPosition).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        // Get instance of a single Player (Child) object
        Player player = (Player) getChild(groupPosition, childPosition);

        // Inflate layout
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_players_content, parent, false);
        }

        // Initialize Views from the layout file
        TextView tvPosition = (TextView) convertView.findViewById(R.id.tv_player_position);
        TextView tvAge = (TextView) convertView.findViewById(R.id.tv_player_age);
        TextView tvAppearances = (TextView) convertView.findViewById(R.id.tv_player_appearances);
        TextView tvGoals = (TextView) convertView.findViewById(R.id.tv_player_goals);
        TextView tvAssists = (TextView) convertView.findViewById(R.id.tv_player_assists);
        TextView tvYellowCards = (TextView) convertView.findViewById(R.id.tv_player_yellow_cards);
        TextView tvRedCards = (TextView) convertView.findViewById(R.id.tv_player_red_cards);

        // Set text on each TextView inside the layout
        tvPosition.setText("Position: " + player.getPosition());
        tvAge.setText("Age: " + player.getAge());
        tvAppearances.setText("Appearances: " + player.getAppearances());
        tvGoals.setText("Goals: " + player.getGoals());
        tvAssists.setText("Assists: " + player.getAssists());
        tvYellowCards.setText("Yellow cards: " + player.getYellowCards());
        tvRedCards.setText("Red cards: " + player.getRedCards());

        // Get instance of currently logged in user
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get value of logged in user's id
        String userId = user.getUid();

        // Point database reference to the users node of Firebase database
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        usersDatabase.child(userId).child("subscriptionType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String subscriptionType = dataSnapshot.getValue(String.class);
                Log.d(TAG, "SUBSCRIPTION TYPE" + subscriptionType);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        return convertView;
    }

    public int getChildrenCount(int groupPosition) {

        // Return children count
        return playersDetails.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        // Get header position
        return this.header.get(groupPosition);
    }

    @Override
    public int getGroupCount() {

        // Get header size
        return this.header.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        // Get header title
        String headerTitle = (String) getGroup(groupPosition);

        // Inflate header layout and set text on it
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_players_header, parent, false);
        }

        // Initialize header view from the layout
        TextView tvHeader = (TextView) convertView.findViewById(R.id.tv_header_player);

        // Set text on the header
        tvHeader.setText(headerTitle);

        // Change the arrow icon depending on whether group is expanded or not
        if (isExpanded)
        {
            tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_arrow_up, 0);
        }
        else
        {
            tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_arrow_down, 0);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

