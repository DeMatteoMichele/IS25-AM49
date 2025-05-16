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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {
    private final GameSession gameSession;
    private ServerSerializer serverSerializer;
    private final ArrayList<Integer> players;
    private ServerRMI serverRMI;
    private final ArrayBlockingQueue<Command> commandQueue;
    private final ExecutorService executors = Executors.newFixedThreadPool(10);

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
                Command command = new Command("adminSetupCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(numberOfPlayers);
                command.addParameter(mode);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (GameSettingsException e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("AdminSetup hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
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
                Command command = new Command("playerJoinCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(username);
                command.addParameter(gameSession.getSeedForGeneration());
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (GameSettingsException e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("PlayerJoin hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in playerJoin: " + e.getMessage());
        }

    }

    public void userDrawTile(int playerId, int tileId) {
        try {
            try {
                gameSession.getState().userDrawTile(playerId, tileId);
                Command command = new Command("userDrawTileCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(tileId);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Draw tile hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.err.println("Error due to RemoteException in Server  in drawTile: " + e.getMessage());
        }

    }

    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) {
        try {
            try {
                gameSession.getState().userPlaceTile(playerId, coordinates);
                Command command = new Command("userPlaceTileCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(coordinates);
                command.addParameter(orientation);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Place tile hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.err.println("Error due to RemoteException in Server  in placeTile: " + e.getMessage());
        }
    }


    public void userPickTile(int playerId, Point coordinates) {
        try {
            try {
                gameSession.getState().userPickTile(playerId, coordinates);
                Command command = new Command("userPickTileCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(coordinates);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Pick tile hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in pickTile: " + e.getMessage());
        }
    }

    public void userDiscardTile(int playerId) {
        try {
            try {
                gameSession.getState().userDiscardTile(playerId);
                Command command = new Command("userDiscardTile", this, playerId);
                command.addParameter(playerId);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Discard tile hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in discardTile: " + e.getMessage());
        }
    }

    public void userFinishShipboard(int playerId) {
        try {
            try {
                gameSession.getState().userFinishShipboard(playerId);
                Command command = new Command("userFinishShipboardCallback", this, playerId);
                command.addParameter(playerId);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Finish shipboard hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in finishShipboard: " + e.getMessage());
        }
    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        try {
            try {
                gameSession.getState().userLookSmallAdventureCardDeck(playerId, smallDeckIndex);
                Command command = new Command("userLookSmallAdventureCardDeckCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(smallDeckIndex);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Look small adventure card deck hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in lookSmallAdventureCardDeck: " + e.getMessage());
        }
    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        try {
            try {
                gameSession.getState().userStopLookSmallAdventureCardDeck(playerId, smallDeckIndex);
                Command command = new Command("userStopLookSmallAdventureCardDeckCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(smallDeckIndex);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Stop look small adventure card deck hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in stopLookSmallAdventureCardDeck: " + e.getMessage());
        }
    }

    public void userPlaceTimer(int playerId) {
        try {
            try {
                gameSession.getState().userPlaceTimer(playerId);
                Command command = new Command("userPlaceTimerCallback", this, playerId);
                command.addParameter(playerId);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Place timer hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in placeTimer: " + e.getMessage());
        }
    }

    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) {

        try {
            try {
                gameSession.getState().userSelectTypeOfToken(playerId, token, coordinates);
                Command command = new Command("userSelectTypeOfTokenCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(token);
                command.addParameter(coordinates);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Select type of token hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in selectTypeOfToken: " + e.getMessage());
        }
    }

    public void userThrowDices(int playerId) {
        try {
            try {
                gameSession.getState().userThrowDices(playerId);
                Command command = new Command("userThrowDicesCallback", this, playerId);
                command.addParameter(playerId);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Throw dices hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in throwDices: " + e.getMessage());
        }
    }

    public void userDrawAdventureCard(int playerId) {
        try {
            try {
                gameSession.getState().userDrawAdventureCard(playerId);
                Command command = new Command("userDrawAdventureCardCallback", this, playerId);
                command.addParameter(playerId);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Draw adventure card hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in drawAdventureCard: " + e.getMessage());
        }
    }

    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) {
        try {
            try {
                gameSession.getState().userFinishDecisionActivating(playerId, coordinates);
                Command command = new Command("userFinishDecisionActivatingCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(coordinates);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Finish decision activating hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in finishDecisionActivating: " + e.getMessage());
        }
    }

    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        try {
            try {
                gameSession.getState().userFinishDecisionToken(playerId, listOfTokens);
                Command command = new Command("userFinishDecisionTokenCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(listOfTokens);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Finish decision token hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in finishDecisionToken: " + e.getMessage());
        }
    }

    public void userAcceptPrizes(int playerId, boolean wantsRewards) {
        try {
            try {
                gameSession.getState().userAcceptPrizes(playerId, wantsRewards);
                Command command = new Command("userAcceptPrizesCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(wantsRewards);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Accept prizes hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in acceptPrizes: " + e.getMessage());
        }
    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        try {
            try {
                gameSession.getState().userLandOnPlanet(playerId, isLanded, planetId);
                Command command = new Command("userLandOnPlanetCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(isLanded);
                command.addParameter(planetId);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Land on planet hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in landOnPlanet: " + e.getMessage());
        }
    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        try {
            try {
                gameSession.getState().userLandOnStationOrShip(playerId, isLanded);
                Command command = new Command("userLandOnStationOrShipCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter(isLanded);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Land on station or ship hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
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
                Command command = new Command("userQuitGameCallback", this, playerId);
                command.addParameter(playerId);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Quit game hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in quitGame: " + e.getMessage());
        }
    }

    public void adminCalculateScore(int playerId) {
        try {
            try {
                gameSession.getState().adminCalculateScore(playerId);
                Command command = new Command("adminCalculateScoreCallback", this, playerId);
                command.addParameter(playerId);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("Calculate score hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in calculateScore: " + e.getMessage());
        }
    }

    public void userEndGame(int playerId) {
        try {
            try {
                gameSession.getState().userEndGame(playerId);
                Command command = new Command("userEndGameCallback", this, playerId);
                command.addParameter(playerId);
                commandQueue.add(command);
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            } catch (Exception e) {
                Command command = new Command("errorCallback", this, playerId);
                command.addParameter(playerId);
                command.addParameter("End game hasn't worked in Server: " + e.getMessage());
                serverSerializer.command_FromServerToClient(command);
                serverRMI.command_FromServerToClient(command);
            }
        } catch (RemoteException e) {
            System.out.println("Error due to RemoteException in Server  in endGame: " + e.getMessage());
        }
    }

    public void setServerRMI(ServerRMI serverRMI) {
        this.serverRMI = serverRMI;
    }

    public void giveMeCommandsForReconnection(int clientId) {
        executors.submit(() -> {
            while (!commandQueue.isEmpty()) {
                try {
                    serverSerializer.command_FromServerToSpecificClient(commandQueue.poll(), clientId);
                    serverRMI.command_FromServerToSpecificClient(commandQueue.poll(), clientId);
                } catch (RemoteException e) {
                    System.out.println("Error due to RemoteException in Server in giveMeCommandsForReconnection: " + e.getMessage());
                }
            }
        });
    }
}
