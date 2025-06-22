package it.polimi.ingsw.is25am49.client.model.exceptions;

public class NotEnoughPlayerException extends RuntimeException {
    public NotEnoughPlayerException(String message) {
        super(message);
    }
}
