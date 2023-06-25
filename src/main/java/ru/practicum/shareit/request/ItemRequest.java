package ru.practicum.shareit.request;

import lombok.Data;

import javax.persistence.*;

/**
 * TODO Sprint add-item-requests.
 */

@Entity
@Table(name = "request")
@Data
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long requestId;
}
