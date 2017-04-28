package com.example.jacek.simplyfootball.adapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jacek.simplyfootball.MatchReportActivity;
import com.example.jacek.simplyfootball.R;
import com.example.jacek.simplyfootball.viewmodel.MatchDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Adapter class for binding fixtures data to be displayed to the ListView that will contain it.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class FixturesListAdapter extends BaseExpandableListAdapter
{
    // Static TAG variable used for debugging (logging)
    private static final String TAG = "FIXTURES_LIST_ADAPTER";
    private Context context;

    // List of String to be used as headers
    private List<String> header;

    // List of MatchDetails to be used as children
    private List<List<MatchDetails>> matchDetails;

    private FirebaseUser user;

    // Instance of a Firebase database
    private DatabaseReference usersDatabase;


    public FixturesListAdapter (Context context, List<String> listDataHeader, List<List<MatchDetails>> listChildData)
    {
        this.context = context;
        this.header = listDataHeader;
        this.matchDetails = listChildData;
    }

    public Object getChild(int groupPosition, int childPosititon)
    {
        // Return current MatchDetails (child) object
        return matchDetails.get(groupPosition).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        // Get instance of a single MatchDetails (Child) object
        MatchDetails matchDetails = (MatchDetails) getChild(groupPosition, childPosition);

        // Inflate the layout
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_fixtures_content, parent, false);
        }

        // Initialize Views from the layout file
        TextView tvDateTime = (TextView) convertView.findViewById(R.id.tv_date_time);
        TextView tvCompetition = (TextView) convertView.findViewById(R.id.tv_competition);
        TextView tvVenue = (TextView) convertView.findViewById(R.id.tv_venue);
        final Button btnReport = (Button) convertView.findViewById(R.id.btn_report);

        // Set text on each TextView in the layout
        tvDateTime.setText(matchDetails.getDate() + " at " + matchDetails.getTime());
        tvCompetition.setText("Scottish Championship");
        tvVenue.setText(matchDetails.getVenue());

        // Check subscription type in the database and hide Match Report button if Partial
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // Point database reference to the users node of Firebase database
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        usersDatabase.child(userId).child("subscriptionType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String subscriptionType = dataSnapshot.getValue(String.class);
                Log.d(TAG, "SUBSCRIPTION TYPE" + subscriptionType);

                btnReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if(subscriptionType.equalsIgnoreCase("Partial Subscription"))
                        {
                            Toast.makeText(context, "You need to be a full subscriber!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            context.startActivity(new Intent(context, MatchReportActivity.class));
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        return convertView;
    }

    public int getChildrenCount(int groupPosition) {

        // Return children count
        return matchDetails.get(groupPosition).size();
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

        // Inflate header layout
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_fixtures_header, parent, false);
        }

        // Initialize header view from the layout
        TextView tvHeader = (TextView) convertView.findViewById(R.id.tv_header_fixture);

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
