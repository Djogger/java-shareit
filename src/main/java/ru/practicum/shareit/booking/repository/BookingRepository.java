package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Booking findTopByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime start);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :id AND b.end < :currentTime AND upper(b.status) = 'APPROVED' " +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerIdStatePast(@Param("id") long id, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :id AND b.end < :currentTime AND upper(b.status) = 'APPROVED' AND b.item.id = :itemId " +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndItemIdStatePast(@Param("id") long id, @Param("itemId") long itemId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end >= :currentTime AND :currentTime >= b.start " +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerIdStateCurrent(@Param("userId") long userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :currentTime ORDER BY b.start DESC")
    List<Booking> findFuture(@Param("userId") long userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findOwnerAll(long ownerId);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE  i.owner.id = :userId AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerFuture(@Param("userId") long userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId " +
            "AND b.start <= :currentTime AND b.end >= :currentTime ORDER BY b.start DESC ")
    List<Booking> findOwnerCurrent(@Param("userId") long userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId AND b.end < :currentTime")
    List<Booking> findOwnerPast(@Param("userId") long userId, @Param("currentTime") LocalDateTime currentTime);
}