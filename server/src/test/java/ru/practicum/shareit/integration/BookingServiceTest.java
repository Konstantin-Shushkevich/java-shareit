package ru.practicum.shareit.integration;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingDeniedException;
import ru.practicum.shareit.exception.BookingUpdateStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.model.Status.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;

    @Autowired
    private EntityManager em;

    private UserDto owner;
    private UserDto booker;
    private ItemDto item;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        owner = UserDto.builder()
                .name("Owner")
                .email("owner@example.com")
                .build();

        owner = userService.create(owner);

        booker = UserDto.builder()
                .name("Booker")
                .email("booker@example.com")
                .build();

        booker = userService.create(booker);

        item = ItemDto.builder()
                .name("Item")
                .description("Test Item")
                .available(true)
                .owner(owner.getId())
                .build();

        new Item();

        item = itemService.create(owner.getId(), item);

        bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start((LocalDateTime.now().plusDays(1)))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void returnsBookingResponseIfCreate() {
        BookingResponse response = bookingService.create(booker.getId(), bookingDto);

        assertNotNull(response.getId());
        assertEquals(WAITING, response.getStatus());
        assertEquals(booker.getId(), response.getBooker().getId());
        assertEquals(item.getId(), response.getItem().getId());
    }

    @Test
    void throwsBookingDeniedExceptionIfItemIsNotValid() {
        item.setAvailable(false);
        itemService.create(owner.getId(), item);

        assertThrows(BookingDeniedException.class,
                () -> bookingService.create(booker.getId(), bookingDto));
    }

    @Test
    void throwsBookingDeniedExceptionIfBookerIsOwnerOfItem() {
        assertThrows(BookingDeniedException.class,
                () -> bookingService.create(owner.getId(), bookingDto));
    }

    @Test
    void throwsAccessDeniedExceptionIfUserTryingToFindByIdIsNotOwner() {
        BookingResponse booking = bookingService.create(booker.getId(), bookingDto);
        UserDto anotherUser = UserDto.builder()
                .name("Another")
                .email("another@example.com")
                .build();

        UserDto anotherUserSaved = userService.create(anotherUser);

        assertThrows(AccessDeniedException.class,
                () -> bookingService.findById(anotherUserSaved.getId(), booking.getId()));
    }

    @Test
    void shouldReturnBookingIfUserIsOwner() {
        BookingResponse createdBooking = bookingService.create(booker.getId(), bookingDto);
        BookingResponse foundBooking = bookingService.findById(owner.getId(), createdBooking.getId());

        assertEquals(createdBooking.getId(), foundBooking.getId());
    }

    @Test
    void throwsAccessDeniedExceptionIfUserTryingToUpdateStatusIsNotOwner() {
        BookingResponse booking = bookingService.create(booker.getId(), bookingDto);

        assertThrows(AccessDeniedException.class,
                () -> bookingService.updateStatus(booker.getId(), booking.getId(), true));
    }

    @Test
    void shouldUpdateStatus() {
        BookingResponse booking = bookingService.create(booker.getId(), bookingDto);
        BookingResponse updated = bookingService.updateStatus(owner.getId(), booking.getId(), true);

        assertEquals(APPROVED, updated.getStatus());
    }

    @Test
    void throwsBookingUpdateStatusExceptionIfStatusIsAlreadyApproved() {
        BookingResponse booking = bookingService.create(booker.getId(), bookingDto);
        bookingService.updateStatus(owner.getId(), booking.getId(), true);

        assertThrows(BookingUpdateStatusException.class,
                () -> bookingService.updateStatus(owner.getId(), booking.getId(), true));
    }

    @Test
    void returnsCurrentBookingsForOwner() {
        LocalDateTime now = LocalDateTime.now();
        bookingDto.setStart(now.minusDays(1));
        bookingDto.setEnd(now.plusDays(1));
        BookingResponse booking = bookingService.create(booker.getId(), bookingDto);
        bookingService.updateStatus(owner.getId(), booking.getId(), true);

        List<BookingResponse> bookings = (List<BookingResponse>) bookingService.readBookingsForOwner(owner.getId(),
                "CURRENT");

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.getFirst().getId());
    }

    @Test
    void returnsPastBookingsForOwner() {
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        BookingResponse booking = bookingService.create(booker.getId(), bookingDto);
        bookingService.updateStatus(owner.getId(), booking.getId(), true);

        List<BookingResponse> result = (List<BookingResponse>) bookingService.readBookingsForOwner(owner.getId(),
                "PAST");

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void returnsFutureBookingsForOwner() {
        bookingService.create(booker.getId(), bookingDto);

        List<BookingResponse> result = (List<BookingResponse>) bookingService.readBookingsForOwner(owner.getId(),
                "FUTURE");

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void returnsWaitingBookingsForOwner() {
        bookingService.create(booker.getId(), bookingDto);

        List<BookingResponse> result = (List<BookingResponse>) bookingService.readBookingsForOwner(owner.getId(),
                "WAITING");

        assertEquals(1, result.size());
        assertEquals(WAITING, result.getFirst().getStatus());
    }

    @Test
    void returnsRejectedBookingsForOwner() {
        BookingResponse booking = bookingService.create(booker.getId(), bookingDto);
        bookingService.updateStatus(owner.getId(), booking.getId(), false);

        List<BookingResponse> result = (List<BookingResponse>) bookingService.readBookingsForOwner(owner.getId(),
                "REJECTED");

        assertEquals(1, result.size());
        assertEquals(REJECTED, result.getFirst().getStatus());
    }

    @Test
    void returnsAllSortedByStartDescByDefault() {
        bookingService.create(booker.getId(), bookingDto);

        BookingDto earlierBookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingService.create(booker.getId(), earlierBookingDto);

        List<BookingResponse> result = (List<BookingResponse>) bookingService.readBookingsForOwner(owner.getId(),
                "ALL");

        assertEquals(2, result.size());
        assertTrue(result.get(0).getStart().isAfter(result.get(1).getStart()));
    }

    @Test
    void returnsCurrentBookingsForUser() {
        bookingDto.setStart(LocalDateTime.now().minusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        BookingResponse booking = bookingService.create(booker.getId(), bookingDto);

        List<BookingResponse> result = bookingService.readBookingsForUser(booker.getId(), "CURRENT");

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.getFirst().getId());
    }

    @Test
    void returnsPastBookingsForUser() {
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        BookingResponse booking = bookingService.create(booker.getId(), bookingDto);

        List<BookingResponse> result = bookingService.readBookingsForUser(booker.getId(), "PAST");

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.getFirst().getId());
    }

    @Test
    void returnsWaitingBookingsForUser() {
        bookingService.create(booker.getId(), bookingDto);

        List<BookingResponse> result = bookingService.readBookingsForUser(booker.getId(), "WAITING");

        assertEquals(1, result.size());
        assertEquals(WAITING, result.getFirst().getStatus());
    }

    @Test
    void returnsRejectedBookingsForUser() {
        BookingResponse booking = bookingService.create(booker.getId(), bookingDto);
        bookingService.updateStatus(owner.getId(), booking.getId(), false);

        List<BookingResponse> result = bookingService.readBookingsForUser(booker.getId(), "REJECTED");

        assertEquals(1, result.size());
        assertEquals(REJECTED, result.getFirst().getStatus());
    }

    @Test
    void returnsFutureBookingsForUser() {
        BookingResponse booking = bookingService.create(booker.getId(), bookingDto);
        List<BookingResponse> bookings = bookingService.readBookingsForUser(booker.getId(), "FUTURE");

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.getFirst().getId());
    }
}
