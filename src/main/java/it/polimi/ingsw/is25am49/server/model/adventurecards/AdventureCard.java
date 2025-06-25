package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSizeToRemove;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalNumOfActivableException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalTokensException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * The AdventureCard class represents an abstract base class for specialized adventure cards
 * in the game. It provides a framework for the card's description, level, name, and state.
 * Various methods are defined to handle specific actions and behaviors related to the card's
 * effects, states, and interactions with the game board and players.
 */
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


    /**
     * Constructs an AdventureCard object with the specified parameters.
     *
     * @param gameBoard    the game board associated with the adventure card
     * @param description  the text description of the adventure card
     * @param level        the difficulty level of the adventure card
     * @param name         the name of the adventure card
     */
    public AdventureCard(GameBoard gameBoard, String description, int level, String name) {
        this.gameBoard = gameBoard;
        this.description = description;
        this.isFinished = false;
        this.level = level;
        this.name = name;
        random = new Random(gameBoard.getSeedForGeneration());
    }


    /**
     * Returns the description of the AdventureCard.
     *
     * @return the description of the card as a String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Verifies if the current adventure card meets specific preconditions for its use or progression.
     * Checks the state, effects, or other requirements necessary for the card's operation.
     */
    public void checkRequirements() {
    }

    /**
     * Returns a string representation of the AdventureCard object, typically including
     * details such as its name, description, level, current state, or other relevant properties.
     *
     * @return a string describing the current state and attributes of the AdventureCard object
     */
    public abstract String toString();

    /**
     * Changes the current state of the adventure to the "DRAW" state, simulating the action of drawing a card.
     * Updates the current player to the first connected player on the game board and prints a message indicating
     * the name of the card that has been drawn.
     */
    public void drawCard() {
        currentState = AdventureStatesTypes.DRAW;
        System.out.println("Hai pescato la carta: " + getClass().getSimpleName());
        gameBoard.setCurrentPlayer(gameBoard.getFirstPlayerConnected());
    }

    /**
     * Handles the landing process either on a station or a ship, depending on the current state.
     * Throws an exception if the method is invoked in an incorrect state.
     *
     * @param isLanded a boolean value indicating if the landing action is completed (true if landed, otherwise false)
     * @throws IllegalMethodOnThisStateException if the current state is not DECISION_LAND_STATION_OR_SHIP
     */
    public void landingOnStationOrShip(boolean isLanded) throws IllegalMethodOnThisStateException {
        if (currentState != AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    /**
     * Handles the action of landing on a planet. This method ensures that the current state
     * of the game allows for landing on a planet and throws an exception if the operation
     * is not valid in the current state.
     *
     * @param isLanded Indicates whether the landing action is successful or not.
     * @param planetId The unique identifier of the planet being landed on.
     * @throws IllegalMethodOnThisStateException If the method is invoked in an invalid state.
     */
    public void landingOnPlanet(boolean isLanded, int planetId) throws IllegalMethodOnThisStateException {
        if (currentState != AdventureStatesTypes.DECISION_LAND_PLANET)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    /**
     * Retrieves the current effect associated with this adventure card.
     *
     * @return the current CardEffect associated with the adventure card
     */
    public CardEffect getCurrentEffect() {
        return currentEffect;
    }

    /**
     * Retrieves the current state of the adventure card.
     *
     * @return the current state of the adventure card as an {@code AdventureStatesTypes} enum value.
     */
    public AdventureStatesTypes getCurrentState() {
        return currentState;
    }

    /**
     * Rolls two six-sided dices, calculates their sum, and applies the result to the current effect.
     * Throws:
     * - IllegalMethodOnThisStateException if the method is invoked in a state other than ROLL_DICES.
     */
    public void rollDices() throws IllegalMethodOnThisStateException {
        if (currentState != AdventureStatesTypes.ROLL_DICES)
            throw new IllegalMethodOnThisStateException("Wrong state");
        int firstDice = random.nextInt(6) + 1;
        int secondDice = random.nextInt(6) + 1;
        dicesResult = firstDice + secondDice;
        System.out.println("The dices have been rolled. Result: " + dicesResult);
        currentEffect.setPosition(dicesResult);
    }

    /**
     * Applies the effect associated with the current AdventureCard.
     *
     * This method checks the current state of the AdventureCard to ensure it is in
     * the EFFECT state. If the current state is not EFFECT, an IllegalMethodOnThisStateException
     * is thrown to indicate that the method is being called in an invalid state.
     *
     * @throws IllegalMethodOnThisStateException if the current state is not EFFECT
     */
    public void applyEffect() {
        if (currentState != AdventureStatesTypes.EFFECT)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    /**
     * Retrieves the level of the AdventureCard.
     *
     * @return the current level of the AdventureCard.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Completes the decision-making process by validating and applying the provided
     * coordinates and their associated tokens.
     *
     * @param coordinates a list of pairs where each pair consists of a coordinate
     *                     represented by a Point and its associated Token.
     */
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
    }

    /**
     * Finalizes the decision-making process based on the provided set of coordinates.
     *
     * @param coordinates a set of coordinates representing the points involved in the decision process
     */
    public void finishDecision(Set<Point> coordinates) {
    }

    /**
     * Processes the acceptance of prizes for the player based on their decision to receive rewards.
     *
     * @param wantsRewards indicates if the player desires to accept the prizes or rewards.
     *                     If true, rewards will be granted; otherwise, they will be declined.
     */
    public void acceptPrizes(boolean wantsRewards) {
    }

    /**
     * Retrieves a list of players who have been defeated in the adventure.
     *
     * @return a list of defeated players, or an empty list if no players have been defeated.
     */
    public List<Player> getDefeatedPlayers() {
        return null;
    }

    /**
     * Checks if the adventure card has been marked as finished.
     *
     * @return {@code true} if the adventure card is finished, {@code false} otherwise.
     */
    public boolean getIsFinished() {
        return isFinished;
    }

    /**
     * Sets the completion status of the adventure card.
     * This method updates the `isFinished` field to indicate whether the card's associated actions or events
     * have been finalized.
     *
     * @param finished a boolean value indicating whether the adventure card is finished (true) or not (false)
     */
    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    /**
     * Updates the state of the adventure card to reflect the completion of an update phase.
     *
     * This method sets the current state of the card to {@code AdventureStatesTypes.UPDATE},
     * performs necessary leaderboard updates via the associated {@code GameBoard} object,
     * and marks the card as finished by setting the {@code isFinished} field to {@code true}.
     */
    protected void update() {
        currentState = AdventureStatesTypes.UPDATE;
        gameBoard.finishUpdateLeaderboard();
        isFinished = true;
    }

    /**
     * Deactivates the specified components on the provided ship board and clears the list of activable coordinates.
     *
     * @param shipBoard the ship board on which components will be deactivated
     * @param coordinatesOfActivable the list of coordinates representing activable components to be deactivated
     */
    protected void deactivateComponents(ShipBoard shipBoard,List<Point> coordinatesOfActivable) {
        coordinatesOfActivable.forEach(shipBoard::turnOff);
        coordinatesOfActivable.clear();
    }

    /**
     * Activates specified components on the ship board using the provided batteries.
     * Ensures the number of components to be activated does not exceed the number of available batteries.
     *
     * @param shipBoard the ship's main board where components are located
     * @param coordinatesOfActivable a list of coordinates representing the components to be activated
     * @param coordinatesOfBatteries a list of pairs containing battery coordinates and their corresponding tokens
     * @throws IllegalNumOfActivableException if the number of components exceeds the number of batteries
     */
    protected void activateComponentsWithBatteries(ShipBoard shipBoard, List<Point> coordinatesOfActivable, List<Pair<Point, Token>> coordinatesOfBatteries) {
        if (coordinatesOfActivable.size() > coordinatesOfBatteries.size()) {
            throw new IllegalNumOfActivableException("You have to choose at max the same number of components as batteries");
        }
        System.out.println("You have decided the coord for the components");
        for (int i = 0; i < coordinatesOfActivable.size(); i++) {
            shipBoard.turnOnComponent(coordinatesOfActivable.get(i), coordinatesOfBatteries.get(i).getKey());
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
                    if (shipBoard.getTile(position).getComponentType() != ComponentType.BATTERY && token != Token.BATTERY_CELLS) {
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

    /**
     * Verifies if the number of batteries provided does not exceed the total number
     * of available battery cells in the shipboard. Throws an exception if the count is
     * exceeded.
     *
     * @param batteries the list of pairs containing points and tokens representing batteries
     * @param shipBoard the shipboard object used to retrieve the total number of available battery cells
     * @throws IllegalArgumentException if the number of batteries provided exceeds the available battery cells
     */
    protected void checkNumberOfBatteries(List<Pair<Point, Token>> batteries, ShipBoard shipBoard) {
        if (batteries.size() > shipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS)) {
            throw new IllegalArgumentException("you have chosen more batteries than you have");
        }
    }

    /**
     * Checks whether the current player is connected. If not, applies a default effect
     * based on the provided current state.
     *
     * @param currState the current state of the adventure, representing the context in which
     *                  the connection check is performed and determining the default effect
     *                  to be applied if the player is not connected
     */
    protected void checkConnectionOrDefaultEffect(AdventureStatesTypes currState) {
        if (!gameBoard.getCurrentPlayer().isConnected())
            applyDefaultEffect(currState);
    }

    /**
     * Applies the default effect based on the given adventure state. This method handles different game
     * states and applies predefined behaviors for each state, such as rolling dices, decision-making
     * involving crew, goods, batteries, cannons, shields, motors, and landing on stations or planets.
     *
     * @param currState The current state of the adventure. Determines the specific default effect
     *                  to apply. The state must correspond to a valid enum value from AdventureStatesTypes.
     */
    protected void applyDefaultEffect(AdventureStatesTypes currState) {
        switch (currState) {
            case ROLL_DICES -> {
                System.out.println("Default effect of rolling dices on " + gameBoard.getCurrentPlayer().getUsername());
                rollDices();
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                System.out.println("Default effect of losing crew on" + gameBoard.getCurrentPlayer().getUsername());

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
                finishDecision(coordinates);
            }
            case DECISION_BATTERIES -> {
                System.out.println("Default effect of losing batteries on" + gameBoard.getCurrentPlayer().getUsername());

                ArrayList<Pair<Point, Token>> batteries = new ArrayList<>();
                finishDecision(batteries);
            }
            case DECISION_CANNON -> {
                System.out.println("Default effect of losing cannons on" + gameBoard.getCurrentPlayer().getUsername());

                Set<Point> cannons = new HashSet<>();
                finishDecision(cannons);
            }
            case DECISION_MOTOR -> {
                System.out.println("Default effect of losing motors on" + gameBoard.getCurrentPlayer().getUsername());

                Set<Point> motors = new HashSet<>();
                finishDecision(motors);
            }
            case DECISION_SHIELDS -> {
                System.out.println("Default effect of losing shields on" + gameBoard.getCurrentPlayer().getUsername());

                Set<Point> shields = new HashSet<>();
                finishDecision(shields);
            }
            case DECISION_GOODS -> {
                System.out.println("Default effect of losing goods on" + gameBoard.getCurrentPlayer().getUsername());
                ArrayList<Pair<Point, Token>> goods = new ArrayList<>();
                finishDecision(goods);
            }
            case DECISION_LAND_PLANET -> {
                landingOnPlanet(false, -1);
            }
            case DECISION_LAND_STATION_OR_SHIP -> {
                landingOnStationOrShip(false);
            }
            case DECISION_REWARDS -> {
                acceptPrizes(false);
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
     * Retrieves the name associated with this object.
     *
     * @return the name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Appends the provided text to the given StringBuilder, formatting it with line wrapping to fit within a specified width.
     * If the text contains a prefix (e.g., before a colon), it preserves the prefix for the initial line
     * and wraps the remaining content to subsequent lines with a continuation prefix.
     *
     * @param sb the StringBuilder to append the formatted text to
     * @param text the text to be appended, formatted with wrapping
     * @param width the maximum line width, including any prefixes and margin
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

    /**
     * Handles the case where a player disconnects during specific game states. If the current game state matches
     * one where player input is required and the disconnected player is the current player, a default effect is applied.
     * Otherwise, no action is taken.
     *
     * @param player the player whose disconnection is being handled
     */
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
            System.out.println("Disconnection handling not needed in this state");
        }
    }


}
