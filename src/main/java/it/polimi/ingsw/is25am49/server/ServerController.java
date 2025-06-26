package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.shared.Command;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.*;

public class ServerController {
    /**
     * Represents the active instance of the game session managed by the server.
     */
    private final GameSession gameSession;
    /**
     * Handles serialization and deserialization of commands between the server and connected clients.
     */
    private ServerSerializer serverSerializer;
    /**
     * Manages Remote Method Invocation (RMI) communication for the server.
     */
    private ServerRMI serverRMI;
    /**
     * Indicates whether the game has started.
     */
    private boolean gameStarted = false;


    /**
     * Initializes the ServerController with the provided game session.
     * @param gameSession the game session to be controlled
     */
    public ServerController(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    /**
     *
     */
    public void setSerializer(ServerSerializer serverSerializer) {
        this.serverSerializer = serverSerializer;
    }

    /**
     * Sets up a new game by the administrator with specified parameters.
     *
     * @param playerId the ID of the player initiating the setup.
     * @param numberOfPlayers the maximum number of players for the game.
     * @param mode the game mode to be set (e.g., FLIGHT_MODE, LEVEL_1, etc.).
     */
    public void adminSetup(int playerId, int numberOfPlayers, GameMode mode) {
        try {
            try {
                gameSession.getState().adminCreateGame(playerId, mode, numberOfPlayers);
                Command command = new Command("adminSetupCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(numberOfPlayers);
                command.addParameter(mode);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (GameSettingsException e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("AdminSetup hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server in adminSetup ->   " + e.getMessage());
        }
    }

    /**
     * Allows a player to join the game session with the provided ID and username.
     *
     * @param playerId the unique identifier of the player joining
     * @param username the username of the player joining
     */
    public void playerJoin(int playerId, String username) {
        try {
            try {
                gameSession.getState().userJoinLobby(username, playerId);
                Command command = new Command("playerJoinCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(username);
                command.addParameter(gameSession.getSeedForGeneration());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
                if (gameStarted) {
                    serverSerializer.signalThatGameStarted();
                    serverRMI.signalThatGameStarted();
                    System.out.println("Game started");
                }
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("PlayerJoin hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in playerJoin ->   " + e.getMessage());
        }
    }

    /**
     * Handles the logic for when a player connects to the game.
     *
     * @param playerId the unique identifier of the player connecting to the game
     */
    public void playerConnected(int playerId) {
        try {
            gameSession.getState().connectPlayer(playerId);
            Command command = new Command("playerConnectedCallback", playerId);
            command.addParameter(playerId);
            serverSerializer.command_FromServerToClient(command);
            serverRMI.command_FromServerToClient(command);
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in playerConnected ->   " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the disconnection of a player from the game session and notifies clients.
     *
     * @param playerId the unique identifier of the player being disconnected.
     */
    public void playerDisconnected(int playerId) {
        try {
            gameSession.getState().disconnectPlayer(playerId);
            Command command = new Command("playerDisconnectedCallback", playerId);
            command.addParameter(playerId);
            serverSerializer.command_FromServerToClient(command);
            serverRMI.command_FromServerToClient(command);
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in playerDisconnected ->   " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Allows a player to draw a tile from the game session.
     *
     * @param playerId the unique identifier of the player performing the action
     * @param tileId the unique identifier of the tile to be drawn
     */
    public void userDrawTile(int playerId, int tileId) {
        try {
            try {
                gameSession.getState().userDrawTile(playerId, tileId);
                Command command = new Command("userDrawTileCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(tileId);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Tile with id " + tileId + " not available or already drawn");
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.err.println("Error due to RemoteException in Server  in drawTile ->   " + e.getMessage());
        }

    }

    /**
     * Allows a player to place a tile with a specific orientation at the given coordinates.
     *
     * @param playerId the ID of the player performing the action
     * @param coordinates the coordinates where the tile is to be placed
     * @param orientation the orientation of the tile being placed
     */
    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) {
        try {
            try {
                gameSession.getState().userRotateTile(playerId, orientation);
                gameSession.getState().userPlaceTile(playerId, coordinates);
                Command command = new Command("userPlaceTileCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(coordinates);
                command.addParameter(orientation);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Place tile hasn't worked in Server ->  " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.err.println("Error due to RemoteException in Server  in placeTile ->   " + e.getMessage());
        }
    }


    /**
     * Handles the action of a user picking a tile at specified coordinates.
     *
     * @param playerId        the ID of the player performing the action.
     * @param coordinates     the coordinates of the tile being picked.
     */
    public void userPickTile(int playerId, Point coordinates) {
        try {
            try {
                gameSession.getState().userPickTile(playerId, coordinates);
                Command command = new Command("userPickTileCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(coordinates);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Pick tile hasn't worked in Server ->  " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in pickTile ->   " + e.getMessage());
        }
    }

    /**
     * Handles the action of a player discarding a tile in the game.
     *
     * @param playerId the unique ID of the player performing the discard action
     */
    public void userDiscardTile(int playerId) {
        try {
            try {
                gameSession.getState().userDiscardTile(playerId);
                Command command = new Command("userDiscardTileCallback", playerId);
                command.addParameter(playerId);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Discard tile hasn't worked in Server ->  " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in discardTile ->   " + e.getMessage());
        }
    }

    /**
     *
     */
    public void userFinishShipboard(int playerId) {
        try {
            try {
                gameSession.getState().userFinishShipboard(playerId);
                Command command = new Command("userFinishShipboardCallback", playerId);
                command.addParameter(playerId);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Finish shipboard hasn't worked in Server ->  " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in finishShipboard ->   " + e.getMessage());
        }
    }

    /**
     *
     */
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        try {
            try {
                gameSession.getState().userLookSmallAdventureCardDeck(playerId, smallDeckIndex);
                Command command = new Command("userLookSmallAdventureCardDeckCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(smallDeckIndex);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Look small adventure card deck hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in lookSmallAdventureCardDeck ->   " + e.getMessage());
        }
    }

    /**
     * Handles the user action for stopping the view of a small adventure card deck.
     *
     * @param playerId the unique identifier of the player performing this action
     * @param smallDeckIndex the index of the small adventure card deck being viewed
     */
    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        try {
            try {
                gameSession.getState().userStopLookSmallAdventureCardDeck(playerId, smallDeckIndex);
                Command command = new Command("userStopLookSmallAdventureCardDeckCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(smallDeckIndex);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Stop look small adventure card deck hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in stopLookSmallAdventureCardDeck ->   " + e.getMessage());
        }
    }

    /**
     * Handles the initiation of a user-specific place timer for in-game actions and communicates results.
     *
     * @param playerId the unique identifier of the player initiating the timer.
     */
    public void userPlaceTimer(int playerId) {
        try {
            try {
                gameSession.getState().userPlaceTimer(playerId);
                Command command = new Command("userPlaceTimerCallback", playerId);
                command.addParameter(playerId);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Place timer hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in placeTimer ->   " + e.getMessage());
        }
    }

    /**
     * Allows a user to select a specific type of token in the game.
     *
     * @param playerId the ID of the player making the selection
     * @param token the type of token selected
     * @param coordinates the coordinates of the token on the game board
     */
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) {

        try {
            try {
                gameSession.getState().userSelectTypeOfToken(playerId, token, coordinates);
                Command command = new Command("userSelectTypeOfTokenCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(token);
                command.addParameter(coordinates);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Select type of token hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in selectTypeOfToken ->   " + e.getMessage());
        }
    }

    /**
     * Handles the dice-throwing action performed by a user in the game session.
     *
     * @param playerId the ID of the player initiating the dice throw.
     */
    public void userThrowDices(int playerId) {
        try {
            try {
                gameSession.getState().userThrowDices(playerId);
                Command command = new Command("userThrowDicesCallback", playerId);
                command.addParameter(playerId);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Throw dices hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in throwDices ->   " + e.getMessage());
        }
    }

    /**
     * Allows a player to draw an adventure card in the game.
     *
     * @param playerId the unique identifier of the player performing the action
     */
    public void userDrawAdventureCard(int playerId) {
        try {
            try {
                gameSession.getState().userDrawAdventureCard(playerId);
                Command command = new Command("userDrawAdventureCardCallback", playerId);
                command.addParameter(playerId);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Draw adventure card hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in drawAdventureCard ->   " + e.getMessage());
        }
    }

    /**
     * Handles the user's decision to finish activating specific points during the game session.
     *
     * @param playerId the ID of the player finishing the decision activating.
     * @param coordinates the set of points associated with the user's decision.
     */
    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) {
        try {
            try {
                gameSession.getState().userFinishDecisionActivating(playerId, coordinates);
                Command command = new Command("userFinishDecisionActivatingCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(coordinates);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Finish decision activating hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in finishDecisionActivating ->   " + e.getMessage());
        }
    }

    /**
     * Finalizes the player's token decision during their turn.
     *
     * @param playerId the ID of the player finishing their token decision
     * @param listOfTokens the list of token and point pairs chosen by the player
     */
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        try {
            try {
                gameSession.getState().userFinishDecisionToken(playerId, listOfTokens);
                Command command = new Command("userFinishDecisionTokenCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(listOfTokens);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Finish decision token hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in finishDecisionToken ->   " + e.getMessage());
        }
    }

    /**
     * Handles the user's decision to accept or decline prizes in the game.
     *
     * @param playerId the unique identifier of the player making the decision.
     * @param wantsRewards a boolean indicating whether the player accepts the prizes.
     */
    public void userAcceptPrizes(int playerId, boolean wantsRewards) {
        try {
            try {
                gameSession.getState().userAcceptPrizes(playerId, wantsRewards);
                Command command = new Command("userAcceptPrizesCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(wantsRewards);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Accept prizes hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in acceptPrizes ->   " + e.getMessage());
        }
    }

    /**
     * Handles the event of a user landing on a planet during the game.
     *
     * @param playerId the ID of the player performing the action.
     * @param isLanded indicates whether the player successfully landed on the planet.
     * @param planetId the ID of the target planet.
     */
    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        try {
            try {
                gameSession.getState().userLandOnPlanet(playerId, isLanded, planetId);
                Command command = new Command("userLandOnPlanetCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(isLanded);
                command.addParameter(planetId);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Land on planet hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in landOnPlanet ->   " + e.getMessage());
        }
    }

    /**
     * Handles the action of a user landing on a station or ship.
     *
     * @param playerId the unique identifier of the player performing the action
     * @param isLanded a boolean indicating whether the user has successfully landed
     */
    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        try {
            try {
                gameSession.getState().userLandOnStationOrShip(playerId, isLanded);
                Command command = new Command("userLandOnStationOrShipCallback", playerId);
                command.addParameter(playerId);
                command.addParameter(isLanded);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Land on station or ship hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in landOnStationOrShip ->   " + e.getMessage());
        }
    }

    /**
     * Handles the action where a user stops their flight in the game.
     *
     * @param playerId the unique identifier of the player stopping the flight
     */
    public void userStopFlight(int playerId) {
        try {
            try {
                gameSession.getState().userStopFlight(playerId);
                Command command = new Command("userStopFlightCallback", playerId);
                command.addParameter(playerId);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Stop flight hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in stopFlight ->   " + e.getMessage());
        }
    }

//    public void userQuitGame(int playerId) {
//        try {
//            try {
//                gameSession.getState().userQuitGame(playerId);
//                Command command = new Command("userQuitGameCallback", playerId);
//                command.addParameter(playerId);
//                serverSerializer.command_FromServerToClient(command);
//                serverRMI.command_FromServerToClient(command);
//            } catch (Exception e) {
//                Command command = new Command("errorCallback", playerId);
//                command.addParameter(playerId);
//                command.addParameter("Quit game hasn't worked in Server ->   " + e.getMessage());
//                serverSerializer.command_FromServerToClient(command);
//                serverRMI.command_FromServerToClient(command);
//            }
//        } catch (RemoteException e) {
//            System.out.println("Error due to RemoteException in Server  in quitGame ->   " + e.getMessage());
//        }
//    }

    /**
     * Sends a command to change the state of the server and informs connected clients.
     * @param state the new state to be set for the server
     */
    public void changeState(String state) {
        try {
            Command command = new Command("changeStateCallback", -1);
            command.addParameter(state);
            serverSerializer.command_FromServerToClient(command);
            serverRMI.command_FromServerToClient(command);
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in changeState ->   " + e.getMessage());
        } catch (Exception e) {
        }
    }

    /**
     * Calculates the score for a specific player by the admin and sends appropriate commands back to the client.
     *
     * @param playerId the unique identifier of the player whose score is to be calculated.
     */
    public void adminCalculateScore(int playerId) {
        try {
            try {
                gameSession.getState().adminCalculateScore(playerId);
                Command command = new Command("adminCalculateScoreCallback", playerId);
                command.addParameter(playerId);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("Calculate score hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in calculateScore ->   " + e.getMessage());
        }
    }

    /**
     * Ends the game and notifies all clients.
     * @param playerId the unique identifier of the player whose game is being ended.
     *
     */
    public void adminEndGame(int playerId) {
        try {
            try {
                gameSession.getState().userEndGame(playerId);
                Command command = new Command("adminEndGameCallback", playerId);
                command.addParameter(playerId);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", playerId);
                command.addParameter(playerId);
                command.addParameter("End game hasn't worked in Server ->   " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in endGame ->   " + e.getMessage());
        }
    }

    public void setServerRMI(ServerRMI serverRMI) {
        this.serverRMI = serverRMI;
    }


    public void signalThatGameStarted() {
        gameStarted = true;
    }

}
