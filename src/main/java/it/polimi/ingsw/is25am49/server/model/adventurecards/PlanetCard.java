package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalPlanetToLand;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlanetCard extends AdventureCard {

    protected final List<Planet> planets=new ArrayList<Planet>();
    protected Planet planet;

    private final int daysLost;
    public PlanetCard(GameBoard gameBoard, String description, int level,String name,int daysLost, List<Planet> planets) {
        super(gameBoard,description, level,name);
        this.daysLost = daysLost;
        this.planets.addAll(planets);
    }



    @Override
    public void drawCard(){
        //chiamo il super draw
        super.drawCard();
        System.out.println("You have found a planet! You can land on it for some goods.");
        currentState = AdventureStatesTypes.CHECK_REQUIREMENTS;
        checkRequirements();
    }


    @Override
    public void checkRequirements() {
        if(currentState != AdventureStatesTypes.CHECK_REQUIREMENTS){
            throw new IllegalMethodOnThisStateException("Wrong state");
        }
        System.out.println("Do you wanna land on the planet for some goods?");
        currentState=AdventureStatesTypes.DECISION_LAND_PLANET;
    }

    @Override
    public void landingOnPlanet(boolean isLanded, int planetId){
        super.landingOnPlanet(isLanded, planetId);
        if(isLanded) {
            planet = getPlanetById(planetId);
            if(!planets.contains(planet)){
                throw new IllegalPlanetToLand("You cannot land on this planet, it doesnt exists or it is already occupied");
            }
            if(!planet.isOccupied()){
                System.out.println(gameBoard.getCurrentPlayer().getUsername() + " You have landed on the planet!");
                planet.setOccupied(true);
                planets.remove(planet);
                planet.setPlayernumber(gameBoard.getCurrentPlayer().getPlayerNumber());
                System.out.println("You have to choose the position of the goods you won:");
                currentState = AdventureStatesTypes.DECISION_GOODS;
                currentEffect = planet.getEffect();
            }
        }
        else {
            System.out.println("you have decided not to land on the planet");
            System.out.println("Now check on next player");
            checkNextPlayer();
        }
    }
    public Planet getPlanetById(int planetId){
        for(Planet planet : planets){
            if(planet.getPlanetId() == planetId){
                return planet;
            }
        }
        throw new IllegalPlanetToLand("You cannot land on this planet the id is not valid");
    }

    private void checkNextPlayer() {
        try{
            gameBoard.nextPlayer();
            System.out.println("it wasn't the last player, go on");
            currentState = AdventureStatesTypes.CHECK_REQUIREMENTS;
            checkRequirements();
        }
        catch (IllegalSetOfNextPlayerException e) {
            System.out.println("It was the last player, end of the turn");
            System.out.println("it goes to update state");
            update();
        }
    }

    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates){
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        checkCorrectnessOfTokensAndPoints(currentState,coordinates,shipBoard);
        if(currentState==AdventureStatesTypes.DECISION_GOODS){
            //se viene chiamato (dallo user) e sono in decisiongoods allora chiamo applyeffect dove inserirò le merci, cambio stato
            System.out.println("You have decided the position of the goods");
            currentEffect.setListOfCoordinates(coordinates);
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
            }
        //se non sono in decizion goods allora non posso chiamare finishdecision
        else{throw new IllegalMethodOnThisStateException("Wrong state");}
    }



    @Override
    public void applyEffect() {
        super.applyEffect();
        System.out.println("You are in apply effect of planet card");
        //applico l'effetto, aggiorno il player con i giorni persi
        currentEffect.action(gameBoard.getCurrentPlayer());
        gameBoard.getCurrentPlayer().setDaysThisTurn(-daysLost);
        gameBoard.updateLeaderboard();
        System.out.println("You have lost " + daysLost + " travel days");
        if(planets.isEmpty()){
            System.out.println("You have no more planets to land on, you have to go on with the game");
            update();
            return;
        }
        checkNextPlayer();

    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        final int width = 90;  // Larghezza standard del box
        String border = "+" + "-".repeat(width - 2) + "+\n";
        String separator = "|" + "-".repeat(width - 2) + "|\n";
        str.append(border);
        String nameText = "| NAME: " + name;
        str.append(nameText).append(" ".repeat(width - nameText.length() - 1)).append("|\n");
        str.append(separator);
        String descText = "| DESCRIPTION: " + description;
        appendTextWithWrapping(str, descText, width);
        String levelText = "| LEVEL: " + level;
        str.append(levelText).append(" ".repeat(width - levelText.length() - 1)).append("|\n");
        String daysLostText = "| DAYS LOST: " + daysLost;
        str.append(daysLostText).append(" ".repeat(width - daysLostText.length() - 1)).append("|\n");
        str.append(separator);
        for(Planet planet : planets){
            String planetText = "| PLANET: " + planet.getPlanetId();
            str.append(planetText).append(" ".repeat(width - planetText.length() - 1)).append("|\n");
            EffectWinGoods effect = (EffectWinGoods) planet.getEffect();
            String redGoods = "| RED GOODS: " + effect.getRedGoods();
            str.append(redGoods).append(" ".repeat(width - redGoods.length() - 1)).append("|\n");
            String yellowGoods = "| YELLOW GOODS: " + effect.getYellowGoods();
            str.append(yellowGoods).append(" ".repeat(width - yellowGoods.length() - 1)).append("|\n");
            String blueGoods = "| BLUE GOODS: " + effect.getBlueGoods();
            str.append(blueGoods).append(" ".repeat(width - blueGoods.length() - 1)).append("|\n");
            String greenGoods = "| GREEN GOODS: " + effect.getGreenGoods();
            str.append(greenGoods).append(" ".repeat(width - greenGoods.length() - 1)).append("|\n");
            str.append(border);
        }
        return str.toString();
    }








    
}
