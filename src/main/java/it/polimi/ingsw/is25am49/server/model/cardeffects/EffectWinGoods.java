package it.polimi.ingsw.is25am49.server.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;

import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisCardException;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

/**
 * Represents an effect that provides goods of different types to a player
 * during gameplay and allows manipulation of these goods on a game board.
 */
public class EffectWinGoods extends CardEffect {
    /**
     * Represents the number of red goods associated with the effect.
     * Used to define specific behavior or rewards related to red goods.
     */
    private int redGoods;
    /**
     * Represents the quantity of yellow goods available in the effect.
     * Used to track and manage yellow goods in the game context.
     */
    private int yellowGoods;
    /**
     * Represents the quantity of green goods associated with the effect.
     * This value is initialized in the EffectWinGoods constructor.
     */
    private int greenGoods;
    /**
     * Represents the quantity of blue goods associated with the effect.
     * It is used to define or track the amount of blue resources manipulated by the effect.
     */
    private int blueGoods;
    /**
     * Stores a list of pairs consisting of a coordinate (Point) and a token (Token).
     * Represents positions and associated tokens affected by the effect.
     */
    private List<Pair<Point, Token>> coordinates;

    /**
     * Constructs an EffectWinGoods object which represents a card effect
     * for winning goods of different types.
     *
     * @param name_effect the name of the effect
     * @param redGoods the quantity of red goods to win
     * @param yellowGoods the quantity of yellow goods to win
     * @param greenGoods the quantity of green goods to win
     * @param blueGoods the quantity of blue goods to win
     */
    public EffectWinGoods(String name_effect, int redGoods, int yellowGoods, int greenGoods, int blueGoods) {
        super(name_effect);
        this.redGoods = redGoods;
        this.yellowGoods = yellowGoods;
        this.greenGoods = greenGoods;
        this.blueGoods = blueGoods;
    }

    /**
     * Retrieves the quantity of red goods associated with this object.
     *
     * @return the number of red goods
     */
    public int getRedGoods() {
        return redGoods;
    }

    /**
     * Retrieves the quantity of yellow goods associated with this effect.
     *
     * @return the amount of yellow goods.
     */
    public int getYellowGoods() {
        return yellowGoods;
    }

    /**
     * Retrieves the number of green goods associated with this effect.
     *
     * @return the number of green goods
     */
    public int getGreenGoods() {
        return greenGoods;
    }

    /**
     * Retrieves the quantity of blue goods in the current object.
     *
     * @return the number of blue goods
     */
    public int getBlueGoods() {
        return blueGoods;
    }

    /**
     * Executes the action associated with this card effect by interacting with the given player's shipboard.
     *
     * @param player the player on which the action is performed; their shipboard tokens may be modified.
     * @throws IllegalMethodOnThisCardException if the coordinates field is not set before invoking this method.
     */
    public void action(Player player) {
//        if (coordinates == null || coordinates.isEmpty()) {
        if (coordinates == null){
            throw new IllegalMethodOnThisCardException("You have to set the coordinates before using this method");
        }
        for (Pair<Point, Token> coordinate : coordinates) {
            Component component = player.getShipBoard().getTile(coordinate.getKey()).getComponent();
            if (component.getLevel() == component.getTokenList().size()) {
                //se è pieno rimuovo il token di minore valore per inserire quello di maggior valore
                boolean isPossible = canAddATokenToThisTile(coordinate.getKey(), coordinate.getValue(), player);
                if (!isPossible) {
                    continue;
                }
                Token tokenToRemove = minValueToken(component.getTokenList());
                player.getShipBoard().removeTokenFromTile(coordinate.getKey(), tokenToRemove);
                System.out.println("rimozione di " + tokenToRemove + " dalla posizione " + coordinate.getKey());
                player.getShipBoard().addTokenToTile(coordinate.getKey(), coordinate.getValue());
                System.out.println("aggiunta di " + coordinate.getValue() + " dalla posizione " + coordinate.getKey());
            } else {
                if (canAddATokenToThisTile(coordinate.getKey(), coordinate.getValue(), player)) {
                    player.getShipBoard().addTokenToTile(coordinate.getKey(), coordinate.getValue());
                    System.out.println("aggiunta di " + coordinate.getValue() + " dalla posizione " + coordinate.getKey());
                }
            }

        }
    }

    /**
     * Checks if a token can be added to the specified tile based on the available goods
     * and updates the count of goods if the addition is successful.
     *
     * @param coordinate the position of the tile where the token is to be added
     * @param token the type of token to be added
     * @param player the player attempting to add the token
     * @return true if the token can be added, false otherwise
     */
    private boolean canAddATokenToThisTile(Point coordinate, Token token, Player player) {
        boolean canAdd = false;

        switch (token) {
            case RED_GOODS:
                canAdd = redGoods > 0;
                if (canAdd) redGoods--;
                break;
            case YELLOW_GOODS:
                canAdd = yellowGoods > 0;
                if (canAdd) yellowGoods--;
                break;
            case GREEN_GOODS:
                canAdd = greenGoods > 0;
                if (canAdd) greenGoods--;
                break;
            case BLUE_GOODS:
                canAdd = blueGoods > 0;
                if (canAdd) blueGoods--;
                break;
        }
        if (canAdd) {
            return true; // si può aggiungere
        } else {
            System.out.println("you are trying to add a token that the card didn't give you or you have already added it the maximum number of times");
            return false; //non si può aggiungere
        }
    }

    /**
     * Finds and returns the token with the minimum value from the given list of tokens.
     *
     * @param tokens the list of tokens to evaluate
     * @return the token with the lowest value in the list
     */
    private Token minValueToken(List<Token> tokens) {
        Token minValueToken = tokens.getFirst();
        for (Token token : tokens) {
            // controllo se il token corrente ha un valore minore rispetto al minimo trovato finora
            if (getTokenValue(token) < getTokenValue(minValueToken)) {
                minValueToken = token;
            }
        }
        return minValueToken;
    }

    /**
     * Returns the value associated with a specific token type.
     *
     * @param token the token whose value is to be retrieved
     * @return the value corresponding to the token, or 0 if the token is not recognized
     */
    private int getTokenValue(Token token) {
        return switch (token) {
            case RED_GOODS -> 4;
            case YELLOW_GOODS -> 3;
            case GREEN_GOODS -> 2;
            case BLUE_GOODS -> 1;
            default -> 0;
        };
    }

    /**
     * This method is intended to set the position of the effect.
     * However, it is not applicable for the current card type and will throw an exception.
     *
     * @param position the intended position to set for the effect
     * @throws IllegalMethodOnThisCardException if attempted on this card
     */
    public void setPosition(int position) {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }

    /**
     * Sets the list of coordinates and associated tokens where the effect is applied.
     *
     * @param positions the list of pairs containing coordinates (Point) and tokens (Token)
     */
    public void setListOfCoordinates(List<Pair<Point, Token>> positions) {
        this.coordinates = positions;
    }

    /**
     * Sets the coordinate where the effect is applied.
     * This method is not allowed for this card effect and will throw an exception.
     *
     * @param coordinate the coordinate where the effect would be applied
     * @throws IllegalMethodOnThisCardException if this method is invoked
     */
    @Override
    public void setCoordinate(Point coordinate) {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }

    /**
     * Gets the number of crew lost.
     * This implementation throws an exception as the method
     * is not applicable for this card effect.
     *
     * @return throws IllegalMethodOnThisCardException since this method is not supported
     */
    @Override
    public int getLostCrew() {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }

    /**
     * Retrieves a list of goods represented by their corresponding integer amounts.
     *
     * @return a list of integers containing the quantities of red, yellow, green, and blue goods.
     */
    @Override
    public List<Integer> getGoods() {
        List<Integer> goodsList = new ArrayList<>();
        goodsList.add(redGoods);
        goodsList.add(yellowGoods);
        goodsList.add(greenGoods);
        goodsList.add(blueGoods);
        return goodsList;
    }
}
