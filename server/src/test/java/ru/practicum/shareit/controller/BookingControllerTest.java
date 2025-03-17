package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private BookingDto bookingDto;

    private BookingResponse bookingResponse;

    @BeforeEach
    public void beforeEach() {
        this.bookingDto = BookingDto.builder()
                .id(1L)
                .bookerId(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .status(Status.WAITING)
                .build();

        this.bookingResponse = BookingResponse.builder()
                .id(1L)
                .booker(
                        UserDto.builder()
                                .id(1L)
                                .name("name")
                                .email("email")
                                .build()
                )
                .item(
                        ItemDto.builder()
                                .id(1L)
                                .name("name")
                                .description("description")
                                .available(true)
                                .owner(2L)
                                .build()
                )
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .status(Status.WAITING)
                .build();
    }

    @Test
    public void shouldAddBooking() throws Exception {
        when(bookingService.create(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponse.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingResponse.getEnd().toString()))
                .andExpect(jsonPath("$.status").value(bookingResponse.getStatus().toString()))
                .andExpect(jsonPath("$.item.id").value(bookingResponse.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponse.getBooker().getId()));
    }

    @Test
    public void shouldApproveBooking() throws Exception {
        Mockito
                .when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponse.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingResponse.getEnd().toString()))
                .andExpect(jsonPath("$.status").value(bookingResponse.getStatus().toString()))
                .andExpect(jsonPath("$.item.id").value(bookingResponse.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponse.getBooker().getId()));
    }

    @Test
    public void shouldGetBookingById() throws Exception {
        Mockito
                .when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponse.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingResponse.getEnd().toString()))
                .andExpect(jsonPath("$.status").value(bookingResponse.getStatus().toString()))
                .andExpect(jsonPath("$.item.id").value(bookingResponse.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponse.getBooker().getId()));
    }

    @Test
    public void shouldGetListOfUsersBookings() throws Exception {
        Mockito
                .when(bookingService.readBookingsForUser(anyLong(), anyString()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.[0].start").value(bookingResponse.getStart().toString()))
                .andExpect(jsonPath("$.[0].end").value(bookingResponse.getEnd().toString()))
                .andExpect(jsonPath("$.[0].status").value(bookingResponse.getStatus().toString()))
                .andExpect(jsonPath("$.[0].item.id").value(bookingResponse.getItem().getId()))
                .andExpect(jsonPath("$.[0].booker.id").value(bookingResponse.getBooker().getId()));
    }

    @Test
    public void shouldGetListOfBookingsUserItems() throws Exception {
        Mockito
                .when(bookingService.readBookingsForOwner(anyLong(), anyString()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.[0].start").value(bookingResponse.getStart().toString()))
                .andExpect(jsonPath("$.[0].end").value(bookingResponse.getEnd().toString()))
                .andExpect(jsonPath("$.[0].status").value(bookingResponse.getStatus().toString()))
                .andExpect(jsonPath("$.[0].item.id").value(bookingResponse.getItem().getId()))
                .andExpect(jsonPath("$.[0].booker.id").value(bookingResponse.getBooker().getId()));
    }
}
