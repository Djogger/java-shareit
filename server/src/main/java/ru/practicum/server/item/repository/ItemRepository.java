package ru.practicum.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Collection<Item> findByQuery(@Param("query") String query);

    @Query("SELECT i FROM Item i WHERE i.request.id = :requestId ORDER BY i.request.created DESC")
    List<Item> findByRequestId(@Param("requestId") Long requestId);

    @Query("SELECT i FROM Item i WHERE i.request.id IN :requestsIds")
    List<Item> findByRequestsIds(@Param("requestsIds") List<Long> requestsIds);

}
