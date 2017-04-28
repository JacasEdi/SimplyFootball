package com.example.jacek.simplyfootball.viewmodel;

/**
 * Simple POJO class used as a model for a single football team inside the league table.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class TableDataItem
{
    int position;
    String name;
    int matchesPlayed;
    int wins;
    int draws;
    int losses;
    int goalsScored;
    int goalsConceded;
    int goalDifference;
    int points;

    public TableDataItem(int position, String name, int matchesPlayed, int wins, int draws, int losses,
                         int goalsScored, int goalsConceded, int goalDifference, int points)
    {
        this.position = position;
        this.name = name;
        this.matchesPlayed = matchesPlayed;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalsScored = goalsScored;
        this.goalsConceded = goalsConceded;
        this.goalDifference = goalDifference;
        this.points = points;
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getGoalsScored() {
        return goalsScored;
    }

    public void setGoalsScored(int goalsScored) {
        this.goalsScored = goalsScored;
    }

    public int getGoalsConceded() {
        return goalsConceded;
    }

    public void setGoalsConceded(int goalsConceded) {
        this.goalsConceded = goalsConceded;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(int goalDifference) {
        this.goalDifference = goalDifference;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "TableDataItem{" +
                "position='" + position + '\'' +
                ", name='" + name + '\'' +
                ", matchesPlayed=" + matchesPlayed +
                ", wins=" + wins +
                ", draws=" + draws +
                ", losses=" + losses +
                ", goalsScored=" + goalsScored +
                ", goalsConceded=" + goalsConceded +
                ", goalDifference='" + goalDifference + '\'' +
                ", points=" + points +
                '}';
    }
}
