package org.example.progetto.exceptions;

public class CfAlreadyExistException extends RuntimeException {
    public CfAlreadyExistException(String message) {
        super(message);
    }
}
