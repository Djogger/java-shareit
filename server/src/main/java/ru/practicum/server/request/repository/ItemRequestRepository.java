package ru.practicum.server.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requester.id = :requesterId ORDER BY ir.created DESC")
    List<ItemRequest> findAllByRequesterId(@Param("requesterId") Long userId);
}
