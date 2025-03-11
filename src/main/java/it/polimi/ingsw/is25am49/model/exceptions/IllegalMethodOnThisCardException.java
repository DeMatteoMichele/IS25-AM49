package it.polimi.ingsw.is25am49.model.exceptions;

public class IllegalMethodOnThisCardException extends RuntimeException {
    public IllegalMethodOnThisCardException(String message) {
        super(message);
    }
}
