package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingDeniedException;
import ru.practicum.shareit.exception.BookingUpdateStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.booking.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.BookingMapper.toBookingResponse;
import static ru.practicum.shareit.booking.model.Status.*;
import static ru.practicum.shareit.user.UserMapper.toUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingResponse create(Long userId, BookingDto bookingDto) {

        log.trace("Adding booking at service level has started");
        User user = toUser(userService.findById(userId));
        log.debug("User with id: {} is in repository", userId);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException(String.format("There's no item with id: %d in repository", bookingDto.getId())));
        log.debug("Item with id: {} is in repository", item.getId());

        if (!item.getAvailable()) {
            throw new BookingDeniedException("Item is not available for booking. It had been already booked");
        }
        log.debug("The item has available status for booking");

        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new BookingDeniedException("It's not available to book your own item");
        }
        log.debug("The user has roots to book the item");

        Booking booking = toBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(WAITING);
        log.trace("The item has been successfully assigned some characteristics");

        return toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse findById(Long userId, Long bookingId) {

        log.trace("Searching for booking with id: {} by user with id {} has started (at service layer)", bookingId,
                userId);

        userService.findById(userId);
        log.debug("User with id: {} is in repository", userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("There's no booking with id: %d in repository", bookingId)));
        log.debug("Booking with id: {} is in repository", bookingId);


        if (!Objects.equals(userId, booking.getBooker().getId()) &&
                !Objects.equals(userId, booking.getItem().getOwner().getId())) {
            throw new AccessDeniedException("Only the owner of the item or the author of the booking have access");
        }
        log.debug("The search of the booking with id {} by user with id {} was succeeded", bookingId, userId);

        return toBookingResponse(booking);
    }

    @Override
    public Collection<BookingResponse> readBookingsForOwner(Long userId, String state) {

        log.trace("Searching for some bookings for user-owner with id: {} has started (at service layer)", userId);
        userService.findById(userId);
        log.debug("User with id: {} is in repository", userId);

        LocalDateTime now = LocalDateTime.now();
        log.trace("Current Date-Time set: {}", now);

        List<Booking> bookings = switch (state) {
            case "CURRENT" ->
                    bookingRepository.findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                            now, now);
            case "PAST" -> bookingRepository.findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE" -> bookingRepository.findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING" -> bookingRepository.findBookingsByItemOwnerIdAndStatusOrderByStartDesc(userId, WAITING);
            case "REJECTED" -> bookingRepository.findBookingsByItemOwnerIdAndStatusOrderByStartDesc(userId, REJECTED);
            default -> bookingRepository.findBookingsByItemOwnerIdOrderByStartDesc(userId);
        };
        log.debug("Booking status set");

        return bookings.stream().map(BookingMapper::toBookingResponse).toList();
    }

    @Override
    public List<BookingResponse> readBookingsForUser(Long userId, String state) {

        log.trace("Searching for some bookings for user with id: {} has started (at service layer)", userId);
        userService.findById(userId);
        log.debug("User with id: {} is in repository", userId);

        LocalDateTime now = LocalDateTime.now();
        log.trace("Current Date-Time set: {}", now);

        List<Booking> bookings = switch (state) {
            case "CURRENT" -> bookingRepository.findBookingsByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                    now, now);
            case "PAST" -> bookingRepository.findBookingsByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE" -> bookingRepository.findBookingsByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING" -> bookingRepository.findBookingsByBookerIdAndStatusOrderByStartDesc(userId, WAITING);
            case "REJECTED" -> bookingRepository.findBookingsByBookerIdAndStatusOrderByStartDesc(userId, REJECTED);
            default -> bookingRepository.findBookingsByBookerIdOrderByStartDesc(userId);
        };
        log.debug("Booking status set");

        return bookings.stream().map(BookingMapper::toBookingResponse).toList();
    }

    @Override
    public BookingResponse updateStatus(Long userId, Long bookingId, Boolean approved) {

        log.trace("Status update of booking with id: {} has started (at service layer)", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("There's no booking with id: %d in repository", bookingId)));
        log.debug("Booking with id: {} is in repository", bookingId);

        if (booking.getStatus().equals(APPROVED) && approved) {
            throw new BookingUpdateStatusException(String.format("Unable to approve the booking. " +
                    "Booking with id: %d has already been approve", bookingId));
        }
        log.debug("Booking is able to be approved as it hadn't been approved before");

        if (!Objects.equals(userId, booking.getItem().getOwner().getId())) {
            throw new AccessDeniedException(String.format("Status update denied. " +
                    "User with id: %d isn't an owner of item with id: %d", userId, booking.getItem().getId()));
        }
        log.debug("User has rights to update status of booking");

        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }

        log.debug("Status set");

        return toBookingResponse(bookingRepository.save(booking));
    }
}
