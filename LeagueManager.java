import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Players;
import com.teamtreehouse.model.Team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LeagueManager {
    private ArrayList<Team> mTeams = new ArrayList<>();
    private BufferedReader mReader = new BufferedReader(new InputStreamReader(System.in));
    private Map<String, String> mMenu;

    // Method that displays menu options to the user
    public LeagueManager() {
        mMenu = new HashMap<>();
        mMenu.put("create", "Create a new team");
        mMenu.put("add", "Add player to a team");
        mMenu.put("remove", "Remove a player from a team");
        mMenu.put("quit", "Quit the program");
    }

    // The  method provides a menu item that allows the Organizer to create a new team for the season
    public void runMenu() {
        String command = "";
        try {
            while (!command.equals("quit")) {
                printInstructions();
                System.out.print("What would you like to do: ");
                command = mReader.readLine();
                switch (command) {
                    case "create":
                        Team team = promptTeamCreation();
                        mTeams.add(team);
                        System.out.println("Team created succesfully: " + team.getTeamName());
                        break;
                    case "add":
                        if (!mTeams.isEmpty()) {
                            Team selectedTeam = selectTeam();
                            if (selectedTeam != null && selectedTeam.getPlayers().size() < 11) {
                                Player selectedPlayer = selectPlayer();
                                if (selectedPlayer != null) {
                                    selectedTeam.addPlayer(selectedPlayer);
                                    System.out.println("Player added to the team successfully.");
                                }
                            } else if (selectedTeam != null) {
                                System.out.println("This team already has 11 players.");
                            }
                        } else {
                            System.out.println("There are no available teams. Please create one first.");
                        }
                        addPlayerToTeam();
                        break;
                    case "remove":
                        removePlayerFromTeam();
                        break;
                    case "quit":
                        System.out.println("Exiting the League manager program...");
                        break;
                    default:
                        System.out.println("Unknown command, please try again.");
                }
            }
        } catch (IOException ioe) {
            System.out.println("Problem with input");
            ioe.printStackTrace();
        }
    }

    public void printInstructions() {
        System.out.println("\nMenu Options:");
        for (Map.Entry<String, String> option : mMenu.entrySet()) {
            System.out.printf("%s - %s%n", option.getKey(), option.getValue());
        }
    }

    private Team promptTeamCreation() throws IOException {
        System.out.print("Enter the team's name: ");
        String teamName = mReader.readLine();
        System.out.print("Enter the coach's name: ");
        String coachName = mReader.readLine();
        return new Team(teamName, coachName);
    }

    // Prompts user to enter the index number for the team they wish to select
    private Team selectTeam() throws IOException {
        System.out.println("Choose a team:");
        for (int i = 0; i < mTeams.size(); i++) {
            System.out.println(i + 1 + ", " + mTeams.get(i).getTeamName());
        }
        int teamIndex = Integer.parseInt(mReader.readLine()) - 1;

        // validate user input
        if (teamIndex >= 0 && teamIndex < mTeams.size()) {
            return mTeams.get(teamIndex);
        } else {
            System.out.println("Invalid team index. Please try again.");
            return selectTeam(); // recursively call selectTeam if the input is invalid
        }
    }

    // Select a player from the list of unassigned players
    private Player selectPlayer() throws IOException {
        System.out.println("Available players:");
        Player[] players = Players.load();
        ArrayList<Player> unassignedPlayers = new ArrayList<>();
        int index = 1;
        for (Player player : players) {
            if (!player.isAssigned()) {
                unassignedPlayers.add(player);
                System.out.println(index++ + ", " + player.getFirstName() + " " + player.getLastName());
            }
        }
        System.out.println("Select a player by index: ");
        int playerIndex = Integer.parseInt(mReader.readLine()) - 1;
        if (playerIndex >= 0 && playerIndex < unassignedPlayers.size()) {
            Player selectedPlayer = unassignedPlayers.get(playerIndex);
            selectedPlayer.setAssigned(true);
            return selectedPlayer;
        } else {
            System.out.println("Invalid index. Please try again.");
            return selectPlayer();
        }
    }

    public void addPlayerToTeam() throws IOException {
        Team selectedTeam = selectTeam();
        if (selectedTeam == null) {
            System.out.println("No team selected or no teams exist.");
            return;
        }

        // Check if the selected team has fewer than 11 players
        if (selectedTeam.getPlayers().size() >= 11) {
            // No more players can be added
            System.out.println("This team already has 11 players. No more players can be added.");
            return;
        }

        // display unassigned players and allow the user to select one
        Player selectedPlayer = selectPlayer();
        if (selectedPlayer == null) {
            System.out.println("No player selected or no available players.");
            return;
        }

        // Add the player to the team
        selectedTeam.getPlayers().add(selectedPlayer);
        selectedPlayer.setAssigned(true); // Mark the player as assigned to a team
        System.out.println("Player " + selectedPlayer.getFirstName() + " " + selectedPlayer.getLastName() + " succesfully added to " + selectedTeam.getTeamName() + ".");
    }

    private Player selectPlayerForRemoval(Team team) throws IOException {
        Set<Player> players = team.getPlayers();
        int index = 0;
        Map<Integer, Player> indeedPlayers = new HashMap<>();

        for (Player player : players) {
            indeedPlayers.put(++index, player);
            System.out.println(index + " - " + player.getFirstName() + " " + player.getLastName());
        }
        System.out.print("Select a player to remove by index: ");
        int playerIndex = Integer.parseInt(mReader.readLine());

        if (indeedPlayers.containsKey(playerIndex)) {
            return indeedPlayers.get(playerIndex);
        } else {
            System.out.println("Invalid index. Please try again.");
            return selectPlayerForRemoval(team);
        }
    }

    public void removePlayerFromTeam() throws IOException {
        Team selectedTeam = selectTeam();
        if (selectedTeam != null){
            Player playerToRemove = selectPlayerForRemoval(selectedTeam);
            if (playerToRemove != null){
                selectedTeam.getPlayers().remove(playerToRemove);
                playerToRemove.setAssigned(false);
                System.out.println("Player " + playerToRemove.getFirstName() + " " + playerToRemove.getLastName() + " has been removed from " + selectedTeam.getTeamName());
            }
        }
    }

    public static void main(String[] args) {

        Player[] players = Players.load();
        System.out.printf("There are currently %d registered players.%n", players.length);
        new LeagueManager().runMenu();

    }

}
