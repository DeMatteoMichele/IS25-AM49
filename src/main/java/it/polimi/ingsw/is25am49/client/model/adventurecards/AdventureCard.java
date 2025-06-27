package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSizeToRemove;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalNumOfActivableException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalTokensException;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
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
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Activating components with batteries");
        for (int i = 0; i < coordinatesOfActivable.size(); i++) {
            shipBoard.turnOnComponent(coordinatesOfActivable.get(i), coordinatesOfBatteries.get(i).getKey());
            userInterface.updateEffectModifyEnergy(shipBoard.getPlayerID(), coordinatesOfBatteries.get(i).getKey(),coordinatesOfBatteries.get(i).getValue());
        }
    }

    /**
     * Validates the correctness of tokens and their associated positions based on the current adventure state and ship board configuration.
     *
     * @param state        The current state of the adventure, which determines the validation rules.
     * @param coordinates  A list of pairs, each containing a position (Point) and a token (Token), for verification.
     * @param shipBoard    The ship board containing tiles and components to validate tokens and positions against.
     * @throws IllegalTokensException If the tokens or positions do not adhere to the rules of the current state.
     * @throws IllegalSizeToRemove   If the number of selected coordinates exceeds or does not match the required amount to be removed in the current state.
     */
    protected void checkCorrectnessOfTokensAndPoints(AdventureStatesTypes state, List<Pair<Point, Token>> coordinates, ShipBoard shipBoard) {
        switch (state) {
            case DECISION_BATTERIES -> {
                for (Pair<Point, Token> pair : coordinates) {
                    Point position = pair.getKey();
                    Token token = pair.getValue();
                    if (shipBoard.getTile(position).getComponentType() != ComponentType.BATTERY || token != Token.BATTERY_CELLS) {
                        throw new IllegalTokensException("You can only use batteries with battery cells!");
                    }
                    if (countSamePair(pair, coordinates) > shipBoard.getTile(position).getComponent().getNumberOfaToken(token)) {
                        throw new IllegalTokensException("You selected an incorrect number of tokens, incompatible with your shipboard");
                    }
                }
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                if (coordinates.size() > currentEffect.getLostCrew()) {
                    throw new IllegalTokensException("You cannot choose a number of crew greater than " + currentEffect.getLostCrew() + "!");
                }
                int totalCrew = shipBoard.getTotalNumberOfaToken(Token.ASTRONAUT) + shipBoard.getTotalNumberOfaToken(Token.BROWN_ALIENS) + shipBoard.getTotalNumberOfaToken(Token.PURPLE_ALIENS);
                if (!((totalCrew < currentEffect.getLostCrew() && coordinates.size() == totalCrew) || (coordinates.size() == currentEffect.getLostCrew())))
                    throw new IllegalSizeToRemove("Please select the right amount of crew to remove: " + currentEffect.getLostCrew());
                for (Pair<Point, Token> pair : coordinates) {
                    Point position = pair.getKey();
                    Token token = pair.getValue();
                    if (shipBoard.getTile(position).getComponentType() != ComponentType.CABINE || (token != Token.ASTRONAUT && token != Token.BROWN_ALIENS && token != Token.PURPLE_ALIENS)) {
                        throw new IllegalTokensException("You can only use astronauts, aliens, and cabins!");
                    }
                    if (countSamePair(pair, coordinates) > shipBoard.getTile(position).getComponent().getNumberOfaToken(token)) {
                        throw new IllegalTokensException("You can remove at most " + shipBoard.getTile(position).getComponent().getNumberOfaToken(token) + " astronauts or aliens from this cabin!");
                    }
                }
            }
            case DECISION_GOODS -> {
                List<Integer> goods = currentEffect.getGoods();
//                int redGoods = goods.get(0);
//                int yellowGoods = goods.get(1);
//                int greenGoods = goods.get(2);
//                int blueGoods = goods.get(3);
//                int totalGoods = redGoods + yellowGoods + greenGoods + blueGoods;
                for (Pair<Point, Token> pair : coordinates) {
                    Point position = pair.getKey();
                    Token token = pair.getValue();
                    if (!(shipBoard.getTile(position).getComponentType() == ComponentType.SPECIAL_STORAGE && (token == Token.RED_GOODS || token == Token.BLUE_GOODS || token == Token.GREEN_GOODS || token == Token.YELLOW_GOODS)) && !(shipBoard.getTile(position).getComponentType() == ComponentType.STORAGE && (token == Token.BLUE_GOODS || token == Token.GREEN_GOODS || token == Token.YELLOW_GOODS))) {
                        throw new IllegalTokensException("You chose a wrong type of storage !");
                    }
//                    if (token == Token.RED_GOODS) redGoods--;
//                    else if (token == Token.YELLOW_GOODS) yellowGoods--;
//                    else if (token == Token.GREEN_GOODS) greenGoods--;
//                    else if (token == Token.BLUE_GOODS) blueGoods--;
//                }
//                if (redGoods != 0 || yellowGoods != 0 || greenGoods != 0 || blueGoods != 0) {
//                    throw new IllegalTokensException("You have selected a wrong type or number of goods!");
                }
            }
        }
    }

    /**
     * Counts the number of occurrences of a specific pair within a list of pairs.
     *
     * @param targetPair the pair to be compared against elements in the list
     * @param coordinates the list of pairs to search through
     * @return the number of times the targetPair appears in the list
     */
    private int countSamePair(Pair<Point, Token> targetPair, List<Pair<Point, Token>> coordinates) {
        int count = 0;
        for (Pair<Point, Token> pair : coordinates) {
            if (pair.equals(targetPair)) {
                count++;
            }
        }
        return count;
    }


    /**
     * Validates the correctness of selected activable components based on the current state
     * and prevents invalid actions by throwing appropriate exceptions.
     *
     * @param state the current state of the game, dictating which components can be activated
     * @param coordinates the set of positions on the shipboard representing components to be activated
     * @param shipBoard the ship board instance containing the layout and activable components
     * @throws IllegalTokensException if the number of components selected exceeds the activable limit
     *                                or if the selected components do not match the expected type for the state
     */
    protected void checkCorrectnessOfActivable(AdventureStatesTypes state, Set<Point> coordinates, ShipBoard shipBoard) {
        switch (state) {
            case DECISION_CANNON -> {
                if (coordinates.size() > shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON).size()) {
                    throw new IllegalTokensException("You must select a number of cannons less than or equal to the available double cannons!");
                }
                for (Point position : coordinates) {
                    if (shipBoard.getTile(position).getComponentType() != ComponentType.DOUBLE_CANNON) {
                        throw new IllegalTokensException("You can only use double cannons!");
                    }
                }
            }
            case DECISION_MOTOR -> {
                if (coordinates.size() > shipBoard.getActivatableComponents(ComponentType.DOUBLE_ENGINE).size()) {
                    throw new IllegalTokensException("You must select a number of engines less than or equal to the available double engines!");
                }
                for (Point position : coordinates) {
                    if (shipBoard.getTile(position).getComponentType() != ComponentType.DOUBLE_ENGINE) {
                        throw new IllegalTokensException("You can only use double engines!");
                    }
                }
            }
            case DECISION_SHIELDS -> {
                if (coordinates.size() > shipBoard.getActivatableComponents(ComponentType.SHIELD).size()) {
                    throw new IllegalTokensException("You must select a number of shields less than or equal to the available shields!");
                }
                for (Point position : coordinates) {
                    if (shipBoard.getTile(position).getComponentType() != ComponentType.SHIELD) {
                        throw new IllegalTokensException("You can only use shields!");
                    }
                }
            }
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

    public void checkFinishDecisionActivatingStates(int playerId, Set<Point> coordinates) {
        if (currentState == AdventureStatesTypes.DECISION_CANNON || currentState == AdventureStatesTypes.DECISION_MOTOR || currentState == AdventureStatesTypes.DECISION_SHIELDS) {
            checkCorrectnessOfActivable(currentState, coordinates, gameBoard.getCurrentPlayer().getShipBoard());

        } else {
            throw new IllegalMethodOnThisStateException("Wrong state");
        }
    }

    public void checkFinishDecisionTokenStates(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        if (currentState == AdventureStatesTypes.DECISION_BATTERIES || currentState == AdventureStatesTypes.DECISION_GOODS  || currentState == AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS) {
            checkCorrectnessOfTokensAndPoints(currentState, listOfTokens, gameBoard.getCurrentPlayer().getShipBoard());
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
