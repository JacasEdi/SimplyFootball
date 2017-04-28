package com.example.jacek.simplyfootball.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jacek.simplyfootball.HomeScreenActivity;
import com.example.jacek.simplyfootball.MatchReportActivity;
import com.example.jacek.simplyfootball.NewsActivity;
import com.example.jacek.simplyfootball.R;
import com.example.jacek.simplyfootball.viewmodel.NewsItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Adapter class for binding news data to be displayed to the ListView that will contain it.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>
{
    private List<NewsItem> newsList;
    private Context mContext;

    FirebaseUser user;
    DatabaseReference usersDatabase;

    public NewsAdapter(Context context, List<NewsItem> news) {
        this.mContext = context;
        this.newsList = news;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        int layoutId = R.layout.list_item;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NewsAdapter.ViewHolder holder, int position)
    {
        final NewsItem news = newsList.get(position);

        try
        {
            // Populate current ListView item with news title and description
            holder.tvTitle.setText(news.getTitle().trim());
            holder.tvDescription.setText(news.getShortdesc().trim());

            // Get image associated with the news
            String imageFile = news.getImgsrc();

            // Draw the retrieved image
            InputStream inputStream = mContext.getAssets().open(imageFile);
            Drawable d = Drawable.createFromStream(inputStream, null);

            // Populate imageView with the image
            holder.imageView.setImageDrawable(d);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // Check subscription type in the database and hide Match Report button if Partial
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // Point database reference to the users node of Firebase database
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        usersDatabase.child(userId).child("subscriptionType").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Get value of subscriptionType from user's node inside Firebase database
                final String subscriptionType = dataSnapshot.getValue(String.class);
                Log.d(TAG, "SUBSCRIPTION TYPE" + subscriptionType);

                // Either show the toast or open the news depending on the value of subscription type
                holder.mView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(subscriptionType.equalsIgnoreCase("Partial Subscription"))
                        {
                            Toast.makeText(mContext, "You need to be a full subscriber!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent intent = new Intent(mContext, NewsActivity.class);
                            intent.putExtra("url", news.getLink());
                            mContext.startActivity(intent);
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

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView tvTitle;
        public TextView tvDescription;
        public ImageView imageView;
        public View mView;

        public ViewHolder(View itemView)
        {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvNewsTitle);
            tvDescription = (TextView) itemView.findViewById(R.id.tvNewsDescription);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            mView = itemView;
        }
    }
}
