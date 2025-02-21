package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Objects;

import static ru.practicum.shareit.item.ItemMapper.toItemDtoFromItem;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Component
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        if (Objects.isNull(booking)) {
            return null;
        }
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingResponse toBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(toItemDtoFromItem(booking.getItem()))
                .booker(toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }
}
