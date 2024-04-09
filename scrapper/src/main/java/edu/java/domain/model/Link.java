package edu.java.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String url;
    @NotNull
    private OffsetDateTime lastUpdate;
    @NotNull
    private OffsetDateTime lastCheck;

    @ManyToMany(mappedBy = "trackingLinks")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Chat> tgChats;

    public Link(String url) {
        this.url = url;
        this.lastUpdate = getCurrentDateTime();
        this.lastCheck = getCurrentDateTime();
    }

    private static OffsetDateTime getCurrentDateTime() {
        return OffsetDateTime.now();
    }
}

