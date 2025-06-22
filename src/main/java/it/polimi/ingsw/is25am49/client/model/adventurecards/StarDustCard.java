package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;

public class StarDustCard extends AdventureCard {

    public StarDustCard(String cardId, GameBoard gameBoard, String description, int level, String name) {
        super(cardId, gameBoard, description, level, name);
    }

    @Override
    public void drawCard() {
        super.drawCard();
        userInterface.updateGenericCardMessage(genericIdForUpdates, "You have found a Star Dust! You will lose traveldays as much as you have exposed connectors on you ship!");
        currentState = AdventureStatesTypes.EFFECT;
        userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
        applyEffect();
    }

    @Override
    public void applyEffect() {
        super.applyEffect();
        for (int i = 1; i <= gameBoard.getLeaderBoard().size(); i++) {
            Player player = gameBoard.getLeaderBoard().getPlayerInPosition(i);
            gameBoard.setCurrentPlayer(player);
            int numExposedConnectors = player.getShipBoard().getNumberOfExposedConnectors();
            if (numExposedConnectors > 0) {
                player.setDaysThisTurn(-numExposedConnectors);
                userInterface.updateGenericCardMessage(genericIdForUpdates, gameBoard.getCurrentPlayer().getUsername() + "has " + numExposedConnectors + " exposed connectors on your ship, he loses " + numExposedConnectors + " travel days.");
            }
        }
        update();
    }

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
