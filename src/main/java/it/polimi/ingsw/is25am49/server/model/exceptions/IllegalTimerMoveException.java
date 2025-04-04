package it.polimi.ingsw.is25am49.server.model.exceptions;

public class IllegalTimerMoveException extends RuntimeException {
    public IllegalTimerMoveException(String message) {
        super(message);
    }
}
