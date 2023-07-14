package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByOwnerId(Long ownerId, Sort sort);

    Page<Request> findAll(Pageable page);

    @Query("select distinct request from Request as request " +
            "join request.items " +
            "where request.owner.id <> ?1")
    Page<Request> findAllExceptOwnerRequestsEager(Long ownerId, Pageable page);

}
