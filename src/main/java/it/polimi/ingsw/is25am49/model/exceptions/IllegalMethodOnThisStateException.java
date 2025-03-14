package it.polimi.ingsw.is25am49.model.exceptions;

public class IllegalMethodOnThisStateException extends RuntimeException {
    public IllegalMethodOnThisStateException(String message) {
        super(message);
    }
}
