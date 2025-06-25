package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectWinGoods;

/**
 * Represents a planet in the game with specific properties,
 * such as player occupation, effects, and unique identifier.
 */
public class Planet {
    private boolean isOccupied;
    private int playernumber;
    private CardEffect effect;
    /**
     * Unique identifier for the planet.
     */
    private int planetId;
    /**
     * Constructs a Planet with specified goods and a unique identifier.
     * Initializes the planet as unoccupied and assigns it an effect
     * based on the supplied goods.
     *
     * @param redGoods    the number of red goods available on the planet
     * @param yellowGoods the number of yellow goods available on the planet
     * @param greenGoods  the number of green goods available on the planet
     * @param blueGoods   the number of blue goods available on the planet
     * @param planetID    the unique identifier for the planet
     */
    public Planet(int redGoods, int yellowGoods, int greenGoods, int blueGoods,int planetID) {
        this.isOccupied = false;
        this.effect = new EffectWinGoods("Win goods", redGoods, yellowGoods, greenGoods, blueGoods);
        this.planetId = planetID;
    }

    /**
     * Checks whether the planet is currently occupied.
     *
     * @return true if the planet is occupied, false otherwise.
     */
    public boolean isOccupied() {
        return isOccupied;
    }

    /**
     * Sets the occupation state of the planet.
     *
     * @param occupied boolean value indicating if the planet is occupied (true) or not (false).
     */
    public void setOccupied(boolean occupied) {
        isOccupied = occupied;

    }

    /**
     * Retrieves the unique identifier of the planet.
     *
     * @return the planet's unique identifier as an integer.
     */
    public int getPlanetId() {
        return planetId;
    }

    /**
     * Retrieves the effect associated with the planet.
     *
     * @return the CardEffect applied to the planet
     */
    public CardEffect getEffect() {
        return effect;
    }

    /**
     * Retrieves the number associated with the player who occupies the planet.
     *
     * @return the player number. Returns 0 if the planet is unoccupied.
     */
    public int getPlayernumber() {
        return playernumber;
    }

    /**
     * Sets the player number associated with this planet.
     *
     * @param playernumber the number of the player occupying the planet
     */
    public void setPlayernumber(int playernumber) {
        this.playernumber = playernumber;
    }


}
