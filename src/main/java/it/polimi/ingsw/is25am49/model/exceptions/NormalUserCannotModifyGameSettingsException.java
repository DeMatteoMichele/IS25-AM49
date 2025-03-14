package it.polimi.ingsw.is25am49.model.exceptions;

public class NormalUserCannotModifyGameSettingsException extends RuntimeException {
    public NormalUserCannotModifyGameSettingsException(String message) {
        super(message);
    }
}
