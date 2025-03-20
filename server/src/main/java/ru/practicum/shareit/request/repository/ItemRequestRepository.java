package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequester_IdNotOrderByCreatedDesc(Long requesterId);

    @Query("SELECT DISTINCT r FROM ItemRequest r " +
            "LEFT JOIN FETCH r.items " +
            "WHERE r.requester.id = :userId " +
            "ORDER BY r.created DESC")
    List<ItemRequest> findByRequesterIdWithItems(@Param("userId") Long userId);
}
