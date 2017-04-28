package com.example.jacek.simplyfootball.viewmodel;

/**
 * Simple POJO class used as a model for a single Match object.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class MatchDetails
{
    String date;
    String time;
    String venue;

    public MatchDetails(String date, String time, String venue)
    {
        this.date = date;
        this.time = time;
        this.venue = venue;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public String toString()
    {
        return date + " " + time + " " + venue;
    }
}
