package com.teamtreehouse;

import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Players;
import com.teamtreehouse.model.Team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LeagueManager {
    private Team mTeam;
    private BufferedReader mReader;
    private Map<String, String> mMenu;


    // Method that displays menu options to the user
    public void LeagueManager(Team team) {
        mTeam = team;
        mReader = new BufferedReader(new InputStreamReader(System.in));
        mMenu = new HashMap<String, String>();
        mMenu.put("create", "Create a new team");
    }

    private Team promptTeamCreation() throws IOException {
        System.out.print("Enter the team's name: ");
        String teamName = mReader.readLine();
        System.out.print("Enter the coach's name: ");
        String coachName = mReader.readLine();
        return new Team(teamName, coachName);
    }


    public static void main(String[] args) {
        Player[] players = Players.load();
        System.out.printf("There are currently %d registered players.%n", players.length);

        public void promptAction () {
            // TODO:
        }

        // The  method provides a menu item that allows the Organizer to create a new team for the season
        public void printInstructions () {
            String command = "";

            do {
                try {
                    command = // promptAction();

                            switch (command) {
                                case "create":
                                    Team team = promptTeamCreation();
                                    break;
                                case "quit":
                                    System.out.println("Terminated the League manager program.");
                                    break;
                            } catch(IOException ioe){
                        System.out.println("Problem with input");
                        ioe.printStackTrace();
                    }
                    while (!command.equals("quit")) ;
                }
            }

        }
    }

}
