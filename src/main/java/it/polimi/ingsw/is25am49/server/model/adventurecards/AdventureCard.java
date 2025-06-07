package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalTokensException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
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
    protected String name;
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

    public void checkRequirements() {
    }

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

    public void landingOnPlanet(boolean isLanded, int playerId) throws IllegalMethodOnThisStateException {
        if (currentState != AdventureStatesTypes.DECISION_LAND_PLANET)
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

    public void acceptPrizes(boolean wantsRewards) {
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
        gameBoard.finishUpdateLeaderboard();
        isFinished = true;
    }

    /**
     * This method ensures that the user selected the correct points, the correct type of token and number of tokens
     * in accordance with the internal state of the adventurecard and the characteristics of the currenteffect.
     * Otherwise, an IllegalTokensException is thrown that must be catched in servercontroller.
     * @param state
     * @param coordinates
     * @param shipBoard
     */
    protected void checkCorrectnessOfTokensAndPoints(AdventureStatesTypes state, List<Pair<Point, Token>> coordinates, ShipBoard shipBoard) {
        switch (state) {
            case DECISION_BATTERIES ->{
                for (Pair<Point, Token> pair : coordinates) {
                    Point position = pair.getKey();
                    Token token = pair.getValue();
                    if (shipBoard.getTile(position).getComponentType() != ComponentType.BATTERY && token != Token.BATTERY_CELLS) {
                        throw new IllegalTokensException("Puoi utilizzare solo batterie con celle di batteria!");
                    }
                    if (countSamePair(pair, coordinates) > shipBoard.getTile(position).getComponent().getNumberOfaToken(token)) {
                        throw new IllegalTokensException("Hai selezionato un numero di token errato, incompatibile con la tua shipboard");
                    }
                }
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                if(currentEffect.getLostCrew() != coordinates.size()) {
                    throw new IllegalTokensException("Devi selezionare un numero di astronauti o alieni pari a " + currentEffect.getLostCrew() + "!");
                }
                for (Pair<Point, Token> pair : coordinates) {
                    Point position = pair.getKey();
                    Token token = pair.getValue();
                    if (shipBoard.getTile(position).getComponentType() != ComponentType.CABINE || (token != Token.ASTRONAUT) && token != Token.BROWN_ALIENS && token != Token.PURPLE_ALIENS) {
                        throw new IllegalTokensException("Puoi utilizzare solo astronauti in una cabina!");
                    }
                    if (countSamePair(pair, coordinates) > shipBoard.getTile(position).getComponent().getNumberOfaToken(token)) {
                        throw new IllegalTokensException("Puoi rimuovere al massimo " + shipBoard.getTile(position).getComponent().getLevel() + " astronauti o alieni da questa cabina!");
                    }
                }
            }
            case DECISION_GOODS -> {
                List<Integer> goods = currentEffect.getGoods();
                int redGoods = goods.get(0);
                int yellowGoods = goods.get(1);
                int greenGoods = goods.get(2);
                int blueGoods = goods.get(3);
                int totalGoods = redGoods + yellowGoods + greenGoods + blueGoods;
                if(totalGoods != coordinates.size()) {
                    throw new IllegalTokensException("You have to choose  " + totalGoods+ " goods!");
                }
                for (Pair<Point, Token> pair : coordinates) {
                    Point position = pair.getKey();
                    Token token = pair.getValue();
                    if (!(shipBoard.getTile(position).getComponentType() == ComponentType.SPECIAL_STORAGE && token == Token.RED_GOODS || token == Token.BLUE_GOODS || token == Token.GREEN_GOODS || token == Token.YELLOW_GOODS) || !(shipBoard.getTile(position).getComponentType() == ComponentType.STORAGE && (token == Token.BLUE_GOODS || token == Token.GREEN_GOODS || token == Token.YELLOW_GOODS))) {
                        throw new IllegalTokensException("You chose a wrong type of storage !");
                    }
                    if(token ==Token.RED_GOODS) redGoods--;
                    else if(token ==Token.YELLOW_GOODS) yellowGoods--;
                    else if(token ==Token.GREEN_GOODS) greenGoods--;
                    else if(token ==Token.BLUE_GOODS) blueGoods--;
                }
                if(redGoods != 0 || yellowGoods != 0 || greenGoods != 0 || blueGoods != 0) {
                    throw new IllegalTokensException("You have selected a wrong type or number of goods!");
                }
            }
        }
    }


    /**
     * This method ensures that the user selected the correct type and number of activable component in accordance with the internal state
     * of the card and with the structure of the shipboard.
     * Otherwise, an IllegalTokensException is thrown that must be catched in servercontroller.
     *
     * @param state
     * @param coordinates
     * @param shipBoard
     */
    public void checkCorrectnessOfActivable(AdventureStatesTypes state, Set<Point> coordinates, ShipBoard shipBoard) {
        switch (state) {
            case DECISION_CANNON -> {
                if (coordinates.size() > shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON).size()) {
                    throw new IllegalTokensException("Devi selezionare un numero di cannoni minore o uguale al numero di cannoni doppi attivabili!");
                }
                for (Point position : coordinates) {
                    if (shipBoard.getTile(position).getComponentType() != ComponentType.DOUBLE_CANNON) {
                        throw new IllegalTokensException("Puoi utilizzare solo cannoni doppi!");
                    }
                }
            }
            case DECISION_MOTOR -> {
                if (coordinates.size() > shipBoard.getActivatableComponents(ComponentType.DOUBLE_ENGINE).size()) {
                    throw new IllegalTokensException("Devi selezionare un numero di cannoni minore o uguale al numero di motori doppi attivabili!");
                }
                for (Point position : coordinates) {
                    if (shipBoard.getTile(position).getComponentType() != ComponentType.DOUBLE_ENGINE) {
                        throw new IllegalTokensException("Puoi utilizzare solo motori doppi!");
                    }
                }
            }
            case DECISION_SHIELDS -> {
                if (coordinates.size() > shipBoard.getActivatableComponents(ComponentType.SHIELD).size()) {
                    throw new IllegalTokensException("Devi selezionare un numero di scudi minore o uguale al numero di scudi attivabili!");
                }
                for (Point position : coordinates) {
                    if (shipBoard.getTile(position).getComponentType() != ComponentType.SHIELD) {
                        throw new IllegalTokensException("Puoi utilizzare solo scudi!");
                    }
                }
            }
        }
    }

    /**
     * This method counts how many times a chosen targetPair appears in List<Pair<Point, Token>>
     * @param targetPair
     * @param coordinates
     * @return
     */
    private int countSamePair(Pair<Point,Token> targetPair, List<Pair<Point, Token>> coordinates) {
        int count = 0;
        for (Pair<Point, Token> pair : coordinates) {
            if (pair.equals(targetPair)) {
                count++;
            }
        }
        return count;
    }

    public String getName() {
        return name;
    }


    /**
     * If description is longer than the width, it will be split into multiple lines
     *
     * @param sb    the StringBuilder to append the text to
     * @param text  the text to append
     * @param width the width of the box
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
