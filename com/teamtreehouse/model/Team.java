package com.teamtreehouse.model;


public class Team {
    protected String mTeamName;
    protected String mCoachName;

    public Team(String teamName, String coachName){
        mTeamName = teamName;
        mCoachName = coachName;
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


    
}