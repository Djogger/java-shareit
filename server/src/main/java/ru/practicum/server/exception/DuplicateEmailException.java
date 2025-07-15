package ru.practicum.server.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String ex) {
        super(ex);
    }
}
