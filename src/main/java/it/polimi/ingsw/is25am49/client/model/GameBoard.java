package it.polimi.ingsw.is25am49.client.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.client.model.adventurecards.*;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectMeteoriti;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Orientation;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.client.model.exceptions.OutOfDeckExceptions;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.client.model.shipboard.Tile;
import javafx.util.Pair;

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
    private boolean isFirstCard;
    private final ArrayList<Player> landedPlayers;
    private final ArrayList<Player> stopFlightPlayers;
    private int seedForGeneration;


    /**
     * *********EXISTS ONLY FOR NOT BREAKING SOME TESTS*********************
     * Secondary Constructor of the GameBoard class
     */
    public GameBoard() {
        mapSmallAdventureDecks = new ConcurrentHashMap<>();
        // Nel costruttore è presente la generate() e lo shuffle() integrati.
        tilesDeck = new TileCardDeck(42); //TODO HARDCODATO IL SEED PER NON ROMPERE NULLA, DA NON USARE QUESTO COSTRUTTORE PER CLASSI FUTURE
        mainAdventureDeck = new AdventureCardDeck();
        isFirstCard = true;
        landedPlayers = new ArrayList<>();
        stopFlightPlayers = new ArrayList<>();
    }

    /**
     * New main constructor of the GameBoard class, uses a seed for the generation od dices, decks and tiles
     */
    public GameBoard(int seedForGeneration) {
        this.seedForGeneration = seedForGeneration;
        mapSmallAdventureDecks = new ConcurrentHashMap<>();
        // Nel costruttore è presente la generate() e lo shuffle() integrati.
        tilesDeck = new TileCardDeck(seedForGeneration);
        mainAdventureDeck = new AdventureCardDeck();
        isFirstCard = true;
        landedPlayers = new ArrayList<>();
        stopFlightPlayers = new ArrayList<>();
    }

    /**
     * This method is used to check if the player is the current player
     * @param player the player to check
     * @throws IllegalMethodOnThisStateException if the player is not the current player
     */
    private void isYourTurn(Player player) {
        if (!player.equals(currentPlayer))
            throw new IllegalMethodOnThisStateException("It's not your turn");
    }


    /**
     * This method is used to configurate the flight of the game
     * @param gameMode the game mode
     * @param players the players of the game
     */
    public void configurateFlight(GameMode gameMode, ArrayList<Player> players) {
        int level=switch (gameMode) {
            case FLIGHT_MODE -> 0;
            case LEVEL_1 -> 1;
            case LEVEL_2 -> 2;
            default -> throw new GameSettingsException("Illegal gameMode");
        };
        players.stream().forEach(player ->
                player.setShipBoard(new ShipBoard(level, player.getPlayerNumber(),player.getColor()))
        );
                setLeaderBoard(gameMode);
    }

    /**
     * This method is used to initialize the game
     * @param gameMode the game mode
     */
    public void initialize(GameMode gameMode) {
        generateSmallDecks(gameMode);
        //mainAdventureDeck.shuffle(); la shuffle viene fatta nel mergedecks
    }

    /**
     * This method is used to get the main adventure deck
     * @return the main adventure deck
     */
    public AdventureCardDeck getMainAdventureDeck() {
        return mainAdventureDeck;
    }

    /**
     * This method is used to generate the small decks
     * If the game mode is FLIGHT_MODE, it generates a main deck with all the cards
     * @param gameMode the game mode
     * @throws RuntimeException if the file is not found
     */
    public void generateSmallDecks(GameMode gameMode) {
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
                    String name = node.get("name").asText();
                    String description = node.get("description").asText();
                    card = switch (name) {
                        case "SpazioAperto" -> new SpazioAperto(this, description, level,name);
                        case "StarDustCard" -> new StarDustCard(this, description, level,name);
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
                                    int planetId = planetNode.get("planetId").asInt();
                                    Planet planet = new Planet(redGoods, yellowGoods, greenGoods, blueGoods,planetId);
                                    planets.add(planet);
                                }
                            }
                            yield new PlanetCard(this, description, level,name, daysLost, planets);
                        }
                        case "AbandonedShipCard" -> {
                            int daysLost = node.get("daysLost").asInt();
                            int creditsEarned = node.get("creditsEarned").asInt();
                            int lostAstronauts = node.get("lostAstronauts").asInt();
                            yield new AbandonedShipCard(this, description, level,name,daysLost,creditsEarned,lostAstronauts);
                        }
                        case "AbandonedStationCard"->{
                            int daysLost = node.get("daysLost").asInt();
                            int minimumAstronauts = node.get("minimumAstronauts").asInt();
                            int redGoods = node.get("redGoods").asInt();
                            int yellowGoods = node.get("yellowGoods").asInt();
                            int greenGoods = node.get("greenGoods").asInt();
                            int blueGoods = node.get("blueGoods").asInt();
                            yield new AbandonedStationCard(this, description,level, name,daysLost,minimumAstronauts,redGoods,yellowGoods,greenGoods,blueGoods);
                        }
                        case "SabotageCard" -> new SabotageCard(this, description, level,name);
                        case "WarZoneCard"->{
                            int daysLost = node.get("daysLost").asInt();
                            int loseAstronauts = node.get("lostAstronauts").asInt();
                            JsonNode effectsNode = node.get("effects");
                            List<CardEffect> effects = new ArrayList<>();
                            for(JsonNode effectNode : effectsNode) {
                                String name2 = effectNode.get("name").asText();
                                int power = effectNode.get("level").asInt();
                                Orientation orientation = Orientation.valueOf(effectNode.get("orientation").asText());
                                effects.add(new EffectCannonata(name2, power, orientation));
                            }
                            yield new WarZoneCard(this, description, level,name, daysLost, loseAstronauts,effects);

                        }
                        case "SmugglerCard"->{
                            int daysLost = node.get("daysLost").asInt();
                            int minimumFirePower = node.get("minimumFirepower").asInt();
                            int redGoods = node.get("redGoods").asInt();
                            int yellowGoods = node.get("yellowGoods").asInt();
                            int greenGoods = node.get("greenGoods").asInt();
                            int blueGoods = node.get("blueGoods").asInt();
                            int lostGoods = node.get("number").asInt();
                            yield new SmugglerCard(this, description,level,name, daysLost,minimumFirePower,redGoods,yellowGoods,greenGoods,blueGoods,lostGoods);

                        }
                        case "MeteorRainCard"->{
                            JsonNode effectsNode = node.get("effects");
                            List<CardEffect> effects = new ArrayList<>();
                            for(JsonNode effect : effectsNode) {
                                String name2 = effect.get("name").asText();
                                int power = effect.get("level").asInt();
                                Orientation orientation = Orientation.valueOf(effect.get("orientation").asText());
                                effects.add(new EffectMeteoriti(name2, power, orientation));
                            }
                            yield new MeteorRainCard(this, description, level,name, effects);

                        }
                        case "PiratesCard" -> {
                            int daysLost = node.get("daysLost").asInt();
                            int minimumFirePower = node.get("minimumFirepower").asInt();
                            int creditsEarned = node.get("creditsEarned").asInt();
                            List<CardEffect> effects = new ArrayList<>();
                            JsonNode effectsNode = node.get("effects");
                            for (JsonNode effectNode : effectsNode) {
                                String name2 = effectNode.get("name").asText();
                                int power = effectNode.get("level").asInt();
                                Orientation orientation = Orientation.valueOf(effectNode.get("orientation").asText());
                                effects.add(new EffectCannonata(name2, power, orientation));
                            }
                            yield new PiratesCard(this, description, level, name,daysLost, minimumFirePower, creditsEarned, effects);
                        }
                        case "SlaverCard"->{
                            int daysLost = node.get("daysLost").asInt();
                            int minimumFirePower = node.get("minimumFirepower").asInt();
                            int creditsEarned = node.get("creditsEarned").asInt();
                            int lostAstronauts = node.get("lostAstronauts").asInt();
                            yield new SlaverCard(this,description,level,name,daysLost,minimumFirePower,creditsEarned,lostAstronauts);
                        }
                        case "EpidemyCard"->new EpidemyCard(this,description,level,name);
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
                if (gameMode == GameMode.FLIGHT_MODE) {
                    deckFlightMode.forEach(adventureCard -> {
                        mainAdventureDeck.add(adventureCard);
                    });
                }
                if (gameMode == GameMode.LEVEL_1) {
                    // Creo 4 mazzi separati, ognuno con 2 carte di livello 1
                    for (int i = 0; i < 4; i++) {
                        Collections.shuffle(deckLevel1,new Random(seedForGeneration));
                        AdventureCardDeck smallDeck = new AdventureCardDeck();
                        smallDeck.add(deckLevel1.remove(i));
                        smallDeck.add(deckLevel1.remove(i));
                        mapSmallAdventureDecks.put(i, smallDeck);
                    }
                }
                if (gameMode == GameMode.LEVEL_2) {
                    //creo 4 mazzi separati, ognuno con 2 carte di livello 2 e una di livello 1
                    for (int i = 0; i < 4; i++) {
                        Collections.shuffle(deckLevel2,new Random(seedForGeneration));
                        Collections.shuffle(deckLevel1,new Random(seedForGeneration));
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


    /**
     * This method is used to set the leaderboard
     * @param gameMode the game mode
     */
    public void setLeaderBoard(GameMode gameMode) {
        leaderBoard = new LeaderBoard(gameMode);
    }

    /**
     * This method is used to get the small adventure deck
     * @param index
     * @return AdventureCardDeck the small adventure deck
     */
    public AdventureCardDeck getSmallAdventureDeck(Integer index) {
        return mapSmallAdventureDecks.get(index);
    }

    /**
     * This method is used to merge the small adventure decks into the main adventure deck
     * Then it shuffles the main adventure deck
     */
    public void mergeDecks() {
        if (!mapSmallAdventureDecks.isEmpty()) {
            AdventureCardDeck bufferDeck = new AdventureCardDeck();
            mapSmallAdventureDecks.forEach((index, smallDeck) -> smallDeck.stream().forEach(bufferDeck::add));
            mapSmallAdventureDecks.clear();
            setMainAdventureDeck(bufferDeck);
        }
        mainAdventureDeck.shuffle(new Random(seedForGeneration));
    }

    /**
     * This method is used to get the leaderboard
     * @return the leaderboard
     */
    public LeaderBoard getLeaderBoard() {
        return leaderBoard;
    }


//    public void setCurrentCard(AdventureCard card) {
//        currentCard = card;
//    }

    /**
     * This method is used to get the current card
     * @return the current card (AdventureCard)
     */
    public AdventureCard getCurrentCard() {
        return currentCard;
    }

    //todo serve solo per i test poi si può rimuovere
    public ArrayList<Player> getLandedPlayers() {
        return landedPlayers;
    }

    /**
     * This method is used to remove a player from the leaderboard
     * and add it to the landed players
     * It's called when a player choose or is forced to land
     * @param player the player to check
     * @throws GameSettingsException if the player is already landed
     */
    public void playerNoMoreInFlight(Player player) {
        if (landedPlayers.contains(player))
            throw new GameSettingsException("This player is already landed");
        else {
            landedPlayers.add(player);
            leaderBoard.remove(player);
        }
    }
    /**
     * This method is used to check if is possible to draw an adventure card
     * @param player the player who is drawing the card
     * @throws IllegalMethodOnThisStateException if the player is not the leader
     * @throws GameSettingsException if the card is not finished
     */
    public void checkDrawAdventureCard(Player player){
        if (player.equals(leaderBoard.getLeader())) {
            if (isFirstCard || currentCard.getIsFinished() || !mainAdventureDeck.getDeck().isEmpty()) {
                //OK
            } else {
                throw new IllegalMethodOnThisStateException("You can't draw adventure card until it is finished.");
            }
        } else
            throw new GameSettingsException("You are not the leader of this turn");
    }


    /**
     * This method is used to draw an adventure card
     * @param player the player who is drawing the card
     * @throws IllegalMethodOnThisStateException if the player is not the leader
     * @throws GameSettingsException if the card is not finished
     */
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

    /**
     * This method is used to roll the dices on the current card
     * @param player the player who roll the dices
     */
    public void userThrowDices(Player player) {
        isYourTurn(player);
        currentCard.rollDices();
    }

    public void userFinishDecisionActivating(Player player, Set<Point> coordinates) {
        isYourTurn(player);
        currentCard.finishDecision(coordinates);
    }

    public void userFinishDecisionToken(Player player, ArrayList<Pair<Point, Token>> listOfTokens) {
        isYourTurn(player);
        currentCard.finishDecision(listOfTokens);
    }

    public void userLandOnPlanet(Player player) {
        isYourTurn(player);
    }

    public void userLandOnStationOrShip(Player player) {
        isYourTurn(player);

    }

    public void userQuitGame(Player player) throws GameSettingsException {

    }

    public void userStopFlight(Player player) {
        if(currentCard.getIsFinished()){
            playerNoMoreInFlight(player);
        }
        else{
            stopFlightPlayers.add(player);
        }
    }
    public List<Player> getStopFlightPlayers() {
        return stopFlightPlayers;
    }

    /**
     * This method is used to add a player to the leaderboard
     * @param player the player to add
     */
    public void addPlayerToLeaderboard(Player player) {
        if (leaderBoard.isEmpty()) {
            leaderBoard.add(player);
            currentPlayer = player;
        } else if (!leaderBoard.contains(player))
            leaderBoard.add(player);
    }


    public void addDiscardedTile(Player player) {
        Optional<Tile> newTile = player.discardTile();
        newTile.ifPresent(tile -> tilesDeck.addDiscardTile(tile, player));
    }

    /**
     * This method is used to draw a tile from the tiles deck
     * @param index the index of the tile to draw
     * @param player the player who is drawing the tile
     * @return the tile drawn
     */
    public Tile drawTile(int index, Player player) {
        return tilesDeck.drawByTileId(index, player);
    }

    /**
     * This method is used to get the leader of the game
     * @return the player who is the leader
     */
    public Player getLeader() {
        return leaderBoard.getLeader();
    }

    /**
     * This method is used to calculate the final credits of the players
     * It is used at the end of the game
     * For each player, it calculates the credits based on the position in the leaderboard,
     * the number of lost components, the score of goods and if the player has the minimum exposed components
     * Then it stores the value in credits of the player
     */
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


    /**
     * This method is used to get the players with the minimum exposed components
     * First it calculates the minimum value of exposed components
     * Then it filters the players with the same value
     * @return the list of players with the minimum exposed components
     */
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

    /**
     * This method is used to get the next player in the leaderboard
     * @return the next player
     * @throws IllegalSetOfNextPlayerException if the current player is the last player
     */
    private Player getNextPlayer() {
        int currentPos = leaderBoard.getPos(currentPlayer);
        if (currentPos == leaderBoard.size())
            throw new IllegalSetOfNextPlayerException("This is the last player");
        else
            return leaderBoard.getPlayerInPosition(currentPos + 1);
    }

    /**
     * This method is used to get the current player in the game
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void nextPlayer() {
        //setto il currentplayer al next player dato il current player
        setCurrentPlayer(getNextPlayer());
    }

    /**
     * This method is used to set the current player
     * @param currentPlayer the current player
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * This method is used when we are in update in the card
     * It updates the leaderboard with the day lost or earned for each player
     * Then it checks if someone is lapped
     */
    public void finishUpdateLeaderboard() {
        updateLeaderboard();
        checkIfIsLapped();
    }

    /**
     * This method is used to update the leaderboard
     * It modifies the flight days of each player with the days of this turn
     * The days of this turn are based on the effect of the card
     * Then it sets the days this turn to 0 for each player
     * It starts from the last player to the first player
     */
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


    /**
     * This method is used to check if someone is lapped
     * For each player, it checks if they are lapped, if yes it adds them to the lappedPlayers list
     * Then it calls playerNoMoreInFlight that removes them from the leaderboard and adds them to the landedPlayers list
     */
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

    /**
     * This method is used to set the main adventure deck
     * @param mainAdventureDeck the main adventure deck
     */
    public void setMainAdventureDeck(AdventureCardDeck mainAdventureDeck) {
        this.mainAdventureDeck = mainAdventureDeck;
    }

    /**
     * This method is used when the player finish the shipboard
     * It adds the player to the leaderboard
     * @param player the player who finish the shipboard
     * @return the size of the leaderboard
     */
    public int userFinishShipboard(Player player) {
        player.completeShipboard();
        addPlayerToLeaderboard(player);
        return leaderBoard.size();
    }

    /**
     * This method is used to look at small adventure card deck
     * It's synchronized to avoid concurrent access
     * @param player the player who wanna look at the deck
     * @param index the index of the deck
     * @throws GameSettingsException if any player is already watching a deck
     * @throws OutOfDeckExceptions if the deck does not exist
     */
    public void userLookSmallAdventureCardDeck(Player player, int index) {
        synchronized (mapSmallAdventureDecks.get(index)) {
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
    }

    /**
     * This method is used to stop looking at small adventure card deck
     * @param player the player who is stopping to look at the deck
     * @param index the index of the deck
     * @throws OutOfDeckExceptions if the deck does not exist
     */
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

    /**
     * This method is used to check if someone has zero astronauts
     * If yes it adds them to the playersWithZeroAstronauts list
     * Then it calls playerNoMoreInFlight that removes them from the leaderboard and adds them to the landedPlayers list
     */
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

    public void setSeedForGeneration(int seed) {
        this.seedForGeneration = seed;
    }

    public long getSeedForGeneration() {
        return seedForGeneration;
    }


    public TileCardDeck getTilesDeck(){
        return tilesDeck;
    }

    public String smallDeckToString(int index) {
        return mapSmallAdventureDecks.get(index).toString();
    }
}
