package it.polimi.ingsw.is25am49.server.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.server.model.adventurecards.*;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectMeteoriti;
import it.polimi.ingsw.is25am49.server.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.exceptions.OutOfDeckExceptions;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import javafx.scene.effect.Effect;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameBoard {
    private LeaderBoard leaderBoard;

    private AdventureCardDeck mainAdventureDeck;
    private final TileCardDeck tilesDeck;
    private final ConcurrentHashMap<Integer, AdventureCardDeck> mapSmallAdventureDecks;
    private AdventureCard currentCard;
    private Player currentPlayer;
    private FlightMode flightMode;
    private boolean isFirstCard;
    private final ArrayList<Player> landedPlayers;


    public GameBoard() {
        mapSmallAdventureDecks = new ConcurrentHashMap<>();
        // Nel costruttore è presente la generate() e lo shuffle() integrati.
        tilesDeck = new TileCardDeck();
        mainAdventureDeck = new AdventureCardDeck();
        flightMode = FlightMode.DEFAULT;
        isFirstCard = true;
        landedPlayers = new ArrayList<>();
    }

    private void isYourTurn(Player player) {
        if (!player.equals(currentPlayer))
            throw new IllegalMethodOnThisStateException("It's not your turn");
    }

    public boolean isTestFlight() {
        return flightMode == FlightMode.TEST;
    }

    //TODO da fare
    //Bisogna modificare la lunghezza del percorso, in base a flightMode e gameMode
    //Andrà anche a inizializzare la Shipboard della dimensione giusta in base alla GameMode, ( serve la lista di player)
    public void configurateFlight(GameMode gameMode, ArrayList<Player> players) {
        int level;
        if (gameMode == GameMode.LEVEL_1)
            level = 1;
        else if (gameMode == GameMode.LEVEL_2)
            level = 2;
        else
            throw new GameSettingsException("Illegal gameMode");
        players.stream().forEach(player ->
                player.setShipBoard(new ShipBoard(level, player.getPlayerNumber()))
        );
        setLeaderBoard(gameMode);
    }

    public void initialize(GameMode gameMode) {
        generateSmallDecks(gameMode);
        mainAdventureDeck.shuffle();
    }

    public AdventureCardDeck getMainAdventureDeck() {
        return mainAdventureDeck;
    }

    public void generateSmallDecks(GameMode gameMode) {
        //TODO se il livello della carta coincide con quello della gamemode la aggiungo, altrimenti null
        List<AdventureCard> deckFlightMode = new ArrayList<>();
        List<AdventureCard> deckLevel1 = new ArrayList<>();
        List<AdventureCard> deckLevel2 = new ArrayList<>();
        try {
            File file = new File("src/main/resources/it/polimi/ingsw/is25am49/adventure_card_deck_config.json");
            if (!file.exists()) {
                throw new FileNotFoundException("File non trovato");
            } else {
                List<AdventureCard> adventureCards = new ArrayList<>();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(file);
                AdventureCard card;
                for (JsonNode node : rootNode) {
                    int level = node.get("level").asInt();
                    String description = node.get("description").asText();
                    //TODO rifare il check
                    card = switch (description) {
                        case "SpazioAperto" -> new SpazioAperto(this, description, level);
                        case "StarDustCard" -> new StarDustCard(this, description, level);
                        case "PlanetCard"->{
                            int daysLost = node.get("daysLost").asInt();
                            List<Planet> planets = new ArrayList<>();
                            JsonNode planetsNode = node.get("planets");
                            if (planetsNode != null && planetsNode.isArray()) {
                                for (JsonNode planetNode : planetsNode) {
                                    int redGoods = planetNode.get("redGoods").asInt();
                                    int yellowGoods = planetNode.get("yellowGoods").asInt();
                                    int greenGoods = planetNode.get("greenGoods").asInt();
                                    int blueGoods = planetNode.get("blueGoods").asInt();
                                    Planet planet = new Planet(redGoods, yellowGoods, greenGoods, blueGoods);
                                    planets.add(planet);
                                }
                            }
                            yield new PlanetCard(this, description, level, daysLost, planets);
                        }
                        case "AbandonedShipCard" -> {
                            int daysLost = node.get("daysLost").asInt();
                            int creditsEarned = node.get("creditsEarned").asInt();
                            int lostAstronauts = node.get("lostAstronauts").asInt();
                            yield new AbandonedShipCard(this, description, level,daysLost,creditsEarned,lostAstronauts);
                        }
                        case "AbandonedStationCard"->{
                            int daysLost = node.get("daysLost").asInt();
                            int minimumAstronauts = node.get("minimumAstronauts").asInt();
                            int redGoods = node.get("redGoods").asInt();
                            int yellowGoods = node.get("yellowGoods").asInt();
                            int greenGoods = node.get("greenGoods").asInt();
                            int blueGoods = node.get("blueGoods").asInt();
                            yield new AbandonedStationCard(this, description,level, daysLost,minimumAstronauts,redGoods,yellowGoods,greenGoods,blueGoods);
                        }
                        case "SabotageCard" -> new SabotageCard(this, description, level);
                        case "WarZoneCard"->{
                            int daysLost = node.get("daysLost").asInt();
                            int loseAstronauts = node.get("lostAstronauts").asInt();
                            JsonNode effectsNode = node.get("effects");
                            List<CardEffect> effects = new ArrayList<>();
                            for(JsonNode effectNode : effectsNode) {
                                String name = effectNode.get("name").asText();
                                int power = effectNode.get("level").asInt();
                                Orientation orientation = Orientation.valueOf(effectNode.get("orientation").asText());
                                effects.add(new EffectCannonata(name, power, orientation));
                            }
                            yield new WarZoneCard(this, description, level, daysLost, loseAstronauts,effects);

                        }
                        case "SmugglerCard"->{
                            int daysLost = node.get("daysLost").asInt();
                            int minimumFirePower = node.get("minimumFirepower").asInt();
                            int redGoods = node.get("redGoods").asInt();
                            int yellowGoods = node.get("yellowGoods").asInt();
                            int greenGoods = node.get("greenGoods").asInt();
                            int blueGoods = node.get("blueGoods").asInt();
                            int lostGoods = node.get("number").asInt();
                            yield new SmugglerCard(this, description,level, daysLost,minimumFirePower,redGoods,yellowGoods,greenGoods,blueGoods,lostGoods);

                        }
                        case "MeteorRainCard"->{
                            JsonNode effectsNode = node.get("effects");
                            List<CardEffect> effects = new ArrayList<>();
                            for(JsonNode effect : effectsNode) {
                                String name = effect.get("name").asText();
                                int power = effect.get("level").asInt();
                                Orientation orientation = Orientation.valueOf(effect.get("orientation").asText());
                                effects.add(new EffectMeteoriti(name, power, orientation));
                            }
                            yield new MeteorRainCard(this, description, level, effects);

                        }
                        case "PiratesCard" -> {
                            int daysLost = node.get("daysLost").asInt();
                            int minimumFirePower = node.get("minimumFirepower").asInt();
                            int creditsEarned = node.get("creditsEarned").asInt();
                            List<CardEffect> effects = new ArrayList<>();
                            JsonNode effectsNode = node.get("effects");
                            for (JsonNode effectNode : effectsNode) {
                                String name = effectNode.get("name").asText();
                                int power = effectNode.get("level").asInt();
                                Orientation orientation = Orientation.valueOf(effectNode.get("orientation").asText());
                                effects.add(new EffectCannonata(name, power, orientation));
                            }
                            yield new PiratesCard(this, description, level, daysLost, minimumFirePower, creditsEarned, effects);
                        }
                        case "SlaverCard"->{
                            int daysLost = node.get("daysLost").asInt();
                            int minimumFirePower = node.get("minimumFirepower").asInt();
                            int creditsEarned = node.get("creditsEarned").asInt();
                            int lostAstronauts = node.get("lostAstronauts").asInt();
                            yield new SlaverCard(this,description,level,daysLost,minimumFirePower,creditsEarned,lostAstronauts);
                        }
                        case "EpidemyCard"->new EpidemyCard(this,description,level);
                        default -> null;
                    };

                    if (card != null) {
                        if (level == 0) {
                            deckFlightMode.add(card);
                            deckLevel1.add(card);
                        }
                        if (level == 1) {
                            deckLevel1.add(card);
                        }
                        if (level == 2) {
                            deckLevel2.add(card);
                        }

                    }
                    }
                if(gameMode==GameMode.FLIGHT_MODE){
                    deckFlightMode.forEach(adventureCard -> {
                        mainAdventureDeck.add(adventureCard);
                    });}
                if(gameMode==GameMode.LEVEL_1){
                    // Creo 4 mazzi separati, ognuno con 2 carte di livello 1
                    // Mescolo per distribuire casualmente
                    for(int i=0; i<4; i++) {
                        Collections.shuffle(deckLevel1);
                        AdventureCardDeck smallDeck = new AdventureCardDeck();
                        smallDeck.add(deckLevel1.remove(i));
                        smallDeck.add(deckLevel1.remove(i));
                        mapSmallAdventureDecks.put(i, smallDeck);
                    }
                }
                if(gameMode==GameMode.LEVEL_2){
                    //creo 4 mazzi separati, ognuno con 2 carte di livello 2 e una di livello 1
                    //mescolo per distribuire casualmente
                    for(int i=0;i<4;i++){
                        Collections.shuffle(deckLevel2);
                        Collections.shuffle(deckLevel1);
                        AdventureCardDeck smallDeck = new AdventureCardDeck();
                        smallDeck.add(deckLevel2.remove(i));
                        smallDeck.add(deckLevel2.remove(i));
                        smallDeck.add(deckLevel1.remove(i));
                        mapSmallAdventureDecks.put(i, smallDeck);
                    }
                }



            }
        } catch (FileNotFoundException e) {
            System.err.println("Errore: Il file di configurazione non è stato trovato");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




    public void setLeaderBoard(GameMode gameMode) {
        leaderBoard = new LeaderBoard(gameMode);
    }

    public boolean checkStatus() {
        return !flightMode.equals(FlightMode.DEFAULT);
    }

    public void setFlightMode(FlightMode flightMode) {
        this.flightMode = flightMode;
    }

    public AdventureCardDeck getSmallAdventureDeck(Integer index) {
        return mapSmallAdventureDecks.get(index);
    }

    public void mergeDecks() {
        if(!mapSmallAdventureDecks.isEmpty()) {
            AdventureCardDeck bufferDeck = new AdventureCardDeck();
            mapSmallAdventureDecks.forEach((index, smallDeck) -> smallDeck.stream().forEach(bufferDeck::add));
            mapSmallAdventureDecks.clear();
            setMainAdventureDeck(bufferDeck);
        }
        mainAdventureDeck.shuffle();
    }

    public LeaderBoard getLeaderBoard() {
        return leaderBoard;
    }

    public void setCurrentCard(AdventureCard card) {
        currentCard = card;
    }

    public AdventureCard getCurrentCard() {
        return currentCard;
    }

    //todo serve solo per i test poi si può rimuovere
    public ArrayList<Player> getLandedPlayers() {
        return landedPlayers;
    }

    public void drawAdventureCard(Player player) {
        if (player.equals(leaderBoard.getLeader())) {
            if (isFirstCard || currentCard.getIsFinished()) {
                currentCard = mainAdventureDeck.getFirstCard();
                mainAdventureDeck.draw();
                isFirstCard = false;
            } else {
                throw new IllegalMethodOnThisStateException("You can't draw adventure card until it is finished.");
            }
        } else
            throw new GameSettingsException("You are not the leader of this turn");
    }

    public void playerNoMoreInFlight(Player player) {
        if (landedPlayers.contains(player))
            throw new GameSettingsException("This player is already landed");
        else {
            landedPlayers.add(player);
            leaderBoard.remove(player);
        }
    }

    public void userThrowDices(Player player) {
        isYourTurn(player);
        currentCard.rollDices();
    }

    public void userPlayAdventureCard(Player player) {
    }

    public void userFinishDecision(Player player) {
    }

    public void userActivateComponent(Player player, Point coordinates) {
    }

    public void userActivateDaysUpdate() {
    }

    public void userLandOnPlanet(Player player) {
    }

    public void userLandOnShip(Player player) {
    }

    public void userQuitGame(Player player) {
    }

    public void addPlayerToLeaderboard(Player player) {
        if (leaderBoard.isEmpty()) {
            leaderBoard.add(player);
            currentPlayer = player;
        }
        else if(!leaderBoard.contains(player))
            leaderBoard.add(player);
    }


    public void addDiscardedTile(Player player) {
        Optional<Tile> newTile = player.discardTile();
        newTile.ifPresent(tile -> tilesDeck.addDiscardTile(tile, player));
    }

    public Tile drawTile(int index, Player player) {
        return tilesDeck.draw(index, player);
    }

    public Player getLeader() {
        return leaderBoard.getLeader();
    }

    public void calculateFinalCredits() {

        leaderBoard.stream().forEach(entry -> {
            int credits = 0;
            Player p = entry.getKey();
            credits = 5 - leaderBoard.getPos(p);
            credits -= p.getLostComponents();
            credits += p.scoreOfGoods();
            if (minimumExposedComponents().contains(p)) {
                credits += 2;
            }

            p.addCredits(credits);

        });

        landedPlayers.forEach(player -> {
            int credits = 0;

            credits = 5 - leaderBoard.getPos(player);
            credits -= player.getLostComponents();
            credits += player.scoreOfGoods();
            if (minimumExposedComponents().contains(player)) {
                credits += 2;
            }
            player.addCredits(credits);
        });
    }

    public void setFlightModeToDefault() {
        flightMode = FlightMode.DEFAULT;
    }

    public List<Player> minimumExposedComponents() {
        int minValue = leaderBoard.
                stream()
                .map(Map.Entry::getKey)//ritorno il player
                //ritorno solo il numero di connettori esposti dato il player
                .mapToInt(p -> p.getShipBoard().getNumberOfExposedConnectors())
                .min()
                .orElse(0);
        //System.out.println("the minimum value of exposed compopnents is " + minValue);
        return leaderBoard
                .stream()
                .map(Map.Entry::getKey) //ritorna il player
                //cerco se il player ha il minimo valore di conncetors, se si lo filtro
                .filter(p -> p.getShipBoard().getNumberOfExposedConnectors() == minValue)
                .collect(Collectors.toList());


    }

    private Player getNextPlayer() {
        int currentPos = leaderBoard.getPos(currentPlayer);
        if (currentPos == leaderBoard.size())
            throw new IllegalSetOfNextPlayerException("This is the last player");
        else
            return leaderBoard.getPlayerInPosition(currentPos + 1);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void nextPlayer() {
        //setto il currentplayer al next player dato il current player
        setCurrentPlayer(getNextPlayer());
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }


    public void finishUpdateLeaderboard() {
        updateLeaderboard();
        checkIfIsLapped();
    }

    public void updateLeaderboard() {

        //faccio modify flight days con i daysthisturn del player partendo AL CONTRARIO
        leaderBoard.stream()
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing(player -> -leaderBoard.getPos(player))) // ordine decrescente per posizione
                .forEach(player ->
                        leaderBoard.modifyFlightDays(player, player.getDaysThisTurn())
                );
        //alla fine setto tutti i days this turn a 0
        leaderBoard.stream()
                .map(Map.Entry::getKey)
                .forEach(player ->
                        player.setDaysThisTurn(0)
                );
    }


    public void checkIfIsLapped() {
        List<Player> lappedPlayers = new ArrayList<>();
        //per ogni player controllo se è stato doppiato, se si lo aggiungo alla lista
        leaderBoard.stream()
                .map(Map.Entry::getKey)
                .forEach(player -> {
                    //System.out.println("Player " + player.getUsername() + " has " + leaderBoard.getDays(player) + " days");
                    if (leaderBoard.getDays(player) + leaderBoard.getNumberOfCells() < leaderBoard.getDays(leaderBoard.getLeader())) {
                        // System.out.println("Player " + player.getUsername() + " is lapped");
                        lappedPlayers.add(player);
                    }
                });
        //per ogni player della lista lappedPlayers, lo rimuovo dalla leaderboard e lo aggiungo a landedPlayers
        lappedPlayers.forEach(this::playerNoMoreInFlight);
    }

    public void setMainAdventureDeck(AdventureCardDeck mainAdventureDeck) {
        this.mainAdventureDeck = mainAdventureDeck;
    }

    public int userFinishShipboard(Player player) {
        player.completeShipboard();
        addPlayerToLeaderboard(player);
        return leaderBoard.size();
    }

    public void userLookSmallAdventureCardDeck(Player player, int index) {
        if (mapSmallAdventureDecks.containsKey(index)) {
            if (!mapSmallAdventureDecks.get(index).isWatched() && !player.isWatching()) {
                mapSmallAdventureDecks.get(index).watch();
                player.lockLastTile();
                player.setSmallDeck(mapSmallAdventureDecks.get(index));
            } else
                throw new GameSettingsException("You cannot watch this deck");
        } else
            throw new OutOfDeckExceptions("This deck does not exist");
    }

    public void userStopLookSmallAdventureCardDeck(Player player, int index) {
        if (mapSmallAdventureDecks.containsKey(index)) {
            if (player.isWatching() && mapSmallAdventureDecks.get(index).equals(player.getSmallAdventureDeck())) {
                player.clearSmallDeck();
                mapSmallAdventureDecks.get(index).stopWatch();
            } else
                throw new GameSettingsException("You are not watching this deck");
        } else
            throw new OutOfDeckExceptions("This deck does not exist");
    }

    public void checkIfSomeoneHasZeroAstronauts() {
        List<Player> playersWithZeroAstronauts = new ArrayList<>();
        leaderBoard.stream()
                .map(Map.Entry::getKey)
                .forEach(player -> {
                    if (player.getShipBoard().getTotalNumberOfaToken(Token.ASTRONAUT) == 0)
                        playersWithZeroAstronauts.add(player);
                });
        playersWithZeroAstronauts.forEach(this::playerNoMoreInFlight);
    }
}
