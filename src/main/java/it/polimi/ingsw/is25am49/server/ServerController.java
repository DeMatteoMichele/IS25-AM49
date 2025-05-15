package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class ServerController {
    private final GameSession gameSession;
    private ServerSerializer serverSerializer;
    private final ArrayList<Integer> players;
    private ServerRMI serverRMI;
    private final ArrayBlockingQueue<Command> commandQueue;

    public ServerController(GameSession gameSession) {
        this.gameSession = gameSession;
        this.players = new ArrayList<>();
        commandQueue = new ArrayBlockingQueue<>(50000, true);
    }

    public void setSerializer(ServerSerializer serverSerializer) {
        this.serverSerializer = serverSerializer;
    }

    public void addPlayerToLobby(int playerId) {
        players.add(playerId);
    }

    public void registerClientRMI(RemoteClient client) {

    }

    public void adminSetup(int playerId, int numberOfPlayers, GameMode mode) {
        try {
            try {
                gameSession.getState().adminCreateGame(playerId, mode, numberOfPlayers);
                Command serObjectTemp = new Command("adminSetupCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(numberOfPlayers);
                serObjectTemp.addParameter(mode);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (GameSettingsException e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("AdminSetup hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server in adminSetup: " + e.getMessage());
        }
    }

    public void playerJoin(int playerId, String username, Color color) {
        addPlayerToLobby(playerId);
        try {
            try {
                gameSession.getState().userJoinLobby(color, username, playerId);
                Command serObjectTemp = new Command("playerJoinCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(username);
                serObjectTemp.addParameter(gameSession.getSeedForGeneration());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (GameSettingsException e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("PlayerJoin hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in playerJoin: " + e.getMessage());
        }

    }

    public void userDrawTile(int playerId, int tileId) {
        try {
            try {
                gameSession.getState().userDrawTile(playerId, tileId);
                Command serObjectTemp = new Command("userDrawTileCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(tileId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Draw tile hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.err.println("Error due to RemoteException in Server  in drawTile: " + e.getMessage());
        }

    }

    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) {
        try {
            try {
                gameSession.getState().userPlaceTile(playerId, coordinates);
                Command serObjectTemp = new Command("userPlaceTileCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(coordinates);
                serObjectTemp.addParameter(orientation);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Place tile hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.err.println("Error due to RemoteException in Server  in placeTile: " + e.getMessage());
        }
    }


    public void userPickTile(int playerId, Point coordinates) {
        try {
            try {
                gameSession.getState().userPickTile(playerId, coordinates);
                Command serObjectTemp = new Command("userPickTileCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(coordinates);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Pick tile hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in pickTile: " + e.getMessage());
        }
    }

    public void userDiscardTile(int playerId) {
        try {
            try {
                gameSession.getState().userDiscardTile(playerId);
                Command serObjectTemp = new Command("userDiscardTile", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Discard tile hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in discardTile: " + e.getMessage());
        }
    }

    public void userFinishShipboard(int playerId) {
        try {
            try {
                gameSession.getState().userFinishShipboard(playerId);
                Command serObjectTemp = new Command("userFinishShipboardCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Finish shipboard hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in finishShipboard: " + e.getMessage());
        }
    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        try {
            try {
                gameSession.getState().userLookSmallAdventureCardDeck(playerId, smallDeckIndex);
                Command serObjectTemp = new Command("userLookSmallAdventureCardDeckCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(smallDeckIndex);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Look small adventure card deck hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in lookSmallAdventureCardDeck: " + e.getMessage());
        }
    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        try {
            try {
                gameSession.getState().userStopLookSmallAdventureCardDeck(playerId, smallDeckIndex);
                Command serObjectTemp = new Command("userStopLookSmallAdventureCardDeckCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(smallDeckIndex);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Stop look small adventure card deck hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in stopLookSmallAdventureCardDeck: " + e.getMessage());
        }
    }

    public void userPlaceTimer(int playerId) {
        try {
            try {
                gameSession.getState().userPlaceTimer(playerId);
                Command serObjectTemp = new Command("userPlaceTimerCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Place timer hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in placeTimer: " + e.getMessage());
        }
    }

    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) {

        try {
            try {
                gameSession.getState().userSelectTypeOfToken(playerId, token, coordinates);
                Command serObjectTemp = new Command("userSelectTypeOfTokenCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(token);
                serObjectTemp.addParameter(coordinates);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Select type of token hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in selectTypeOfToken: " + e.getMessage());
        }
    }

    public void userThrowDices(int playerId) {
        try {
            try {
                gameSession.getState().userThrowDices(playerId);
                Command serObjectTemp = new Command("userThrowDicesCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Throw dices hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in throwDices: " + e.getMessage());
        }
    }

    public void userDrawAdventureCard(int playerId) {
        try {
            try {
                gameSession.getState().userDrawAdventureCard(playerId);
                Command serObjectTemp = new Command("userDrawAdventureCardCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Draw adventure card hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in drawAdventureCard: " + e.getMessage());
        }
    }

    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) {
        try {
            try {
                gameSession.getState().userFinishDecisionActivating(playerId, coordinates);
                Command serObjectTemp = new Command("userFinishDecisionActivatingCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(coordinates);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Finish decision activating hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in finishDecisionActivating: " + e.getMessage());
        }
    }

    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        try {
            try {
                gameSession.getState().userFinishDecisionToken(playerId, listOfTokens);
                Command serObjectTemp = new Command("userFinishDecisionTokenCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(listOfTokens);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Finish decision token hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in finishDecisionToken: " + e.getMessage());
        }
    }

    public void userAcceptPrizes(int playerId, boolean wantsRewards) {
        try {
            try {
                gameSession.getState().userAcceptPrizes(playerId, wantsRewards);
                Command serObjectTemp = new Command("userAcceptPrizesCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(wantsRewards);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Accept prizes hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in acceptPrizes: " + e.getMessage());
        }
    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        try {
            try {
                gameSession.getState().userLandOnPlanet(playerId, isLanded, planetId);
                Command serObjectTemp = new Command("userLandOnPlanetCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(isLanded);
                serObjectTemp.addParameter(planetId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Land on planet hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in landOnPlanet: " + e.getMessage());
        }
    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        try {
            try {
                gameSession.getState().userLandOnStationOrShip(playerId, isLanded);
                Command serObjectTemp = new Command("userLandOnStationOrShipCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(isLanded);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Land on station or ship hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in landOnStationOrShip: " + e.getMessage());
        }
    }

    public void userStopFlight(int playerId) {
        //????
    }

    //todo commentato, c'è da aggiungere userquitgame callback a commandclient
    public void userQuitGame(int playerId) {
        try {
            try {
                gameSession.getState().userQuitGame(playerId);
                Command serObjectTemp = new Command("userQuitGameCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Quit game hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in quitGame: " + e.getMessage());
        }
    }

    public void adminCalculateScore(int playerId) {
        try {
            try {
                gameSession.getState().adminCalculateScore(playerId);
                Command serObjectTemp = new Command("adminCalculateScoreCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Calculate score hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in calculateScore: " + e.getMessage());
        }
    }

    public void userEndGame(int playerId) {
        try {
            try {
                gameSession.getState().userEndGame(playerId);
                Command serObjectTemp = new Command("userEndGameCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                Command serObjectTemp = new Command("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("End game hasn't worked in Server: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in endGame: " + e.getMessage());
        }
    }

    public void setServerRMI(ServerRMI serverRMI) {
        this.serverRMI = serverRMI;
    }
}
