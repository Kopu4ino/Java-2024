package edu.java.mapper;

import edu.java.domain.model.Link;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shared.dto.request.AddLinkRequest;
import shared.dto.request.RemoveLinkRequest;
import shared.dto.response.LinkResponse;

@Service
@RequiredArgsConstructor
public class DefaultObjectMapper {
    public List<LinkResponse> mapToListLinksResponse(List<Link> links) {
        return links.stream().map(link -> new LinkResponse(link.getId(), URI.create(link.getUrl()))).toList();
    }

    public Link convertToLink(AddLinkRequest request) {
        return new Link(request.url());
    }

    public Link convertToLink(RemoveLinkRequest request) {
        return new Link(request.url());
    }

    public LinkResponse convertToLinkResponse(Link link) {
        return new LinkResponse(link.getId(), URI.create(link.getUrl()));
    }
}
