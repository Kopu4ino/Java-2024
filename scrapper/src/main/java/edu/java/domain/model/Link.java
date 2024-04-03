package edu.java.domain.model;

import java.time.OffsetDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Link {
    private Long id;
    private String url;
    private OffsetDateTime lastUpdate;
    private OffsetDateTime lastCheck;

    public Link(String url) {
        this.url = url;
        this.lastUpdate = getCurrentDateTime();
        this.lastCheck = getCurrentDateTime();
    }

    private static OffsetDateTime getCurrentDateTime() {
        return OffsetDateTime.now();
    }
}

