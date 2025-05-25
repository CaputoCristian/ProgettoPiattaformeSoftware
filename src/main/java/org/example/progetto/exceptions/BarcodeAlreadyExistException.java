package org.example.progetto.exceptions;

public class BarcodeAlreadyExistException extends RuntimeException {
    public BarcodeAlreadyExistException(String message) {
        super(message);
    }
}
