import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Players;
import com.teamtreehouse.model.Team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LeagueManager {
    private TreeMap<String, Team> mTeams = new TreeMap<>();
    private BufferedReader mReader = new BufferedReader(new InputStreamReader(System.in));
    private Map<String, String> mMenu;

    // Method that displays menu options to the user
    public LeagueManager() {
        mMenu = new HashMap<>();
        mMenu.put("create", "Create a new team");
        mMenu.put("add", "Add player to a team");
        mMenu.put("remove", "Remove a player from a team");
        mMenu.put("height report", " iew a team's height report");
        mMenu.put("balance report", "View league balance report");
        mMenu.put("roster", "Print team roster");
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
                        promptTeamCreation();
                        break;
                    case "add":
                        addPlayerToTeam();
                        break;
                    case "remove":
                        removePlayerFromTeam();
                        break;
                    case "height report":
                        displayHeightReport();
                        break;
                    case "balance report":
                        displayLeagueBalanceReport();
                        break;
                    case "roster":
                        displayTeamRoster();
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

    private void promptTeamCreation() throws IOException {
        System.out.print("Enter the team's name: ");
        String teamName = mReader.readLine();
        System.out.print("Enter the coach's name: ");
        String coachName = mReader.readLine();
        Team newTeam = new Team(teamName, coachName);
        mTeams.put(teamName, newTeam);
        System.out.println("Team created successfully: " + teamName);
    }

    // Prompts user to enter the index number for the team they wish to select
    private Team selectTeam() throws IOException {
        if (mTeams.isEmpty()) {
            System.out.println("No teams available. Please create one first.");
            return null;
        }

        int index = 1;
        for (Map.Entry<String, Team> entry : mTeams.entrySet()) {
            System.out.println(index++ + " - " + entry.getKey());
        }

        System.out.println("Enter the index of the team to select: ");
        int teamIndex = Integer.parseInt(mReader.readLine()) - 1;

        // validate user input
        if (teamIndex >= 0 && teamIndex < mTeams.size()) {
            return new ArrayList<>(mTeams.values()).get(teamIndex);
        } else {
            System.out.println("Invalid team index. Please try again.");
            return selectTeam(); // recursively call selectTeam if the input is invalid
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

    public void removePlayerFromTeam() throws IOException {
        Team selectedTeam = selectTeam();
        if (selectedTeam != null) {
            Player playerToRemove = selectPlayerForRemoval(selectedTeam);
            if (playerToRemove != null) {
                selectedTeam.getPlayers().remove(playerToRemove);
                playerToRemove.setAssigned(false);
                System.out.println("Player " + playerToRemove.getFirstName() + " " + playerToRemove.getLastName() + " has been removed from " + selectedTeam.getTeamName());
            }
        } else {
            System.out.println("No team selected.");
        }
    }

    // Select a player from the list of unassigned players
    private Player selectPlayer() throws IOException {
        displayPlayersAlphabetically();
        System.out.println("Select a player by index:");
        int playerIndex = Integer.parseInt(mReader.readLine()) - 1;
        Player[] players = Players.load();
        ArrayList<Player> unassignedPlayers = new ArrayList<>();
        for (Player player : players) {
            if (!player.isAssigned()) {
                unassignedPlayers.add(player);
            }
        }
        if (playerIndex >= 0 && playerIndex < unassignedPlayers.size()) {
            Player selectedPlayer = unassignedPlayers.get(playerIndex);
            selectedPlayer.setAssigned(true);
            return selectedPlayer;
        } else {
            System.out.println("Invalid index. Please try again.");
            return selectPlayer();
        }
    }

    // Display Height Report
    private void displayHeightReport(Team team) {
        if (team == null || team.getPlayers().isEmpty()) {
            System.out.println("No players in the team or team not selected.");
            return;
        }

        // Define height ranges
        Set<Player> players = team.getPlayers();
        Map<String, ArrayList<Player>> heightGroups = new TreeMap<>();
        int[] heightRanges = {35, 40, 46, 50, 55, 60}; // Height treshold

        // Initializes players by height
        for (int i = 0; i < heightRanges.length - 1; i++) {
            heightGroups.get(heightRanges[i] + "-" + (heightRanges[i + 1] - 1), new ArrayList<>());
        }

        // Group players by height
        for (Player player : team.getPlayers()) {

            for (int i = 0; i < heightRanges.length - 1; i++) {
                if (player.getHeightInInches() >= heightRanges[i] && player.getHeightInInches() < heightRanges[i + 1]) {
                    String key = heightRanges[i] + "-" + (heightRanges[i + 1] - 1);
                    List<Player> list = heightGroups.get(key);
                    list.add(player);
                    break;
                }
            }
        }

        // Display height groups
        for (Map.Entry<String, List<Player>> entry : heightGroups.entrySet()) {
            System.out.println("Height range " + entry.getKey());
            for (Player p : entry.getValue()) {
                System.out.println(p.getFirstName() + " " + p.getLastName() + " - " + p.getHeightInInches() + " inches");
            }
        }
    }

        private Player selectPlayerForRemoval (Team team) throws IOException {
            displayPlayersAlphabetically();
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

        // Method to display players alphabetically
        private void displayPlayersAlphabetically () throws IOException {
            List<Player> players = new ArrayList<>(Arrays.asList(Players.load()));
            Collections.sort(players);

            System.out.println("Available Players:");
            for (Player player : players) {
                if (!player.isAssigned()) {
                    System.out.println(player.getLastName() + ", " + player.getFirstName() + " - Height: " + player.getHeightInInches() + " inches, Experience: " + (player.isPreviousExperience() ? "Yes" : "No"));
                }
            }
        }
    
    private void displayLeagueBalanceReport() {
    if (mTeams.isEmpty()) {
        System.out.println("No teams available. Please create some teams first.");
        return;
    }

    Map<String, int[]> balanceMap = new TreeMap<>(); // To store team names and counts of experienced/inexperienced players

    for (Map.Entry<String, Team> entry : mTeams.entrySet()) {
        String teamName = entry.getKey();
        Team team = entry.getValue();
        int[] counts = {0, 0}; // First index for experienced, second for inexperienced

        for (Player player : team.getTeamPlayers()) {
            if (player.isPreviousExperience()) {
                counts[0]++; // Increment experienced count
            } else {
                counts[1]++; // Increment inexperienced count
            }
        }

        balanceMap.put(teamName, counts);
    }

    // Displaying the results
    System.out.println("League Balance Report:");
    for (Map.Entry<String, int[]> teamEntry : balanceMap.entrySet()) {
        System.out.printf("Team: %s, Experienced Players: %d, Inexperienced Players: %d%n",
                teamEntry.getKey(), teamEntry.getValue()[0], teamEntry.getValue()[1]);
    }
}

    private void displayTeamRoster() {
    Team selectedTeam = selectTeam();
    if (selectedTeam == null) {
        System.out.println("No team selected or no teams exist.");
        return;
    }

    Set<Player> players = selectedTeam.getTeamPlayers();
    if (players.isEmpty()) {
        System.out.println("No players in this team.");
        return;
    }

    System.out.println("Roster for Team: " + selectedTeam.getTeamName());
    for (Player player : players) {
        System.out.printf("Name: %s %s, Height: %d inches, Experienced: %s%n",
                player.getFirstName(), player.getLastName(),
                player.getHeightInInches(), player.isPreviousExperience() ? "Yes" : "No");
    }
}


        public static void main (String[]args){

            Player[] players = Players.load();
            System.out.printf("There are currently %d registered players.%n", players.length);
            new LeagueManager().runMenu();

        }

    }
