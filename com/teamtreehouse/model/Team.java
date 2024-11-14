package com.teamtreehouse.model;

import java.util.HashSet;
import java.util.Set;

public class Team {
    private String mTeamName;
    private String mCoachName;
    private Set<Player> players;

    public Team(String teamName, String coachName) {
        mTeamName = teamName;
        mCoachName = coachName;
        this.players = new HashSet<>();
    }

    public String getCoachName() {
        return mCoachName;
    }

    public void setCoachName(String coachName) {
        mCoachName = coachName;
    }

    public String getTeamName() {
        return mTeamName;
    }

    public void setTeamName(String teamName) {
        mTeamName = teamName;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }
}