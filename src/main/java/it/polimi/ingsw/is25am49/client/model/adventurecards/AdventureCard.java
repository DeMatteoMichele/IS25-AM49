package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalNumOfActivableException;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

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
    protected UserInterface userInterface;
    protected String cardId;
    protected int genericIdForUpdates = -99;

    public AdventureCard(String cardId, GameBoard gameBoard, String description, int level, String name) {
        this.cardId = cardId;
        this.gameBoard = gameBoard;
        this.description = description;
        this.isFinished = false;
        this.level = level;
        this.name = name;
        random = new Random(gameBoard.getSeedForGeneration());
        this.userInterface = gameBoard.getUserInterface();
    }
    public String getCardId() {
        return cardId;
    }
    public String getDescription() {
        return description;
    }

    public void checkRequirements() {
    }

    public abstract String toString();

    public void drawCard() {
        currentState = AdventureStatesTypes.DRAW;
        userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
        userInterface.updateDrawAdventureCard(-99, this);
        gameBoard.setCurrentPlayer(gameBoard.getFirstPlayerConnected());
        userInterface.updateChangeTurn(gameBoard.getFirstPlayerConnected().getIdPlayer());
    }

    public void landingOnStationOrShip(boolean isLanded) throws IllegalMethodOnThisStateException {
        if (currentState != AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public void landingOnPlanet(boolean isLanded, int planetId) throws IllegalMethodOnThisStateException {
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
        userInterface.updateThrowDices(gameBoard.getCurrentPlayer().getIdPlayer(), firstDice, secondDice);
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
        userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
        gameBoard.finishUpdateLeaderboard();
        isFinished = true;
    }

    protected void deactivateComponents(ShipBoard shipBoard,List<Point> coordinatesOfActivable) {
        if(coordinatesOfActivable.isEmpty()) {
            return;
        }
        coordinatesOfActivable.forEach(shipBoard::turnOff);
        userInterface.updateDeactivating(shipBoard.getPlayerID(), coordinatesOfActivable);
        coordinatesOfActivable.clear();
    }

    protected void activateComponentsWithBatteries(ShipBoard shipBoard, List<Point> coordinatesOfActivable, List<Pair<Point, Token>> coordinatesOfBatteries) {
        if (coordinatesOfActivable.size() > coordinatesOfBatteries.size()) {
            throw new IllegalNumOfActivableException("You have to choose at max the same number of components as batteries");
        }
        for (int i = 0; i < coordinatesOfActivable.size(); i++) {
            shipBoard.turnOnComponent(coordinatesOfActivable.get(i), coordinatesOfBatteries.get(i).getKey());
            userInterface.updateEffectModifyEnergy(shipBoard.getPlayerID(), coordinatesOfBatteries.get(i).getKey(),coordinatesOfBatteries.get(i).getValue());
        }
    }
    protected void checkNumberOfBatteries(List<Pair<Point, Token>> batteries, ShipBoard shipBoard) {
        if (batteries.size() > shipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS)) {
            throw new IllegalArgumentException("you have chosen more batteries than you have");
        }
    }

    protected void checkConnectionOrDefaultEffect(AdventureStatesTypes currState) {
        if (!gameBoard.getCurrentPlayer().isConnected())
            applyDefaultEffect(currState);
    }

    protected void applyDefaultEffect(AdventureStatesTypes currState) {
        switch (currState) {
            case ROLL_DICES -> {
                userInterface.updateGenericCardMessage(genericIdForUpdates,"Default effect of rolling dices on " + gameBoard.getCurrentPlayer().getUsername());
                rollDices();
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                ArrayList<Point> astronauts = gameBoard.getCurrentPlayer().getShipBoard().getPositionsOfaToken(Token.ASTRONAUT);
                ArrayList<Point> brownAliens = gameBoard.getCurrentPlayer().getShipBoard().getPositionsOfaToken(Token.BROWN_ALIENS);
                ArrayList<Point> purpleAliens = gameBoard.getCurrentPlayer().getShipBoard().getPositionsOfaToken(Token.PURPLE_ALIENS);
                ArrayList<Pair<Point, Token>> coordinates = new ArrayList<>();
                for (Point point : astronauts) {
                    coordinates.add(new Pair<>(point, Token.ASTRONAUT));
                }
                for (Point point : brownAliens) {
                    coordinates.add(new Pair<>(point, Token.BROWN_ALIENS));
                }
                for (Point point : purpleAliens) {
                    coordinates.add(new Pair<>(point, Token.PURPLE_ALIENS));
                }
                if (coordinates.size() > currentEffect.getLostCrew()) {
                    coordinates = new ArrayList<>(coordinates.subList(0, currentEffect.getLostCrew()));
                }
                userInterface.updateGenericCardMessage(genericIdForUpdates,"Default effect of losing crew on" + gameBoard.getCurrentPlayer().getUsername());
                finishDecision(coordinates);
            }
            case DECISION_BATTERIES -> {
                userInterface.updateGenericCardMessage(genericIdForUpdates,"Default effect: no batteries chosen on  " + gameBoard.getCurrentPlayer().getUsername());
                ArrayList<Pair<Point, Token>> batteries = new ArrayList<>();
                finishDecision(batteries);
            }
            case DECISION_CANNON -> {
                userInterface.updateGenericCardMessage(genericIdForUpdates,"Default effect: no cannons activated on " + gameBoard.getCurrentPlayer().getUsername());
                Set<Point> cannons = new HashSet<>();
                finishDecision(cannons);
            }
            case DECISION_MOTOR -> {
                userInterface.updateGenericCardMessage(genericIdForUpdates,"Default effect: no engines activated on" + gameBoard.getCurrentPlayer().getUsername());
                Set<Point> motors = new HashSet<>();
                finishDecision(motors);
            }
            case DECISION_SHIELDS -> {
                userInterface.updateGenericCardMessage(genericIdForUpdates,"Default effect: no shield activated on " + gameBoard.getCurrentPlayer().getUsername());
                Set<Point> shields = new HashSet<>();
                finishDecision(shields);
            }
            case DECISION_GOODS -> {
                userInterface.updateGenericCardMessage(genericIdForUpdates,"Default effect of decision goods on" + gameBoard.getCurrentPlayer().getUsername());
                ArrayList<Pair<Point, Token>> goods = new ArrayList<>();
                finishDecision(goods);
            }
            case DECISION_LAND_PLANET -> {
                userInterface.updateGenericCardMessage(genericIdForUpdates,"Default effect of not landing on planet on " + gameBoard.getCurrentPlayer().getUsername());
                landingOnPlanet(false, -1);
            }
            case DECISION_LAND_STATION_OR_SHIP -> {
                userInterface.updateGenericCardMessage(genericIdForUpdates,"Default effect of not landing on station/ship on " + gameBoard.getCurrentPlayer().getUsername());
                landingOnStationOrShip(false);
            }
            case DECISION_REWARDS -> {
                userInterface.updateGenericCardMessage(genericIdForUpdates,"Default effect of not accepting the prizes on" + gameBoard.getCurrentPlayer().getUsername());
                acceptPrizes(false);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void checkThrowDicesState() {
        if (currentState != AdventureStatesTypes.ROLL_DICES) {
            throw new IllegalMethodOnThisStateException("Wrong state");
        }
    }

    public void checkFinishDecisionActivatingStates() {
        if (currentState == AdventureStatesTypes.DECISION_CANNON || currentState == AdventureStatesTypes.DECISION_MOTOR || currentState == AdventureStatesTypes.DECISION_SHIELDS) {
            //OK
        } else {
            throw new IllegalMethodOnThisStateException("Wrong state");
        }
    }

    public void checkFinishDecisionTokenStates() {
        if (currentState == AdventureStatesTypes.DECISION_BATTERIES || currentState == AdventureStatesTypes.DECISION_GOODS  || currentState == AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS) {
            //OK
        } else {
            throw new IllegalMethodOnThisStateException("Wrong state");
        }

    }

    public void checkFinishDecisionTokenCoordinates(ArrayList<Pair<Point, Token>> coordinates) {
        //IMPLEMENTATO DA MAICALS NEL SERVER


    }

    public void checkLandOnStationOrShipState() {
        if (currentState != AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP) {
            throw new IllegalMethodOnThisStateException("Wrong state");
        }
    }

    public void checkLandOnPlanetState() {
        if (currentState != AdventureStatesTypes.DECISION_LAND_PLANET) {
            throw new IllegalMethodOnThisStateException("Wrong state");
        }
    }

    public void checkAcceptPrizesState() {
        if (currentState != AdventureStatesTypes.DECISION_REWARDS) {
            throw new IllegalMethodOnThisStateException("Wrong state");
        }
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

    public void disconnectionHandling(Player player) {
        if (gameBoard.getCurrentPlayer().equals(player) &&
                (currentState == AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS ||
                currentState == AdventureStatesTypes.DECISION_BATTERIES ||
                currentState == AdventureStatesTypes.DECISION_GOODS ||
                currentState == AdventureStatesTypes.DECISION_LAND_PLANET ||
                currentState == AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP ||
                currentState == AdventureStatesTypes.DECISION_REWARDS ||
                currentState == AdventureStatesTypes.DECISION_CANNON ||
                currentState == AdventureStatesTypes.DECISION_MOTOR ||
                currentState == AdventureStatesTypes.DECISION_SHIELDS ||
                currentState == AdventureStatesTypes.ROLL_DICES)) {
            applyDefaultEffect(currentState);
        } else {
            userInterface.updateGenericCardMessage(genericIdForUpdates,player.getUsername() + " has disconnected, you can continue the game without him!");
        }
    }
}
