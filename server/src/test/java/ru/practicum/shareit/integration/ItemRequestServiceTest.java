package ru.practicum.shareit.integration;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private EntityManager entityManager;

    private UserDto user;
    private UserDto otherUser;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .name("User Name")
                .email("user@example.com")
                .build();

        user = userService.create(user);

        otherUser = UserDto.builder()
                .name("Other User")
                .email("other@example.com")
                .build();

        otherUser = userService.create(otherUser);
    }

    @Test
    void returnsSavedRequestIfCreateAndRequestIsValid() {

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a test item")
                .build();

        ItemRequestDto savedRequest = itemRequestService.create(user.getId(), requestDto);
        System.out.println(savedRequest);

        assertNotNull(savedRequest.getId());
        assertEquals(requestDto.getDescription(), savedRequest.getDescription());
        assertNotNull(savedRequest.getCreated());

        var requestFromDb = entityManager.find(ItemRequest.class, savedRequest.getId());
        assertNotNull(requestFromDb);
        assertEquals(user.getId(), requestFromDb.getRequester().getId());
    }

    @Test
    void returnsRequestsWithItemsByUserWithRequests() {
        ItemRequestDto requestDto1 = ItemRequestDto.builder()
                .description("Request 1")
                .build();

        ItemRequestDto createdRequest1 = itemRequestService.create(user.getId(), requestDto1);

        ItemRequestDto requestDto2 = ItemRequestDto.builder()
                .description("Request 2")
                .build();

        itemRequestService.create(user.getId(), requestDto2);

        ItemDto item1 = ItemDto.builder()
                .name("Item 1")
                .requestId(createdRequest1.getId())
                .owner(user.getId())
                .build();

        itemService.create(user.getId(), item1);

        List<ItemRequestDto> result = itemRequestService.readAllByUser(user.getId());

        assertEquals(2, result.size());
        assertEquals("Request 2", result.get(0).getDescription());
        assertEquals("Request 1", result.get(1).getDescription());

        ItemRequestDto requestWithItems = result.stream()
                .filter(r -> r.getId().equals(createdRequest1.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(1, requestWithItems.getItems().size());
        assertEquals("Item 1", requestWithItems.getItems().getFirst().getName());
    }

    @Test
    void returnsOtherRequestsByOtherUsers() {
        ItemRequestDto otherRequestDto = ItemRequestDto.builder()
                .description("Other's request")
                .build();

        ItemRequestDto otherRequest = itemRequestService.create(otherUser.getId(), otherRequestDto);

        ItemRequestDto userRequestDto = ItemRequestDto.builder()
                .description("User's request")
                .build();

        itemRequestService.create(user.getId(), userRequestDto);

        List<ItemRequestDto> result = itemRequestService.readAllByOtherUsers(user.getId());

        assertEquals(1, result.size());
        assertEquals(otherRequest.getId(), result.getFirst().getId());
        assertEquals(otherUser.getId(), result.getFirst().getRequester());
    }

    @Test
    void returnsRequestWithItemsIfRequestIdIsValid() {
        ItemRequestDto userRequestDto = ItemRequestDto.builder()
                .description("User's request")
                .build();

        ItemRequestDto request = itemRequestService.create(user.getId(), userRequestDto);
        ItemDto item = ItemDto.builder()
                .name("Item")
                .requestId(request.getId())
                .owner(user.getId())
                .build();

        itemService.create(user.getId(), item);

        ItemRequestDto result = itemRequestService.readTheItemRequest(request.getId());

        assertNotNull(result);
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Item", result.getItems().getFirst().getName());
    }

    @Test
    void throwsNotFoundExceptionIfRequestIdNotValid() {
        Long invalidId = 0L;

        assertThrows(NotFoundException.class, () -> itemRequestService.readTheItemRequest(invalidId));
    }
}
