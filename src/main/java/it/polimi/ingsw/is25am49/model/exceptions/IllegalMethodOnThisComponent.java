package it.polimi.ingsw.is25am49.model.exceptions;

public class IllegalMethodOnThisComponent extends RuntimeException {
    public IllegalMethodOnThisComponent(String message) {
        super(message);
    }
}
