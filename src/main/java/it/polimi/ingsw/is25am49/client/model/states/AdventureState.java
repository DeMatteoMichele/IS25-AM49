package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class AdventureState extends State {
    public AdventureState(GameSession gameSession){
        super(gameSession);
    }
    public void adminSelectGameMode(Player player, GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userJoinLobby(Color playerColor, String username, int playerID) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void adminCreateGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void adminStartGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userPlaceTile(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userPickTile(Player player, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userDiscardTile(Player player){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userDrawTile(Player player, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userFinishShipboard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userStopLookSmallAdventureCardDeck(Player player, int smallDeckIndex){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userPlaceTimer(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void timerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userSelectTypeOfToken(Player player, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userThrowDices(Player player){
        gameSession.userThrowDices(player);
    }
    public void userDrawAdventureCard(Player player) {
        gameSession.userDrawAdventureCard(player);
    }
    public void userFinishDecisionActivating(Player player, Set<Point> coordinates){

    }
    public void userFinishDecisionToken(Player player, ArrayList<Pair<Point, Token>> listOfTokens){

    }
    public void userLandOnPlanet(Player player) {

    }
    public void userLandOnStationOrShip(Player player) {

    }
    public void userQuitGame(Player player){

    }

    public void userStopFlight(Player player) {

    }

    public void adminCalculateScore(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userEndGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
