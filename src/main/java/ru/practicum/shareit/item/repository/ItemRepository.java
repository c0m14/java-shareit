package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByOwnerId(Pageable page, Long ownerId);

    List<Item> findByOwnerId(Long ownerId);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true and (lower(it.name) like lower(concat('%', ?1, '%')) " +
            "or lower(it.description) like lower(concat('%', ?1,'%')))")
    Page<Item> searchByText(Pageable page, String text);

    List<Item> findAllByRequest_RequestId(Long requestId);

}
