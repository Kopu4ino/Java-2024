package edu.java.updateClients.updateDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record StackOverflowQuestionResponse(
    @JsonProperty("items")
    List<Item> items) {

    public record Item(
        @JsonProperty("owner")
        Owner owner,

        @JsonProperty("last_activity_date")
        OffsetDateTime lastActivityDate
    ) {
        public record Owner(
            @JsonProperty("display_name")
            String displayName
        ) {

        }
    }
}
