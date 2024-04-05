package shared.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddLinkRequest(
    @NotBlank
    String url
) {
}
