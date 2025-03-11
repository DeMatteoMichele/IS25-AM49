package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.Game;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import java.awt.*;

public class BuildState extends State {
    public BuildState(Game game){
        super(game);
    }
    public void adminSelectGameMode(GameMode gamemode) {}
    public void userSelectColor(Player player, Color playerColor) {}
    public void userInsertName(Player player,String playerName) {}

    // public void adminCreateLobby(Player) {}
    // public void userJoinLobby() {}
    // public void adminStartGame() {}

    public void userPlaceTile(Player player, Point coordinates) {}
    public void userRotateTile(Player player, Point coordinates) {}
    public void userPickTile(Player player, Point coordinates) {}
    public void userDiscardTile(Player player) {}
    public void userDrawTile(Player player, int idTIle) {}
    public void userFinishShipboard(Player player) {}
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) {}
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) {}
    public void userPlaceTimer(Player player) {}
    public void userThrowDices(Player player) {}


    public void userDrawAdventureCard(Player player) {}
    public void userPlayAdventureCard(Player player) {}
    public void userEffectDecision(Player player) {}
    public void userActivateComponent(Player player, Point coordinates) {}
    public void userActivateDaysUpdate() {}
    public void userQuitGame(Player player) {}
}
