package it.polimi.ingsw.is25am49.server.model.exceptions;

public class ConcurrentException extends RuntimeException {
    public ConcurrentException(String message) {
        super(message);
    }
}
