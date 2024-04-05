package edu.java.api.controller;

import edu.java.domain.model.Link;
import edu.java.mapper.DefaultObjectMapper;
import edu.java.service.jdbc.JdbcLinkService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shared.dto.request.AddLinkRequest;
import shared.dto.request.RemoveLinkRequest;
import shared.dto.response.LinkResponse;

@RequestMapping("/links")
@RestController
@AllArgsConstructor
public class LinkController {
    @Autowired
    private final JdbcLinkService linkService;

    @Autowired DefaultObjectMapper mapper;

    @GetMapping
    public List<LinkResponse> getLinks(@RequestHeader("Tg-Chat-Id") Long chatId) {
        List<Link> userLinks = linkService.getUserLinks(chatId);
        return mapper.mapToListLinksResponse(userLinks);
    }

    @PostMapping
    public LinkResponse addLink(@RequestHeader("Tg-Chat-Id") Long chatId, @RequestBody AddLinkRequest request) {
        Link link = mapper.convertToLink(request);
        Link addedLink = linkService.addLink(chatId, link);
        return mapper.convertToLinkResponse(addedLink);
    }

    @DeleteMapping
    public LinkResponse deleteLink(@RequestHeader("Tg-Chat-Id") Long chatId, @RequestBody RemoveLinkRequest request) {
        Link link = mapper.convertToLink(request);
        Link removedLink = linkService.deleteLink(chatId, link);
        return mapper.convertToLinkResponse(removedLink);
    }
}
