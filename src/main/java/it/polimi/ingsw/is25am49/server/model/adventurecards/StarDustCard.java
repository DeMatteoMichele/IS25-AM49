package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;

/**
 * Represents a Star Dust card in the game. It is a specific type of AdventureCard
 * with unique effects when drawn by a player.
 */
public class StarDustCard extends AdventureCard {

    /**
     * Constructs a StarDustCard, representing a special type of AdventureCard.
     *
     * @param gameBoard the current game board instance associated with the card
     * @param description a description of the StarDust card
     * @param level the level of the card
     * @param name the name of the card
     */
    public StarDustCard(GameBoard gameBoard, String description, int level,String name) {
        super(gameBoard, description, level,name);
    }


    /**
     * Executes the logic for drawing the Star Dust card.
     * Updates the game state to EFFECT and applies the specific
     * penalty effect associated with the card.
     */
    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found a Star Dust! You will lose traveldays as much as you have exposed connectors on you ship!");
        currentState = AdventureStatesTypes.EFFECT;
        applyEffect();
    }

    /**
     * Applies the effect of the Star Dust card to all players on the leaderboard.
     *
     * For each player, the number of exposed connectors on their ship is calculated.
     * If there are any exposed connectors, the player's "days this turn" is decreased
     * by the number of exposed connectors. Updates the game state after applying
     * the effect to all players.
     */
    @Override
    public void applyEffect() {
        super.applyEffect();
        for (int i = 1; i <= gameBoard.getLeaderBoard().size(); i++) {
            Player player = gameBoard.getLeaderBoard().getPlayerInPosition(i);
            gameBoard.setCurrentPlayer(player);
            int numExposedConnectors = player.getShipBoard().getNumberOfExposedConnectors();
            if (numExposedConnectors > 0) {
                player.setDaysThisTurn(-numExposedConnectors);
            }
        }
        update();
    }

    /**
     * Generates a formatted string representation of the object, displayed within
     * a bordered ASCII box. Includes details such as name, description, and level.
     *
     * @return a string representation of the object in a formatted box layout
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        final int width = 90;  // Larghezza standard del box
        String border = "+" + "-".repeat(width - 2) + "+\n";
        String separator = "|" + "-".repeat(width - 2) + "|\n";
        str.append(border);
        String nameText = "| NAME: " + name;
        str.append(nameText).append(" ".repeat(width - nameText.length() - 1)).append("|\n");
        str.append(separator);
        String descText = "| DESCRIPTION: " + description;
        appendTextWithWrapping(str, descText, width);
        String levelText = "| LEVEL: " + level;
        str.append(levelText).append(" ".repeat(width - levelText.length() - 1)).append("|\n");
        str.append(border);
        return str.toString();
    }
}
