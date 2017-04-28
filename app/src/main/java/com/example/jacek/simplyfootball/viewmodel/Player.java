package com.example.jacek.simplyfootball.viewmodel;

/**
 * Simple POJO class used as a model for a single Player object.
 * Created by Jacek Budzynski.
 * Last modified on 26/04/2017.
 */

public class Player {
    String name;
    int age;
    int number;
    String position;
    int appearances;
    int goals;
    int assists;
    int yellowCards;
    int redCards;

    public Player(String name, int age, int number, String position, int appearances,
                  int goals, int assists, int yellowCards, int redCards)
    {
        this.name = name;
        this.age = age;
        this.number = number;
        this.position = position;
        this.appearances = appearances;
        this.goals = goals;
        this.assists = assists;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
    }

    public Player()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getAppearances() {
        return appearances;
    }

    public void setAppearances(int appearances) {
        this.appearances = appearances;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(int yellowCards) {
        this.yellowCards = yellowCards;
    }

    public int getRedCards() {
        return redCards;
    }

    public void setRedCards(int redCards) {
        this.redCards = redCards;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", number=" + number +
                ", position='" + position + '\'' +
                ", appearances=" + appearances +
                ", goals=" + goals +
                ", assists=" + assists +
                ", yellowCards=" + yellowCards +
                ", redCards=" + redCards +
                '}';
    }
}


