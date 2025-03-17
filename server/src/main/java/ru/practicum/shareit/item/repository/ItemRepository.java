package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item i " +
            "where i.available = true " +
            "and (upper(i.name) like upper(concat('%', ?1, '%'))" +
            "or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.comments " +
            "LEFT JOIN FETCH i.bookings b " +
            "WHERE i.id = :id")
    Optional<Item> findByIdInFull(@Param("id") Long id);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.comments " +
            "LEFT JOIN FETCH i.bookings b " +
            "WHERE i.owner.id = :ownerId")
    List<Item> findAllByOwnerIdInFull(@Param("ownerId") Long ownerId);

    List<Item> findItemByRequestId(Long requestId);
}
