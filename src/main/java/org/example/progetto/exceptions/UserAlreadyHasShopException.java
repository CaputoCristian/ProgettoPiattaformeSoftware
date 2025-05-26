package org.example.progetto.exceptions;

public class UserAlreadyHasShopException extends RuntimeException {
    public UserAlreadyHasShopException(String message) {
        super(message);
    }
}
