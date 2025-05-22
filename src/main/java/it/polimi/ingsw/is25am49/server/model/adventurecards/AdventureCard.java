package it.polimi.ingsw.is25am49.server.model.adventurecards;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class AdventureCard {

    protected String description;
    protected boolean isFinished;
    protected int level;
    protected int dicesResult;
    protected CardEffect currentEffect;
    protected AdventureStatesTypes currentState;
    protected GameBoard gameBoard;
    protected String  name;
    protected Random random;



    public AdventureCard(GameBoard gameBoard, String description, int level, String name) {
        this.gameBoard = gameBoard;
        this.description = description;
        this.isFinished = false;
        this.level = level;
        this.name = name;
        random = new Random(gameBoard.getSeedForGeneration());
    }

    public String getDescription() {
        return description;
    }

    public void checkRequirements(){}
    public abstract String toString();
    public void drawCard() {
        currentState = AdventureStatesTypes.DRAW;
        System.out.println("Hai pescato la carta: " + getClass().getSimpleName());
        gameBoard.setCurrentPlayer(gameBoard.getLeader());
    }

    public void landingOnStationOrShip(boolean isLanded) throws IllegalMethodOnThisStateException {
        if (currentState != AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public void landingOnPlanet(boolean isLanded, int playerId) throws IllegalMethodOnThisStateException{
        if(currentState != AdventureStatesTypes.DECISION_LAND_PLANET)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public CardEffect getCurrentEffect() {
        return currentEffect;
    }

    public AdventureStatesTypes getCurrentState() {
        return currentState;
    }

    public void rollDices() throws IllegalMethodOnThisStateException {
        if (currentState != AdventureStatesTypes.ROLL_DICES)
            throw new IllegalMethodOnThisStateException("Wrong state");
        int firstDice = random.nextInt(6) + 1;
        int secondDice = random.nextInt(6) + 1;
        dicesResult = firstDice + secondDice;
        System.out.println("The dices have been rolled. Result: " + dicesResult);
        currentEffect.setPosition(dicesResult);
    }

    public void applyEffect() {
        if (currentState != AdventureStatesTypes.EFFECT)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public int getLevel() {
        return level;
    }

    public void finishDecision(List<Pair<Point, Token>> coordinates) {
    }

    public void finishDecision(Set<Point> coordinates) {
    }

    public void acceptPrizes(boolean wantsRewards){
    }

    public List<Player> getDefeatedPlayers() {
        return null;
    }

    public boolean getIsFinished() {
        return isFinished;
    }
    public void setFinished(boolean finished) {
        isFinished = finished;
    }


    protected void update() {
        currentState = AdventureStatesTypes.UPDATE;
        gameBoard.checkIfSomeoneHasZeroAstronauts();
        gameBoard.finishUpdateLeaderboard();
        if(!gameBoard.getStopFlightPlayers().isEmpty()){
            for(Player player : gameBoard.getStopFlightPlayers()){
                try{
                    gameBoard.playerNoMoreInFlight(player);
                }catch (GameSettingsException e){
                    System.out.println("Player " + player.getIdPlayer() + " has already quit the game");
                }
            }
        }
        isFinished= true;
    }

    public String getName() {
        return name;
    }



    /**
     If description is longer than the width, it will be split into multiple lines
     @param sb the StringBuilder to append the text to
     @param text the text to append
     @param width the width of the box
     */
    public void appendTextWithWrapping(StringBuilder sb, String text, int width) {
        if (text.length() <= width - 1) {
            // Se il testo è più corto della larghezza, si usa la soluzione semplice
            sb.append(text).append(" ".repeat(Math.max(0, width - text.length() - 1))).append("|\n");
            return;
        }

        // Estraggo il prefisso (es. "| DESCRIPTION: ")
        String prefix = text.substring(0, text.indexOf(':') + 2);
        String content = text.substring(text.indexOf(':') + 2);

        // Prima riga con il testo iniziale
        sb.append(prefix).append(content.substring(0, width - prefix.length() - 1)).append("|\n");

        // Resto del contenuto su più righe
        String remainingContent = content.substring(width - prefix.length() - 1);
        String continuationPrefix = "|   ";

        while (remainingContent.length() > 0) {
            int charsToTake = Math.min(remainingContent.length(), width - continuationPrefix.length() - 1);
            sb.append(continuationPrefix)
                    .append(remainingContent.substring(0, charsToTake))
                    .append(" ".repeat(Math.max(0, width - continuationPrefix.length() - charsToTake - 1)))
                    .append("|\n");
            remainingContent = remainingContent.substring(charsToTake);
        }
    }

}
