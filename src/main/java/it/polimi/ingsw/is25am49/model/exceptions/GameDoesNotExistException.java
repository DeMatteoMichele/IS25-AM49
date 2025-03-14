package it.polimi.ingsw.is25am49.model.exceptions;

public class GameDoesNotExistException extends RuntimeException {
    public GameDoesNotExistException(String message) {
        super(message);
    }
}
