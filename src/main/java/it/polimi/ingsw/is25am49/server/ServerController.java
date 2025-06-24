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
    private final GameSession gameSession;
    private ServerSerializer serverSerializer;
    private ServerRMI serverRMI;
    private boolean gameStarted = false;


    public ServerController(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public void setSerializer(ServerSerializer serverSerializer) {
        this.serverSerializer = serverSerializer;
    }

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
                e.printStackTrace();
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in throwDices ->   " + e.getMessage());
        }
    }

    public void userDrawAdventureCard(int playerId) {
        try {
            try {
                gameSession.getState().userDrawAdventureCard(playerId);
                Command command = new Command("userDrawAdventureCardCallback", playerId);
                command.addParameter(playerId);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
               // e.printStackTrace();
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
                e.printStackTrace();
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in finishDecisionActivating ->   " + e.getMessage());
        }
    }

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
                e.printStackTrace();
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

    public void changeState(String state) {
        try {
            Command command = new Command("changeStateCallback", -1);
            command.addParameter(state);
            serverSerializer.command_FromServerToClient(command);
            serverRMI.command_FromServerToClient(command);
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in changeState ->   " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
