package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */

@Entity
@Table(name = "item")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name")
    private String name;

    @Column(name = "description")
    private String description;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    private User owner;

    @Column(name = "available")
    private Boolean available;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", referencedColumnName = "request_id")
    private Request request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
