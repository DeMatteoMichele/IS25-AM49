package it.polimi.ingsw.is25am49.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalUsernameException;
import it.polimi.ingsw.is25am49.client.model.states.*;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.shared.Command;
import it.polimi.ingsw.is25am49.shared.RemoteClient;
import it.polimi.ingsw.is25am49.shared.RemoteServer;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientController {
    public int myId;
    private GameSession gameSession;
    private RemoteServer serverMethods;
    private ServerHandler serverHandler;
    private UserInterface ui;
    private String username;

    private String error = "⚠ Error : ";

    /**
     * Solo per i test
     */
    public ClientController(GameSession gameSession) {
        myId = -1;  //non ancora assegnato
        this.gameSession = gameSession;
    }

    public ClientController(GameSession gameSession, UserInterface ui) {
        myId = -1;  //non ancora assegnato
        this.gameSession = gameSession;
        this.ui = ui;
    }

    /**
     * Sets the server methods for handling remote server operations.
     *
     * @param serverMethods an instance of RemoteServer to handle server commands
     */
    public void setServerMethods(RemoteServer serverMethods) {
        this.serverMethods = serverMethods;
    }

    /**
     * Sets the server handler for the client.
     *
     * @param serverHandler the ServerHandler instance to handle server-related interactions
     */
    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    /**
     * Retrieves the RemoteServer instance associated with this client.
     *
     * @return the RemoteServer instance used for server communication.
     */
    public RemoteServer getServerMethods() {
        return serverMethods;
    }

    /**
     * Handles error notifications for a specific player.
     *
     * @param playerId the ID of the player to whom the error pertains
     * @param errorMessage the error message to be displayed
     */
    public void errorCallback(int playerId, String errorMessage) {
        if (myId == playerId) {
            ui.printUpdateMessage(error + errorMessage);
        }
    }

    /**
     * Registers the client callback by associating the given player ID and saving it to a JSON file.
     * Updates the client ID in the user interface and server handler.
     * Logs error messages if the username is not set or an I/O exception occurs.
     *
     * @param playerId the ID of the player being registered
     */
    public void registerClientCallback(int playerId) {
//        ui.printUpdateMessage("DEBUG: In registerClientCallback - playerId: " + playerId);
        this.myId = playerId;
        ui.setClientId(playerId);
        if(serverHandler!=null) {
            serverHandler.setMyId(playerId);
        }
        if (username == null || username.trim().isEmpty()) {
            ui.printUpdateMessage(error + "Username is not set. Cannot save ID file.");
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("playerId", playerId);
            File idFile = new File("$IdClient" + username + ".json");
            if (idFile.exists()) {
                idFile.delete();
            }
            idFile = new File("$IdClient" + username + ".json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(idFile, rootNode);
        } catch (IOException e) {
            ui.printUpdateMessage(error + "Error while saving ID: " + e.getMessage());
        }
    }

    /**
     * Handles the callback when a player is connected to the game session.
     *
     * @param clientId the unique identifier of the player being connected
     */
    public void playerConnectedCallback(int clientId) {
        try {
            gameSession.getState().connectPlayer(clientId);
//            ui.printUpdateMessage("DEBUG: In playerConnectedCallback player with id " + clientId + " has been connected.");
        } catch (IllegalArgumentException e) {
//            ui.printUpdateMessage("DEBUG: ITS OK! In playerConnectedCallback player not found for id " + clientId);
        }
    }

    /**
     * Handles the disconnection of a player from the game session.
     *
     * @param clientId the ID of the client that has disconnected
     */
    public void playerDisconnectedCallback(int clientId) {
        try {
            gameSession.getState().disconnectPlayer(clientId);
//            ui.printUpdateMessage("DEBUG: In playerDisconnectedCallback player with id " + clientId + " has been disconnected.");
        } catch (Exception e) {
//            ui.printUpdateMessage("DEBUG: ITS OK! In playerDisconnectedCallback player not found for id " + clientId);
        }
    }

    /**
     * Sets up the game as an administrator by creating a new game session.
     *
     * @param playerId the ID of the administrator initiating the setup
     * @param numberOfPlayers the total number of players for the game
     * @param mode the game mode to be set for the session
     */
    public void adminSetupCallback(int playerId, int numberOfPlayers, GameMode mode) {
        try {
            gameSession.getState().adminCreateGame(playerId, mode, numberOfPlayers);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error during game setup --> " + e.getMessage());
        }
    }

    /**
     * Handles a player's join event by registering them into the game session
     * and setting up the seed for generation.
     *
     * @param playerId the unique identifier of the player joining
     * @param username the username of the player
     * @param seedForGeneration the seed value for game generation purposes
     */
    public void playerJoinCallback(int playerId, String username, int seedForGeneration) {
        try {
            gameSession.getState().userJoinLobby(gameSession.getColor(), username, playerId);
            gameSession.setSeedForGeneration(seedForGeneration);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error during player join --> " + e.getMessage());
        }
    }


    /**
     * Handles the callback for a user drawing a tile action.
     *
     * @param playerId The ID of the player performing the action.
     * @param tileId   The ID of the tile being drawn.
     */
    public void userDrawTileCallback(int playerId, int tileId) {
        try {
            gameSession.getState().userDrawTile(playerId, tileId);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while drawing a card: --> " + e.getMessage());
        }

    }

    /**
     * Handles the callback when a user places a tile on the board.
     *
     * @param playerId The ID of the player placing the tile.
     * @param coordinates The coordinates where the tile is placed.
     * @param orientation The orientation of the tile being placed.
     */
    public void userPlaceTileCallback(int playerId, Point coordinates, Orientation orientation) {
        try {
            gameSession.getState().userRotateHandTile(playerId, orientation);
            gameSession.getState().userPlaceTile(playerId, coordinates);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while placing a tile: --> " + e.getMessage());
        }
    }

    /**
     * Handles the action of a user selecting a tile on the board.
     *
     * @param playerId    the unique identifier of the player performing the action.
     * @param coordinates the coordinates of the tile selected by the player.
     */
    public void userPickTileCallback(int playerId, Point coordinates) {
        try {
            gameSession.getState().userPickTile(playerId, coordinates);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while picking a tile: --> " + e.getMessage());
        }
    }

    /**
     * Handles the callback for when a user discards a tile.
     * Calls the game state method to process the tile discard action.
     *
     * @param playerId The ID of the player discarding the tile.
     */
    public void userDiscardTileCallback(int playerId) {
        try {
            gameSession.getState().userDiscardTile(playerId);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while discarding a tile: --> " + e.getMessage());
        }
    }

    /**
     * Handles the callback for finishing the shipboard action for a specified player.
     *
     * @param playerId the unique identifier of the player performing the action
     */
    public void userFinishShipboardCallback(int playerId) {
        try {
            gameSession.getState().userFinishShipboard(playerId);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while finishing shipboard: --> " + e.getMessage());
        }
    }

    /**
     * Handles the client callback for a user looking at a small adventure card deck.
     *
     * @param playerId       The ID of the player making the request.
     * @param smallDeckIndex The index of the small deck being inspected.
     */
    public void userLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) {
        try {
            gameSession.getState().userLookSmallAdventureCardDeck(playerId, smallDeckIndex);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while looking a small deck: --> " + e.getMessage());
        }
    }

    /**
     * Handles the action of a user stopping to look at a small deck of adventure cards.
     *
     * @param playerId the ID of the player initiating the action
     * @param smallDeckIndex the index of the small adventure card deck being looked at
     */
    public void userStopLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) {
        try {
            gameSession.getState().userStopLookSmallAdventureCardDeck(playerId, smallDeckIndex);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while stopping looking a small deck: --> " + e.getMessage());
        }
    }

    /**
     * Handles the callback for user placing a timer in the game session.
     *
     * @param playerId   the ID of the player initiating the timer placement
     * @param isHistory  a flag indicating whether the action is from history or real-time
     */
    public void userPlaceTimerCallback(int playerId, boolean isHistory) {
        if (isHistory) {
            gameSession.getState().userPlaceTimerNoTime(playerId);
        } else{
            try {
                gameSession.getState().userPlaceTimer(playerId);
            } catch (Exception e) {
                ui.printUpdateMessage(error + "Error while placing a timer: --> " + e.getMessage());
            }
        }
    }

    /**
     * Handles the callback when a user selects the type of token.
     * @param playerId The ID of the player making the selection.
     * @param token The type of token selected by the player.
     * @param coordinates The point on the game board where the token is selected.
     */
    public void userSelectTypeOfTokenCallback(int playerId, Token token, Point coordinates) {
        try {
            gameSession.getState().userSelectTypeOfToken(playerId, token, coordinates);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while selecting a token: --> " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the callback for when a user throws dices.
     *
     * @param playerId the unique identifier of the player throwing the dices
     */
    public void userThrowDicesCallback(int playerId) {
        try {
            gameSession.getState().userThrowDices(playerId);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while throwing dices: --> " + e.getMessage());
        }
    }

    /**
     * Handles the callback when a user draws an adventure card.
     *
     * @param playerId The ID of the player drawing the adventure card.
     */
    public void userDrawAdventureCardCallback(int playerId) {
        try {
            gameSession.getState().userDrawAdventureCard(playerId);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while drawing an adventure card: --> " + e.getMessage());
        }
    }

    /**
     * Handles the callback for finishing a decision activation process.
     *
     * @param playerId the unique identifier of the player performing the action
     * @param coordinates the set of coordinates related to the decision activation
     */
    public void userFinishDecisionActivatingCallback(int playerId, Set<Point> coordinates) {
        try {
            gameSession.getState().userFinishDecisionActivating(playerId, coordinates);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while finishing decision activating: --> " + e.getMessage());
        }
    }

    /**
     * Processes the user's decision to finish token selection and notifies the game state.
     *
     * @param playerId      The unique identifier of the player making the decision.
     * @param listOfTokens  A list of token pairs, each containing a position and the selected token.
     */
    public void userFinishDecisionTokenCallback(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        try {
            gameSession.getState().userFinishDecisionToken(playerId, listOfTokens);
        } catch (Exception e) {
            e.printStackTrace();
            ui.printUpdateMessage(error + "Error while finishing decision token: --> " + e.getMessage());
        }
    }

    /**
     * Handles the callback for user acceptance or rejection of prizes.
     *
     * @param playerId    the unique identifier of the player taking the action
     * @param isAccepted  a boolean indicating whether the prizes were accepted
     */
    public void userAcceptPrizesCallback(int playerId, boolean isAccepted) {
        try {
            gameSession.getState().userAcceptPrizes(playerId, isAccepted);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while accepting prizes: --> " + e.getMessage());
        }
    }

    /**
     * Handles the callback event when a user lands on a planet.
     *
     * @param playerId the ID of the player initiating the landing
     * @param isLanded whether the player successfully landed on the planet
     * @param planetId the ID of the planet where the landing occurred
     */
    public void userLandOnPlanetCallback(int playerId, boolean isLanded, int planetId) {
        try {
            gameSession.getState().userLandOnPlanet(playerId, isLanded, planetId);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while landing on planet: --> " + e.getMessage());
        }
    }

    /**
     * Handles the callback when a user lands on a station or ship, triggering the corresponding state action.
     *
     * @param playerId The ID of the player performing the action.
     * @param isLanded Indicates whether the player has successfully landed.
     */
    public void userLandOnStationOrShipCallback(int playerId, boolean isLanded) {
        try {
            gameSession.getState().userLandOnStationOrShip(playerId, isLanded);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while landing on station or ship: --> " + e.getMessage());
        }
    }

    /**
     * Handles the event where a user stops their flight during the game session.
     *
     * @param playerId the unique identifier of the player stopping their flight
     */
    public void userStopFlightCallback(int playerId) {
        try {
            gameSession.getState().userStopFlight(playerId);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while stopping flight: --> " + e.getMessage());
        }
    }

    /**
     * Handles the callback to calculate the admin's score for the specified player.
     *
     * @param playerId the unique identifier of the player for whom the score is being calculated
     */
    public void adminCalculateScoreCallback(int playerId) {
        try {
            gameSession.getState().adminCalculateScore(playerId);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while calculating score: --> " + e.getMessage());
        }
    }

    /**
     * Ends the game for a specific player initiated by the admin.
     *
     * @param playerId the ID of the player for whom the game is being ended
     */
    public void adminEndGameCallback(int playerId) {
        try {
            gameSession.getState().adminEndGame(playerId);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while ending game: --> " + e.getMessage());
        }
    }

    /**
     * Transitions the game session state based on the provided state name.
     *
     * @param state the name of the state to transition to, e.g., "InitState", "BuildState", "AdventureState", or "FinishScoreState"
     * @throws IllegalArgumentException if the provided state name is invalid
     */
    public void changeStateCallback(String state) {
        if (state.equals("InitState")) {
            gameSession.getState().transition(new InitState(gameSession, ui));
        } else if (state.equals("BuildState")) {
            gameSession.getState().transition(new BuildState(gameSession, ui));
        } else if (state.equals("AdventureState")) {
            gameSession.getState().transition(new AdventureState(gameSession, ui));
        } else if (state.equals("FinishScoreState")) {
            gameSession.getState().transitionToFinish(new FinishScoreState(gameSession, ui));
        } else {
            throw new IllegalArgumentException("Invalid state: " + state);
        }
    }

    /**
     * Registers a RemoteClient instance for Remote Method Invocation (RMI) communication.
     *
     * @param client the RemoteClient object to be registered
     * @throws RemoteException if a remote communication error occurs
     * @throws IOException if an I/O operation fails during registration
     */
    public void registerClientRMI(RemoteClient client) throws RemoteException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        File idFile = new File("$IdClient" + username + ".json");
        myId = mapper.readTree(idFile).get("playerId").asInt();
        Command command = new Command("registerClientRMI", myId);
//        ui.printUpdateMessage("DEBUG: In registerClientRMI - myId: " + myId+" using idFile: "+idFile.getAbsolutePath());
        command.addParameter(client);
        serverMethods.command_FromClientToServer(command);
    }

    /**
     * Registers the client with the server via socket-based communication.
     * Loads client-specific information from a JSON file to initialize the player's ID.
     * Sends a registration command to the server to establish the connection.
     */
    public void registerClientSocket() throws RemoteException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        File idFile = new File("$IdClient" + username + ".json");
        myId = mapper.readTree(idFile).get("playerId").asInt();
        Command command = new Command("registerClientSocket", myId);
//        ui.printUpdateMessage("DEBUG: In registerClientSocket - myId: " + myId+" using idFile: "+idFile.getAbsolutePath());
        serverHandler.connectCommand();
        serverMethods.command_FromClientToServer(command);
    }


    /**
     * Sets up the game session as an admin by configuring the number of players and game mode.
     *
     * @param numberOfPlayers the number of players to participate
     * @param mode the selected game mode identifier
     */
    public void adminSetup(int numberOfPlayers, int mode) {
        try {
            gameSession.getState().checkAdminCreateGame(myId, mode, numberOfPlayers);
            Command command = new Command("adminSetup", myId);
            command.addParameter(myId);
            command.addParameter(numberOfPlayers);
            command.addParameter(GameMode.values()[mode]);
            serverMethods.command_FromClientToServer(command);
        }catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }
    }

    /**
     * Handles the process of a player joining the game lobby.
     *
     * @param username The username of the player attempting to join the game.
     */
    public void playerJoin(String username) {
        try {
//            System.out.println("DEBUG: In playerJoin - myId: " + myId + ", username: " + username);
            gameSession.getState().checkUserJoinLobby(myId, username);
            Command command = new Command("playerJoin", myId);
            command.addParameter(myId);
            command.addParameter(username);
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }
    }

    /**
     * Allows a player to draw a specific tile in the game.
     *
     * @param playerId the identifier of the player drawing the tile
     * @param tileId the identifier of the tile being drawn
     */
    public void userDrawTile(int playerId, int tileId) {

        gameSession.getState().checkUserDrawTile(playerId, tileId);
        Command command = new Command("userDrawTile", myId);
        command.addParameter(playerId);
        command.addParameter(tileId);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }

    }

    /**
     * Handles the action of a user placing a tile during the game.
     *
     * @param playerId the ID of the player placing the tile
     * @param coordinates the location where the tile is being placed
     * @param orientation the orientation of the tile being placed
     */
    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) {
        gameSession.getState().checkUserPlaceTile(playerId, coordinates);
        Command command = new Command("userPlaceTile", myId);
        command.addParameter(playerId);
        command.addParameter(coordinates);
        command.addParameter(orientation);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }

    }

    /**
     * Handles the action of a user picking a tile in the game.
     *
     * @param playerId    the ID of the player making the selection
     * @param coordinates the coordinates of the tile being selected
     */
    public void userPickTile(int playerId, Point coordinates) {
        gameSession.getState().checkUserPickTile(playerId, coordinates);
        Command command = new Command("userPickTile", myId);
        command.addParameter(playerId);
        command.addParameter(coordinates);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }
    }

    /**
     * Allows a user to discard a tile during the game session.
     *
     * @param playerId the unique identifier of the player discarding the tile
     */
    public void userDiscardTile(int playerId) {
        gameSession.getState().checkUserDiscardTile(playerId);
        Command command = new Command("userDiscardTile", myId);
        command.addParameter(playerId);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }

    }

    /**
     * Notifies the server that the specified player has finished their shipboarding phase.
     *
     * @param playerId the unique identifier of the player finishing the shipboarding phase
     */
    public void userFinishShipboard(int playerId) {
        gameSession.getState().checkUserFinishShipboard(playerId);
        Command command = new Command("userFinishShipboard", myId);
        Command command2 = new Command("userDiscardTile", myId);
        command.addParameter(playerId);
        command2.addParameter(playerId);
        try {
            if(gameSession.getPlayer(playerId).get().getHandTile().isPresent()){
                serverMethods.command_FromClientToServer(command2);
        }
            serverMethods.command_FromClientToServer(command);

        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }

    }

    /**
     * Requests a player to view a specified small adventure card deck within the game session.
     * @param playerId the ID of the player making the request
     * @param smallDeckIndex the index of the small adventure card deck to be viewed
     */
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        gameSession.getState().checkUserLookSmallAdventureCardDeck(playerId, smallDeckIndex);
        Command command = new Command("userLookSmallAdventureCardDeck", myId);
        command.addParameter(playerId);
        command.addParameter(smallDeckIndex);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }

    }

    /**
     * Handles the action where a user stops looking at a small adventure card deck.
     *
     * @param playerId       The identifier of the player performing the action.
     * @param smallDeckIndex The index of the small adventure card deck being stopped.
     */
    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {

        gameSession.getState().checkUserStopLookSmallAdventureCardDeck(playerId, smallDeckIndex);
        Command command = new Command("userStopLookSmallAdventureCardDeck", myId);
        command.addParameter(playerId);
        command.addParameter(smallDeckIndex);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }

    }

    /**
     * Sends a command to initiate the timer for the user's move in the game session.
     *
     * @param playerId the unique identifier of the player starting their timer
     */
    public void userPlaceTimer(int playerId) {
        gameSession.getState().checkUserPlaceTimer(playerId);
        Command command = new Command("userPlaceTimer", myId);
        command.addParameter(playerId);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }

    }

    /**
     * Handles the selection of a specific type of token by the user, sending the corresponding command to the server.
     *
     * @param playerId the ID of the player making the selection
     * @param token the type of token selected by the player
     * @param coordinates the coordinates associated with the token selection
     */
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) {
        gameSession.getState().checkUserSelectTypeOfToken(playerId, token, coordinates);
        Command command = new Command("userSelectTypeOfToken", myId);
        command.addParameter(playerId);
        command.addParameter(token);
        command.addParameter(coordinates);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }
    }

    /**
     * Sends a request to the server for the specified player to throw dices.
     *
     * @param playerId the ID of the player who is throwing the dices
     */
    public void userThrowDices(int playerId) {
        gameSession.getState().checkUserThrowDices(playerId);
        Command command = new Command("userThrowDices", myId);
        command.addParameter(playerId);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }

    }

    /**
     * Handles the user's request to draw an adventure card, validating the action
     * and sending the corresponding command to the server.
     *
     * @param playerId the ID of the player requesting to draw the adventure card
     */
    public void userDrawAdventureCard(int playerId) {
        gameSession.getState().checkUserDrawAdventureCard(playerId);
        Command command = new Command("userDrawAdventureCard", myId);
        command.addParameter(playerId);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }

    }

    /**
     * Sends a request to finish the decision activation process for a specific player.
     *
     * @param playerId     the unique identifier of the player who is finishing the decision activation
     * @param coordinates  a set of points associated with the player's decision
     */
    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) {
        gameSession.getState().checkUserFinishDecisionActivating(playerId, coordinates);
        Command command = new Command("userFinishDecisionActivating", myId);
        command.addParameter(playerId);
        command.addParameter(coordinates);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }

    }

    /**
     * Sends the client's decision to finish token-related actions for the specified player.
     *
     * @param playerId The ID of the player finalizing the decision.
     * @param listOfTokens A list of token-position pairs representing the player's decisions.
     */
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {

        gameSession.getState().checkUserFinishDecisionToken(playerId, listOfTokens);
        Command command = new Command("userFinishDecisionToken", myId);
        command.addParameter(playerId);
        command.addParameter(listOfTokens);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }

    }

    /**
     * Handles a user's decision to accept or decline prizes.
     *
     * @param playerId the ID of the player making the decision
     * @param wantsRewards true if the player accepts the prizes, false otherwise
     */
    public void userAcceptPrizes(int playerId, boolean wantsRewards) {

        gameSession.getState().checkUserAcceptPrizes(playerId, wantsRewards);
        Command command = new Command("userAcceptPrizes", myId);
        command.addParameter(playerId);
        command.addParameter(wantsRewards);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }
    }

    /**
     * Handles the user's action of landing on a planet.
     *
     * @param playerId the ID of the player attempting to land
     * @param isLanded indicates whether the player successfully landed
     * @param planetId the ID of the planet being landed on
     */
    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        gameSession.getState().checkUserLandOnPlanet(playerId, isLanded, planetId);
        Command command = new Command("userLandOnPlanet", myId);
        command.addParameter(playerId);
        command.addParameter(isLanded);
        command.addParameter(planetId);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }
    }

    /**
     * Handles the action when a user lands on a station or a ship.
     *
     * @param playerId  the unique ID of the player.
     * @param isLanded  true if the player has landed, false otherwise.
     */
    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        gameSession.getState().checkUserLandOnStationOrShip(playerId, isLanded);
        Command command = new Command("userLandOnStationOrShip", myId);
        command.addParameter(playerId);
        command.addParameter(isLanded);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }
    }

    /**
     * Notifies the server that the specified user wants to quit the game.
     *
     * @param playerId the ID of the player who is quitting the game
     */
    public void userQuitGame(int playerId) {
        try {
            Command command = new Command("userQuitGame", myId);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    /**
     * Calculates the score for the specified player.
     *
     * @param playerId the ID of the player whose score is to be calculated
     */
    public void adminCalculateScore(int playerId) {
//        gameSession.getState().checkAdminCalculateScore(playerId); everyone can calculate the score
        Command command = new Command("adminCalculateScore", myId);
        command.addParameter(playerId);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }
    }

    /**
     * Forces the end of a game session initiated by an admin command.
     *
     * @param playerId the unique identifier of the player associated with the end game operation
     */
    public void adminEndGame(int playerId) {
//        gameSession.getState().checkAdminEndGame(playerId); everyone can end the game
        Command command = new Command("adminEndGame", myId);
        command.addParameter(playerId);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }
    }

    /**
     * Sends a command to stop the flight for the specified player.
     * @param playerId the unique identifier of the player whose flight will be stopped
     */
    public void userStopFlight(int playerId) {
        Command command = new Command("userStopFlight", myId);
        command.addParameter(playerId);
        try {
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException e) {
            ui.printUpdateMessage("Error: you are disconnected from ServerRMI");
        }
    }

    /**
     * Retrieves the unique identifier of the player.
     * @return the player ID as an integer
     */
    public int getplayerId() {
        return myId;
    }

    /**
     * Sets the username for the user.
     * @param username the username to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Creates or updates a JSON file containing the player's ID for the specified username.
     * Deletes the existing file if it already exists, and writes the new ID data into it.
     * Logs messages about success or errors during file operations.
     */
    public void createMyIdJson() {
//        ui.printUpdateMessage("DEBUG: In createMyIdJson - Tentativo di scrivere myId: " + myId + " per username: " + username + " nel file: $IdClient" + username + ".json");
        File idFile = new File("$IdClient" + username + ".json");
        try {
            if (idFile.exists()) {
                idFile.delete();
            }
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("playerId", myId);
            idFile = new File("$IdClient" + username + ".json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(idFile, rootNode);
//            ui.printUpdateMessage("DEBUG: File " + idFile.getAbsolutePath() + " scritto con successo in createMyIdJson.");
        } catch (IOException e) {
            ui.printUpdateMessage(error + "Errore durante l'inizializzazione dell'ID (scrittura file): " + e.getMessage() + " per file: " + idFile.getAbsolutePath());
        }
    }

    /**
     * Resets the client state by initializing a new game session
     * and linking it with the user interface, while resetting the ID.
     */
    public void clientReset() {
        myId = -1;
        gameSession = new GameSession(ui);
        ui.setGameSession(gameSession);
    }
}