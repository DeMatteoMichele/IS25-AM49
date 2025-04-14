package it.polimi.ingsw.is25am49.server.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;

import it. polimi. ingsw. is25am49.server. model. shipboard. Component;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisCardException;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class EffectWinGoods extends CardEffect {
    private int redGoods;
    private int yellowGoods;
    private int greenGoods;
    private int blueGoods;
    private List<Pair<Point, Token>> coordinates;

    public EffectWinGoods(String name_effect, int redGoods, int yellowGoods, int greenGoods, int blueGoods) {
        super(name_effect);
        this.redGoods = redGoods;
        this.yellowGoods = yellowGoods;
        this.greenGoods = greenGoods;
        this.blueGoods = blueGoods;
    }
    public void action(Player player) {
        if(coordinates == null || coordinates.isEmpty()) {
            throw new IllegalMethodOnThisCardException("You have to set the coordinates before using this method");
        }
        for(Pair<Point, Token> coordinate : coordinates) {
            Component component = player.getShipBoard().getTile(coordinate.getKey()).getComponent();
            if(component.getLevel()==component.getTokenList().size()) {
                //se è pieno rimuovo il token di minore valore per inserire quello di maggior valore
                Token tokenToRemove = minValueToken(component.getTokenList());
                player.getShipBoard().removeTokenFromTile(coordinate.getKey(), tokenToRemove);
                System.out.println("rimozione di "+ tokenToRemove + " dalla posizione "+ coordinate.getKey());
                if(coordinate.getValue().equals(Token.RED_GOODS) && !player.getShipBoard().getTile(coordinate.getKey()).getComponentType().equals(ComponentType.SPECIAL_STORAGE)) {
                    throw new IllegalMethodOnThisCardException("you can't add a red goods in a normal storage");
                }
                addTokenToTile(coordinate.getKey(),coordinate.getValue(),player);
            }
            else{
                if(coordinate.getValue().equals(Token.RED_GOODS) && !player.getShipBoard().getTile(coordinate.getKey()).getComponentType().equals(ComponentType.SPECIAL_STORAGE)) {
                    throw new IllegalMethodOnThisCardException("you can't add a red goods in a normal storage");
                }

                addTokenToTile(coordinate.getKey(),coordinate.getValue(),player);
            }

    }}

private void addTokenToTile(Point coordinate, Token token, Player player) {
    boolean canAdd = false;

    switch (token) {
        case RED_GOODS:
            canAdd = redGoods > 0;
            if (canAdd) redGoods--;
            break;
        case YELLOW_GOODS:
            canAdd = yellowGoods > 0;
            if (canAdd) yellowGoods--;
            break;
        case GREEN_GOODS:
            canAdd = greenGoods > 0;
            if (canAdd) greenGoods--;
            break;
        case BLUE_GOODS:
            canAdd = blueGoods > 0;
            if (canAdd) blueGoods--;
            break;
    }

    if (canAdd) {
        player.getShipBoard().addTokenToTile(coordinate, token);
        System.out.println("aggiunta di " + token + " dalla posizione " + coordinate);
    } else {
        System.out.println("you are trying to add a token that the card didn't give you or you have already added it the maximum number of times");
    }

}
    private Token minValueToken(List<Token> tokens) {
        Token minValueToken = tokens.getFirst();
        for (Token token : tokens) {
            // Controllo se il token corrente ha un valore minore rispetto al minimo trovato finora
            if (getTokenValue(token) < getTokenValue(minValueToken)) {
                minValueToken = token;
            }
        }
        return minValueToken;
    }

    private int getTokenValue(Token token) {
        return switch (token) {
            case RED_GOODS -> 4;
            case YELLOW_GOODS -> 3;
            case GREEN_GOODS -> 2;
            case BLUE_GOODS -> 1;
            default -> 0;
        };
    }

    public void setPosition(int position) {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }

    public void setListOfCoordinates(List<Pair<Point, Token>> positions) {
        this.coordinates = positions;
    }

    @Override
    public void setCoordinate(Point coordinate) {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }
}
