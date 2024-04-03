package edu.java.service.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChatAlreadyRegisteredException extends RuntimeException {
    private final HttpStatus statusCode;

    private final String description;

    public ChatAlreadyRegisteredException(Long id) {
        super("Чат с id %d уже зарегистирован.".formatted(id));
        this.statusCode = HttpStatus.BAD_REQUEST;
        this.description = "Чат уже зарегистрирован.";
    }
}
