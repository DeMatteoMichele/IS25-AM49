package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.Game;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import java.awt.*;

public abstract class AdventureState extends State {
    public AdventureState(Game game){
        super(game);
    }
    public void adminSelectGameMode(GameMode gamemode) {}
    public void userSelectColor(Player player, Color playerColor) {}
    public void userInsertName(Player player,String playerName){}

    // public void adminCreateLobby(Player) {}
    // public void userJoinLobby() {}
    // public void adminStartGame() {}

    public void userPlaceTile(Player player, Point coordinates) {}
    public void userRotateTile(Player player, Point coordinates) {}
    public void userPickTile(Player player, Point coordinates) {}
    public void userDiscardTile(Player player) {}
    public void userDrawTile(Player player, int idTIle) {}
    public void userFinishShipboard(Player player) {}
    public abstract void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex);
    public abstract void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex);
    public abstract void userPlaceTimer(Player player);
    public abstract void userThrowDices(Player player);


    public abstract void userDrawAdventureCard(Player player);
    public abstract void userPlayAdventureCard(Player player);
    public abstract void userEffectDecision(Player player);
    public abstract void userActivateComponent(Player player, Point coordinates);
    public abstract void userActivateDaysUpdate();
    public abstract void userQuitGame(Player player);
}
