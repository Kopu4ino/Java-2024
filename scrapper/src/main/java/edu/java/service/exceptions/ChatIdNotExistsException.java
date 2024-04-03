package edu.java.service.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChatIdNotExistsException extends RuntimeException {
    private final HttpStatus statusCode;

    private final String description;

    public ChatIdNotExistsException(Long chatId) {
        super("Чат с id %d не найден.".formatted(chatId));
        this.statusCode = HttpStatus.NOT_FOUND;
        this.description = "Чат не найден.";
    }
}
