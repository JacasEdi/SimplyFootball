package com.example.jacek.simplyfootball.helper;

/**
 * Helper class for storing static variables to be used throughout the app.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class AppConfig
{
    public static final String MATCH_REPORTS_URL = "http://www.stmirren.com/match/first-team/match-reports";

    //SkySport API URL for St Mirren
    public static final String SKY_API_URL = "https://skysportsapi.herokuapp.com/sky/football/getteamnews/stmirren/v1.0/";

    //Soccerama API URL for St Mirren FC fixtures & results
    public static final String SOCCERAMA_API_MATCHES =
            "https://api.soccerama.pro/v1.2/teams/699/season/680?api_token=4tBGAuynwVKUGCKPPeFrq09fe4Et3jVEeR47JwJI47BokeBfacsTEZpq9qXy&include=venue";

    //Soccerama API URL for Scottish Championship league table
    public static final String SOCCERAMA_API_TABLE =
            "https://api.soccerama.pro/v1.2/standings/season/680?api_token=4tBGAuynwVKUGCKPPeFrq09fe4Et3jVEeR47JwJI47BokeBfacsTEZpq9qXy";

    //Soccerama API URL for St Mirren FC squad
    public static final String SOCCERAMA_API_SQUAD =
            "https://api.soccerama.pro/v1.2/players/team/699?api_token=4tBGAuynwVKUGCKPPeFrq09fe4Et3jVEeR47JwJI47BokeBfacsTEZpq9qXy";

    //Soccerama API URL for teams in Scottish Championship
    public static final String SOCCERAMA_API_TEAMS =
            "https://api.soccerama.pro/v1.2/teams/season/680?api_token=4tBGAuynwVKUGCKPPeFrq09fe4Et3jVEeR47JwJI47BokeBfacsTEZpq9qXy&include=venue";
}
