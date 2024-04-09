package edu.java.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table
@Data
@NoArgsConstructor
public class Chat {
    @Id
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "chat_link",
        joinColumns = @JoinColumn(name = "chat_id"),
        inverseJoinColumns = @JoinColumn(name = "link_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Link> trackingLinks;

    public Chat(Long id) {
        this.id = id;
    }
}
