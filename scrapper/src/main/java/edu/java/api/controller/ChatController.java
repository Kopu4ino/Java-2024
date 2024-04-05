package edu.java.api.controller;

import edu.java.service.jdbc.JdbcChatService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/tg-chat")
@RestController
@RequiredArgsConstructor
public class ChatController {
    @Autowired
    private final JdbcChatService chatService;

    @PostMapping("/{chatId}")
    public String registerChat(@PathVariable @NotNull Long chatId) {
        chatService.registerChat(chatId);
        return "Chat registered with id=%d".formatted(chatId);
    }

    @DeleteMapping("/{chatId}")
    public String deleteChat(@PathVariable @NotNull Long chatId) {
        chatService.unregisterChat(chatId);
        return "Chat deleted with id=%d".formatted(chatId);
    }
}
