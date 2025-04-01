package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class LeaderBoard {
    private final HashMap<Player, Integer> mapLeaderboard;
    private final ArrayList<Map.Entry<Player, Integer>> arrayLeaderboard;
    private final int level;
    private final int numberOfCells;

    public LeaderBoard(GameMode mode) {
        mapLeaderboard = new HashMap<>();
        arrayLeaderboard = new ArrayList<>();
        switch (mode) {
            case LEVEL_1 -> {
                level = 1;
                numberOfCells = 18;
                break;
            }
            case LEVEL_2 -> {
                level = 2;
                numberOfCells = 24;
                break;
            }
            default -> {
                throw new GameSettingsException("Error, the Game mode  must be LEVEL_1 or LEVEL_2");
            }
        }
    }
    //per implementarlo devo prima capire come verrano implementate le posizioni nel configurateflight
    public void add(Player player){
        switch(arrayLeaderboard.size()) {
            case 3 -> {
                mapLeaderboard.put(player, 0);
                arrayLeaderboard.add(Map.entry(player, 0));
                break;}
            case 2 -> {
                mapLeaderboard.put(player, 1);
                arrayLeaderboard.add(Map.entry(player, 1));
                break;
            }
            case 1 -> {
                if(level == 1){
                    mapLeaderboard.put(player, 2);
                    arrayLeaderboard.add(Map.entry(player, 2));
                }
                else if(level == 2){
                    mapLeaderboard.put(player, 3);
                    arrayLeaderboard.add(Map.entry(player, 3));
                }
                else
                    throw new GameSettingsException("Error, the Game mode  must be LEVEL_1 or LEVEL_2");
                break;
            }
            case 0 -> {
                if(level == 1){
                    mapLeaderboard.put(player, 4);
                    arrayLeaderboard.add(Map.entry(player, 4));
                }
                else if(level == 2){
                    mapLeaderboard.put(player, 6);
                    arrayLeaderboard.add(Map.entry(player, 6));
                }
                else
                    throw new GameSettingsException("Error, the Game mode  must be LEVEL_1 or LEVEL_2");
                break;
            }
            default -> {
                throw new GameSettingsException("You cannot insert more than 4 players");
            }
        }
    }
    public int getDays(Player player){
        if(mapLeaderboard.containsKey(player))
            return mapLeaderboard.get(player);
        else
            throw new IllegalSetOfNextPlayerException("This player is not in the leaderboard");
    }
    // days può essere maggiore o minore di zero
    public void modifyFlightDays(Player player, int daysModified){
        if(mapLeaderboard.containsKey(player)){
            int tmpDays = mapLeaderboard.get(player);
            if(daysModified >= 0) {
                int numPlayersToSkip = (int) arrayLeaderboard.stream().filter(entry -> entry.getValue() <= tmpDays + daysModified && entry.getValue() > tmpDays).count();
                mapLeaderboard.put(player, tmpDays + daysModified + numPlayersToSkip);
            }
            else{
                int numPlayersToSkip = (int) arrayLeaderboard.stream().filter(entry -> entry.getValue() >= tmpDays + daysModified && entry.getValue() < tmpDays).count();
                mapLeaderboard.put(player, tmpDays + daysModified - numPlayersToSkip);
            }
            sort();
        }
        else
            throw new IllegalSetOfNextPlayerException("This player is not in the leaderboard");
    }
    public void remove(Player player){
        if(mapLeaderboard.containsKey(player)){
            int tmpDays = mapLeaderboard.get(player);
            mapLeaderboard.remove(player);
            arrayLeaderboard.remove(Map.entry(player, tmpDays));
            sort();
        }
        else
            throw new GameSettingsException("This player is not in the leaderboard");
    }
    private void sort(){
        arrayLeaderboard.sort((p1,p2) -> Integer.compare(p2.getValue(), p1.getValue()));
    }
    public int getNumberOfCells() {
        return numberOfCells;
    }
    public Player getFirst() {
        if(mapLeaderboard.isEmpty())
            throw new IllegalSetOfNextPlayerException("The leader board is empty");
        else
            return arrayLeaderboard.getFirst().getKey();
    }
    public int getPos(Player player) {
        if (mapLeaderboard.containsKey(player)) {
            int tmpDays = mapLeaderboard.get(player);
            return arrayLeaderboard.indexOf(Map.entry(player, tmpDays)) + 1;
        } else
            throw new IllegalSetOfNextPlayerException("This player is not in the leaderboard");
    }
    public Stream<Map.Entry<Player, Integer>> stream(){
        return arrayLeaderboard.stream();
    }
    public int size(){
        if(arrayLeaderboard.size() == mapLeaderboard.size())
            return arrayLeaderboard.size();
        else
            throw new GameSettingsException("Something went wrong while adding or removing the players");
    }
    // 1 <= pos <= 4
    public Player getPlayerInPosition(int pos){
        if(pos <= arrayLeaderboard.size() && pos > 0)
            return arrayLeaderboard.get(pos-1).getKey();
        else
            throw new IndexOutOfBoundsException("Error, the given position does not exist");
    }
    public boolean isEmpty(){
        if(arrayLeaderboard.size() == mapLeaderboard.size())
            return arrayLeaderboard.isEmpty();
        else
            throw new GameSettingsException("Something went wrong while adding or removing the players");
    }
}

