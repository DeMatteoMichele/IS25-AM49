package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.model.exceptions.NotEnoughPlayerException;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents a leaderboard for tracking and managing player rankings in a game.
 */
public class LeaderBoard {
    /**
     * A mapping between Player instances and their corresponding leaderboard scores.
     */
    private final HashMap<Player, Integer> mapLeaderboard;
    /**
     * Stores a mapping of disconnected players' IDs to their corresponding Player objects.
     */
    private final HashMap<Integer, Player> disconnectedPlayers;
    /**
     * Represents a leaderboard as a list of player-score entries, sorted or unsorted.
     */
    private final ArrayList<Map.Entry<Player, Integer>> arrayLeaderboard;
    /**
     * Represents a leaderboard containing a list of optional player entries.
     */
    private final ArrayList<Optional<Player>> showableLeaderBoard;
    /**
     * Represents the level of the leaderboard, potentially defining its scale or tier.
     */
    private final int level;
    /**
     * Represents the fixed number of cells in the leaderboard structure.
     */
    private final int numberOfCells;


    /**
     * Initializes the leaderboard based on the specified game mode.
     *
     * @param mode the game mode which determines the leaderboard configuration; must be LEVEL_1 or LEVEL_2.
     * @throws GameSettingsException if an invalid game mode is provided.
     */
    public LeaderBoard(GameMode mode) {
        mapLeaderboard = new HashMap<>();
        disconnectedPlayers = new HashMap<>();
        arrayLeaderboard = new ArrayList<>();
        switch (mode) {
            case FLIGHT_MODE, LEVEL_1 -> {
                level = 1;
                numberOfCells = 18;
            }
            case LEVEL_2 -> {
                level = 2;
                numberOfCells = 24;
            }
            default -> throw new GameSettingsException("Error, the Game mode  must be LEVEL_1 or LEVEL_2");

        }
        int i;
        showableLeaderBoard = new ArrayList<>(numberOfCells);
        for (i = 0; i < numberOfCells; i++) {
            showableLeaderBoard.add(Optional.empty());
        }
    }

    /**
     * Adds a player to the leaderboard based on the current game level and configuration.
     *
     * @param player the player to add to the leaderboard
     * @throws GameSettingsException if the game mode is invalid or the leaderboard exceeds the player limit
     */
    //per implementarlo devo prima capire come verrano implementate le posizioni nel configurateflight
    public void add(Player player) {
        switch (arrayLeaderboard.size()) {
            case 3 -> {
                mapLeaderboard.put(player, 0);
                arrayLeaderboard.add(Map.entry(player, 0));
                showableLeaderBoard.set(0, Optional.of(player));

            }
            case 2 -> {
                mapLeaderboard.put(player, 1);
                arrayLeaderboard.add(Map.entry(player, 1));
                showableLeaderBoard.set(1, Optional.of(player));

            }
            case 1 -> {
                if (level == 1) {
                    mapLeaderboard.put(player, 2);
                    arrayLeaderboard.add(Map.entry(player, 2));
                    showableLeaderBoard.set(2, Optional.of(player));
                } else if (level == 2) {
                    mapLeaderboard.put(player, 3);
                    arrayLeaderboard.add(Map.entry(player, 3));
                    showableLeaderBoard.set(3, Optional.of(player));
                } else
                    throw new GameSettingsException("Error, the Game mode  must be LEVEL_1 or LEVEL_2");

            }
            case 0 -> {
                if (level == 1) {
                    mapLeaderboard.put(player, 4);
                    arrayLeaderboard.add(Map.entry(player, 4));
                    showableLeaderBoard.set(4, Optional.of(player));
                } else if (level == 2) {
                    mapLeaderboard.put(player, 6);
                    arrayLeaderboard.add(Map.entry(player, 6));
                    showableLeaderBoard.set(6, Optional.of(player));
                } else
                    throw new GameSettingsException("Error, the Game mode  must be LEVEL_1 or LEVEL_2");

            }
            default -> throw new GameSettingsException("You cannot insert more than 4 players");

        }
    }

    /**
     * Retrieves the number of days a player has spent in the leaderboard.
     *
     * @param player the player whose days are to be retrieved
     * @return the number of days the player has in the leaderboard
     * @throws IllegalSetOfNextPlayerException if the player is not in the leaderboard
     */
    public int getDays(Player player) {
        if (mapLeaderboard.containsKey(player))
            return mapLeaderboard.get(player);
        else
            throw new IllegalSetOfNextPlayerException("This player is not in the leaderboard");
    }

    /**
     * Finds the index of a player in the showable leaderboard.
     *
     * @param player The player to find in the showable leaderboard.
     * @return The index of the player in the showable leaderboard.
     * @throws IllegalSetOfNextPlayerException If the player is not in the showable leaderboard.
     */
    private int findIndexOfPlayerInShowableLeaderBoard(Player player) {
        int indexOfPlayer = showableLeaderBoard.indexOf(Optional.of(player));
        if (indexOfPlayer == -1)
            throw new IllegalSetOfNextPlayerException("This player is not in the leaderboard");
        else
            return indexOfPlayer;
    }

    /**
     * Decreases the days of a player in the showable leaderboard and adjusts their position.
     *
     * @param player the player whose days on the leaderboard are to be adjusted
     * @param daysModified the number of days to modify; can be negative or positive
     * @return the total position change of the player in the leaderboard
     */
    private int decreaseDaysFromShowableLeaderBoard(Player player, int daysModified) {
        int positionChanged = 0;
        int index = findIndexOfPlayerInShowableLeaderBoard(player);
        int sign = Integer.signum(daysModified);
        Optional<Player> temp = showableLeaderBoard.get(index);
        showableLeaderBoard.set(index, Optional.empty());
        while (daysModified != 0) {
            if (showableLeaderBoard.get(moduleNumberOfCells(index + sign)).isEmpty()) {
                daysModified = daysModified - sign;
                positionChanged = positionChanged + sign;
            } else if (showableLeaderBoard.get(moduleNumberOfCells(index + sign)).isPresent()) {
                positionChanged = positionChanged + sign;
            }
            index = index + sign;
        }
        showableLeaderBoard.set(moduleNumberOfCells(index), temp);
        return positionChanged;
    }

    /**
     * Modifies the flight days of a player in the leaderboard.
     * @param player the player whose flight days are to be modified
     * @param daysModified the number of days to be added or removed from the player's flight days
     * @throws IllegalSetOfNextPlayerException if the player is not present in the leaderboard
     */
    // days può essere maggiore o minore di zero
    public void modifyFlightDays(Player player, int daysModified) {
        if (mapLeaderboard.containsKey(player)) {
            int tmpDays = mapLeaderboard.get(player);
            int daysToChange = decreaseDaysFromShowableLeaderBoard(player, daysModified);
            Map.Entry<Player, Integer> entryBefore = arrayLeaderboard.set(getPos(player) - 1, Map.entry(player, tmpDays + daysToChange));
            mapLeaderboard.put(player, tmpDays + daysToChange);
            sort();
        } else
            throw new IllegalSetOfNextPlayerException("This player is not in the leaderboard");
    }

    /**
     * Removes a player from the leaderboard, updating associated data structures.
     *
     * @param player the player to be removed from the leaderboard
     * @throws NotEnoughPlayerException if the leaderboard becomes empty after removing the player
     * @throws GameSettingsException if the player is not found in the leaderboard
     */
    public void remove(Player player) {
        if (mapLeaderboard.containsKey(player)) {
            int tmpDays = mapLeaderboard.get(player);
            mapLeaderboard.remove(player);
            arrayLeaderboard.remove(Map.entry(player, tmpDays));
            showableLeaderBoard.set(findIndexOfPlayerInShowableLeaderBoard(player), Optional.empty());
            sort();
            if (mapLeaderboard.isEmpty())
                throw new NotEnoughPlayerException("the leaderboard has zero player left, going in finish state");
        } else
            throw new GameSettingsException("This player is not in the leaderboard");
    }

    /**
     * Sorts the leaderboard array in descending order based on player values.
     */
    private void sort() {
        arrayLeaderboard.sort((p1, p2) -> Integer.compare(p2.getValue(), p1.getValue()));
    }

    /**
     * Retrieves the total number of cells in the leaderboard.
     * @return the number of cells in the leaderboard
     */
    public int getNumberOfCells() {
        return numberOfCells;
    }

    /**
     * Retrieves the top player in the leaderboard.
     *
     * @return the leader player from the leaderboard
     * @throws IllegalSetOfNextPlayerException if the leaderboard is empty
     */
    public Player getLeader() {
        if (mapLeaderboard.isEmpty())
            throw new IllegalSetOfNextPlayerException("The leader board is empty");
        else
            return arrayLeaderboard.getFirst().getKey();
    }

    /**
     * Retrieves the position of the specified player in the leaderboard.
     *
     * @param player the player whose position is to be retrieved
     * @return the position of the player in the leaderboard, starting from 1
     * @throws IllegalSetOfNextPlayerException if the player is not present in the leaderboard
     */
    public int getPos(Player player) {
        if (mapLeaderboard.containsKey(player)) {
            int tmpDays = mapLeaderboard.get(player);
            return arrayLeaderboard.indexOf(Map.entry(player, tmpDays)) + 1;
        } else
            throw new IllegalSetOfNextPlayerException("This player is not in the leaderboard");
    }


    public Stream<Map.Entry<Player, Integer>> stream() {
        return arrayLeaderboard.stream();
    }

    /**
     * Returns the size of the leaderboard if internal collections are consistent.
     * @return the number of elements in the leaderboard.
     * @throws GameSettingsException if the internal collections are inconsistent.
     */
    public int size() {
        if (arrayLeaderboard.size() == mapLeaderboard.size())
            return arrayLeaderboard.size();
        else
            throw new GameSettingsException("Something went wrong while adding or removing the players");
    }

    /**
     * Retrieves the player located at the given leaderboard position.
     *
     * @param pos the position in the leaderboard, starting from 1
     * @return the player at the specified position
     * @throws IndexOutOfBoundsException if the position is not valid
     */
    // 1 <= pos <= 4
    public Player getPlayerInPosition(int pos) {
        if (pos <= arrayLeaderboard.size() && pos > 0)
            return arrayLeaderboard.get(pos - 1).getKey();
        else
            throw new IndexOutOfBoundsException("Error, the given position does not exist");
    }

    /**
     * Retrieves the first connected player starting from the given position.
     *
     * @param position the starting position to search from; must be greater than 0 and less than or equal to the leaderboard size
     * @return the first connected player found starting from the given position
     * @throws IndexOutOfBoundsException if the position is invalid
     * @throws IllegalSetOfNextPlayerException if no connected player is found after the given position
     */
    public Player getPlayerConnectedStartingFromThisPosition(int position) {
        if (position <= 0 || position > size()) {
            throw new IndexOutOfBoundsException("Error, the given position does not exist");
        }
        while (position <= size()) {
            Player player = getPlayerInPosition(position);
            if (!isDisconnected(player))
                return getPlayerInPosition(position);
            position++;
        }
        throw new IllegalSetOfNextPlayerException("No connected player found next to the given position");
    }


    /**
     * Checks whether the leaderboard is empty.
     *
     * @return true if the leaderboard is empty; otherwise throws a GameSettingsException.
     */
    public boolean isEmpty() {
        if (arrayLeaderboard.size() == mapLeaderboard.size())
            return arrayLeaderboard.isEmpty();
        else
            throw new GameSettingsException("Something went wrong while adding or removing the players");
    }

    /**
     * Retrieves the list of players in the showable leaderboard.
     *
     */
    public ArrayList<Optional<Player>> getShowableLeaderBoard() {
        return showableLeaderBoard;
    }

    /**
     * Calculates the module operation of a number with the number of cells.
     *
     * @param num the input number to perform the modulo operation on
     * @return the result of num modulo numberOfCells, always non-negative
     */
    public int moduleNumberOfCells(int num) {
        int numParz = num % numberOfCells;
        if (numParz < 0)
            return numParz + numberOfCells;
        else
            return numParz;
    }

    /**
     * Checks if the leaderboard array is sorted in descending order based on player values.
     * @return true if the array is sorted in descending order, false otherwise.
     */
    public boolean isSorted() {
        for (int i = 0; i < arrayLeaderboard.size() - 1; i++) {
            if (arrayLeaderboard.get(i).getValue() < arrayLeaderboard.get(i + 1).getValue())
                return false;
        }
        return true;
    }

    /**
     * Checks if the specified player is present in the leaderboard.
     *
     * @param player the player to check for presence in the leaderboard
     * @return true if the player is present, false otherwise
     */
    public boolean contains(Player player) {
        return mapLeaderboard.containsKey(player);
    }


    /**
     * Removes disconnected players who have reconnected from the disconnectedPlayers map.
     */
    public void refreshConnections() {
        disconnectedPlayers.entrySet().removeIf(entry -> entry.getValue().isConnected());
    }

    /**
     * Reconnects a previously disconnected player by removing their ID from the disconnected players list.
     *
     * @param player the player to reconnect
     */
    public void connectPlayer(Player player) {
        disconnectedPlayers.remove(player.getIdPlayer());
    }

    /**
     * Disconnects the specified player and updates the leaderboard state.
     * @param player the player to be disconnected
     * @throws NotEnoughPlayerException if disconnecting the player leaves only one player in the leaderboard
     */
    public void disconnectPlayer(Player player) {
        disconnectedPlayers.put(player.getIdPlayer(), player);
    }

    /**
     * Checks if the specified player is disconnected.
     *
     * @param player the player to check for disconnection status
     * @return true if the player is disconnected, false otherwise
     */
    public boolean isDisconnected(Player player) {
        return disconnectedPlayers.containsKey(player.getIdPlayer());
    }

    /**
     * Retrieves the first connected player in the leaderboard.
     * @return the first connected Player object.
     */
    public Player getFirstPlayerConnected() {
        return getPlayerConnectedStartingFromThisPosition(1);
    }

    /**
     * Retrieves a map of all disconnected players with their corresponding IDs.
     *
     * @return a map where the key is the player's unique ID and the value is the corresponding Player object.
     */
    public Map<Integer, Player> getDisconnectedPlayers() {
        return disconnectedPlayers;
    }

    /**
     * Computes the number of connected players in the leaderboard.
     * @return the count of connected players, calculated as the total size minus the size of disconnected players.
     */
    public int sizeOfConnected() {
        return (size() - disconnectedPlayers.size());
    }

    /**
     * Retrieves the number of disconnected players in the leaderboard.
     * @return the count of disconnected players
     */
    public int sizeOfDisconnected() {
        return disconnectedPlayers.size();
    }
}

