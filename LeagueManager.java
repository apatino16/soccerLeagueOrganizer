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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class LeagueManager {
    private TreeMap<String, Team> mTeams = new TreeMap<>();
    private BufferedReader mReader = new BufferedReader(new InputStreamReader(System.in));
    private Map<String, String> mMenu;
    private List<Player> waitingList = new ArrayList<>();

    // Method that displays menu options to the user
    public LeagueManager() {
        mMenu = new HashMap<>();
        mMenu.put("create", "Create a new team");
        mMenu.put("add", "Add player to a team");
        mMenu.put("remove", "Remove a player from a team");
        mMenu.put("height report", " iew a team's height report");
        mMenu.put("balance report", "View league balance report");
        mMenu.put("roster", "Print team roster");
        mMenu.put("add to waiting list", "Add player to the waiting list");
        mMenu.put("rotate player", "Rotate a player from a team with one from the waiting list");
        mMenu.put("quit", "Quit the program");
        mMenu.put("auto build", "Automatically build fair teams");

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
                    case "auto build":
                        autoBuildTeams();
                        break;
                    case "add to waiting list":
                        addToWaitingList();
                        break;
                    case "rotate player":
                        rotatePlayerFromTeam();
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

    // Display available menu options to the user
    public void printInstructions() {
        System.out.println("\nMenu Options:");
        for (Map.Entry<String, String> option : mMenu.entrySet()) {
            System.out.printf("%s - %s%n", option.getKey(), option.getValue());
        }
    }

    // Prompts the user to create a new team and stores it in the TreeMap
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

     // Adds a player to a selected team, ensuring the team does not exceed 11 players
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

    // Allows user to remove a player from a selected team
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
    private void displayHeightReport() throws IOException {
        if (mTeams.isEmpty()) {
            System.out.println("No teams have been created yet.");
            return;
        }

        // Define height ranges
        String[] heightRanges = {"35-40", "41-46", "47-50", "51+"}; // Height treshold
        Map<String, Map<String, Integer>> teamHeightCounts = new TreeMap<>();

        // Initialize the map for each team
        for (Team team : mTeams.values()) {
            Map<String, Integer> heightMap = new TreeMap<>();
            for (String range : heightRanges) {
                heightMap.put(range, 0); // Initialize counts for each range
            }
            teamHeightCounts.put(team.getTeamName(), heightMap);
        }

        // Count players in each height range for each team 
        for (Team team : mTeams.values()) {
            for (Player player : team.getPlayers()) {
                String heightKey = determineHeightRange(player.getHeightInInches());
                Map<String, Integer> counts = teamHeightCounts.get(team.getTeamName());
                counts.put(heightKey, counts.get(heightKey) + 1);
            }
        }

        // Display height report for each team
        for (Map.Entry<String, Map<String, Integer>> entry : teamHeightCounts.entrySet()) {
            System.out.println("Team: " + entry.getKey());
            for (Map.Entry<String, Integer> heightEntry : entry.getValue().entrySet()) {
                System.out.println("Height range " + heightEntry.getKey() + ": " + heightEntry.getValue() + " player(s)");
            }
        }
    }

    // Group players by height
    private String determineHeightRange(int height) {
        if (height >= 35 && height <= 40) {
            return "35-40";
        } else if (height >= 41 && height <= 46) {
            return "41-46";
        } else if (height >= 47 && height <= 50) {
            return "47-50";
        } else {
            return "51+";
        }
    }

    // Selects a player for removal from a team
    private Player selectPlayerForRemoval(Team team) throws IOException {
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
    private void displayPlayersAlphabetically() throws IOException {
        List<Player> players = new ArrayList<>(Arrays.asList(Players.load()));
        Collections.sort(players);

        System.out.println("Available Players:");
        for (Player player : players) {
            if (!player.isAssigned()) {
                System.out.println(player.getLastName() + ", " + player.getFirstName() + " - Height: " + player.getHeightInInches() + " inches, Experience: " + (player.isPreviousExperience() ? "Yes" : "No"));
            }
        }
    }

     // Displays the league balance report detailing the number and percentage of experienced players per team
    private void displayLeagueBalanceReport() {
        if (mTeams.isEmpty()) {
            System.out.println("No teams available. Please create some teams first.");
            return;
        }

        System.out.println("League Balance Report:");
        for (Map.Entry<String, Team> entry : mTeams.entrySet()) {
            Team team = entry.getValue();
            int experiencedCount = 0;
            int totalPlayers = team.getPlayers().size();

            for (Player player : team.getPlayers()) {
                if (player.isPreviousExperience()) {
                    experiencedCount++;
                }
            }

            // Displaying the results
            double experiencedPercentage = totalPlayers > 0 ? (double) experiencedCount / totalPlayers * 100 : 0;
            System.out.printf("Team: %s\n", team.getTeamName());
            System.out.printf("Total Players: %d, Experienced Players: %d, Inexperienced Players: %d%n",
                    totalPlayers, experiencedCount, totalPlayers - experiencedCount);
            System.out.printf("Percentage of Experienced Players: %.2f%%\n\n", experiencedPercentage);
        }
    }

    // Displays the team roster, showing all players with their stats
    private void displayTeamRoster() throws IOException {
        Team selectedTeam = selectTeam();
        if (selectedTeam == null) {
            System.out.println("No team selected or no teams exist.");
            return;
        }

        Set<Player> players = selectedTeam.getPlayers();
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

    // Automatic Team Building Method
    private void autoBuildTeams() {
        List<Player> players = new ArrayList<>(Arrays.asList(Players.load())); // Load all players
        Collections.shuffle(players); // Shuffle to randomize the distribution

        // Check if there are enough teams
        if (mTeams.isEmpty()) {
            System.out.println("Not teams have been created. Please create at least two teams.");
            return;
        }

        // Distribute players
        Iterator<Player> playerIterator = players.iterator();

        while (playerIterator.hasNext()) {
            boolean playerAdded = false;

            for (Team team : mTeams.values()) {
                if (team.getPlayers().size() < 11) { // Check if the team has less than 11 players
                    team.getPlayers().add(playerIterator.next()); // Add player to the current team
                    playerAdded = true;
                    break;
                }
            }

            if (!playerAdded) {
                System.out.println("All teams are full. Unable to place additional players.");
                break;
            }
        }

        // Feedback to the user
        System.out.println("Teams have been built automatically.");
        for (Map.Entry<String, Team> entry : mTeams.entrySet()) {
            System.out.println("Team: " + entry.getKey() + " has " + entry.getValue().getPlayers().size() + " players.");
        }
    }

    // Adding players to the waiting list
    private void addToWaitingList() throws IOException {
        System.out.print("Enter player's first name: ");
        String firstName = mReader.readLine();
        System.out.print("Enter player's last name: ");
        String lastName = mReader.readLine();
        System.out.print("Enter player's height in inches: ");
        int height = Integer.parseInt(mReader.readLine());
        System.out.print("Does the player have previous experience? (yes/no): ");
        boolean hasExperience = "yes".equalsIgnoreCase(mReader.readLine());

        Player newPlayer = new Player(firstName, lastName, height, hasExperience);
        waitingList.add(newPlayer);
        System.out.println("Player added to the waiting list.");
        attemptToAssignPlayersFromWaitingList();
    }

    // Attempts to assign players from the waiting list to any available team space
    private void attemptToAssignPlayersFromWaitingList() {
        if (waitingList.isEmpty()) {
            System.out.println("No players in the waiting list.");
            return;
        }

        Iterator<Player> iterator = waitingList.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (addPlayerToAvailableTeam(player)) {
                System.out.println("Player " + player.getFirstName() + " " + player.getLastName() + " added to a team from the waiting list.");
                iterator.remove(); // Remove player from the waiting list after assigning to a team
            }
        }
    }

    // Attempts to add a player from the waiting list to a team that has space
    private boolean addPlayerToAvailableTeam(Player player) {
        for (Team team : mTeams.values()) {
            if (team.getPlayers().size() < 11) {
                team.addPlayer(player);
                player.setAssigned(true);
                return true;
            }
        }
        return false;
    }

    // Rotate Players from Waiting List
    private void rotatePlayerFromTeam() throws IOException {
        Team selectedTeam = selectTeam();
        if (selectedTeam == null) {
            System.out.println("No team selected or no teams exist.");
            return;
        }

        if (selectedTeam.getPlayers().isEmpty()) {
            System.out.println("No players in this team to rotate.");
            return;
        }

        Player playerToRemove = selectPlayerForRemoval(selectedTeam);
        if (playerToRemove == null) {
            System.out.println("No player selected for removal.");
            return;
        }

        // Remove the player from the team
        selectedTeam.getPlayers().remove(playerToRemove);
        System.out.println("Player " + playerToRemove.getFirstName() + " " + playerToRemove.getLastName() + " has been removed from " + selectedTeam.getTeamName());

        // Attempt to add a player from the waiting list
        if (!waitingList.isEmpty()) {
            Player playerToAdd = waitingList.remove(0);  // Remove the first player from the waiting list
            selectedTeam.getPlayers().add(playerToAdd);
            playerToAdd.setAssigned(true);
            System.out.println("Player " + playerToAdd.getFirstName() + " " + playerToAdd.getLastName() + " has been added to " + selectedTeam.getTeamName() + " from the waiting list.");
        } else {
            System.out.println("No players on the waiting list to add to the team.");
        }
    }

    // Main method to start the program
    public static void main(String[] args) {

        Player[] players = Players.load();
        System.out.printf("There are currently %d registered players.%n", players.length);
        new LeagueManager().runMenu();

    }

}
