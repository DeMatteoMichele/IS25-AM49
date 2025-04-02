package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class LeaderBoard {
    private final HashMap<Player, Integer> mapLeaderboard;
    private final ArrayList<Map.Entry<Player, Integer>> arrayLeaderboard;
    private final ArrayList<Optional<Player>> showableLeaderBoard;
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
        int i;
        showableLeaderBoard = new ArrayList<>(numberOfCells);
        for(i=0;i<numberOfCells;i++) {
            showableLeaderBoard.add(Optional.empty());
        }
    }
    //per implementarlo devo prima capire come verrano implementate le posizioni nel configurateflight
    public void add(Player player){
        switch(arrayLeaderboard.size()) {
            case 3 -> {
                mapLeaderboard.put(player, 0);
                arrayLeaderboard.add(Map.entry(player, 0));
                showableLeaderBoard.set(0, Optional.of(player));
                break;}
            case 2 -> {
                mapLeaderboard.put(player, 1);
                arrayLeaderboard.add(Map.entry(player, 1));
                showableLeaderBoard.set(1, Optional.of(player));
                break;
            }
            case 1 -> {
                if(level == 1){
                    mapLeaderboard.put(player, 2);
                    arrayLeaderboard.add(Map.entry(player, 2));
                    showableLeaderBoard.set(2, Optional.of(player));
                }
                else if(level == 2){
                    mapLeaderboard.put(player, 3);
                    arrayLeaderboard.add(Map.entry(player, 3));
                    showableLeaderBoard.set(3, Optional.of(player));
                }
                else
                    throw new GameSettingsException("Error, the Game mode  must be LEVEL_1 or LEVEL_2");
                break;
            }
            case 0 -> {
                if(level == 1){
                    mapLeaderboard.put(player, 4);
                    arrayLeaderboard.add(Map.entry(player, 4));
                    showableLeaderBoard.set(4, Optional.of(player));
                }
                else if(level == 2){
                    mapLeaderboard.put(player, 6);
                    arrayLeaderboard.add(Map.entry(player, 6));
                    showableLeaderBoard.set(6, Optional.of(player));
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
    private int findIndexOfPlayerInShowableLeaderBoard(Player player){
        int indexOfPlayer = showableLeaderBoard.indexOf(Optional.of(player));
        if(indexOfPlayer == -1)
            throw new IllegalSetOfNextPlayerException("This player is not in the leaderboard");
        else
            return indexOfPlayer;
    }
    private int decreaseDaysFromShowableLeaderBoard(Player player, int daysModified){
        int positionChanged = 0;
        int index = findIndexOfPlayerInShowableLeaderBoard(player);
        int sign = Integer.signum(daysModified);
        Optional<Player> temp = showableLeaderBoard.get(index);
        showableLeaderBoard.set(index, Optional.empty());
        while(daysModified != 0){
            if(showableLeaderBoard.get(moduleNumberOfCells(index+sign)).isEmpty()){
                daysModified = daysModified - sign;
                positionChanged = positionChanged + sign;
            }
            else if(showableLeaderBoard.get(moduleNumberOfCells(index+sign)).isPresent()){
               positionChanged = positionChanged + sign;
            }
            index = index + sign;
        }
        showableLeaderBoard.set(moduleNumberOfCells(index), temp);
        return positionChanged;
    }
    // days può essere maggiore o minore di zero
    public void modifyFlightDays(Player player, int daysModified){
        if(mapLeaderboard.containsKey(player)){
            int tmpDays = mapLeaderboard.get(player);
            int daysToChange = decreaseDaysFromShowableLeaderBoard(player, daysModified);
            Map.Entry<Player, Integer> entryBefore = arrayLeaderboard.set(getPos(player)-1, Map.entry(player, tmpDays + daysToChange));
            mapLeaderboard.put(player, tmpDays + daysToChange);
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
            showableLeaderBoard.set(findIndexOfPlayerInShowableLeaderBoard(player), Optional.empty());
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
    public Player getLeader() {
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
    public ArrayList<Optional<Player>> getShowableLeaderBoard(){return showableLeaderBoard;}
    public int moduleNumberOfCells(int num){
        if(num<0){
            num = num *(-1);
            if(num%numberOfCells == 0)
                return 0;
            else
                return numberOfCells - (num % numberOfCells);
        }
        else
            return num % numberOfCells;
    }
    public boolean isSorted(){
        for(int i = 0; i < arrayLeaderboard.size()-1; i++){
            if(arrayLeaderboard.get(i).getValue() < arrayLeaderboard.get(i+1).getValue())
                return false;
        }
        return true;
    }
}

