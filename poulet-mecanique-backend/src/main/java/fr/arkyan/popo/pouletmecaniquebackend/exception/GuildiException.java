package fr.arkyan.popo.pouletmecaniquebackend.exception;

public class GuildiException extends RuntimeException {

    public GuildiException(String message) {
        super(message);
    }

    public GuildiException(String message, Throwable cause) {
        super(message, cause);
    }

}
