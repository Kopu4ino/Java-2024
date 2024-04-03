package shared.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record LinkUpdateRequest(

    @NotNull
    Long id,
    @NotNull
    String url,
    String description,
    @NotEmpty
    List<Long> tgChatIds
) {
}
