package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                                  LocalDateTime nowStart,
                                                                                  LocalDateTime nowEnd);

    List<Booking> findBookingsByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findBookingsByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findBookingsByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findBookingsByBookerIdOrderByStartDesc(Long userId);


    List<Booking> findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                                     LocalDateTime nowStart,
                                                                                     LocalDateTime nowEnd);

    List<Booking> findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findBookingsByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findBookingsByItemOwnerIdOrderByStartDesc(Long userId);


    Booking findTopBookingByItemIdAndStatusNotAndStartBeforeOrderByEndDesc(Long bookingId,
                                                                           Status status,
                                                                           LocalDateTime start);

    Booking findTopBookingByItemIdAndStatusNotAndStartAfterOrderByStartAsc(Long bookingId,
                                                                           Status status,
                                                                           LocalDateTime start);
}
