package ru.practicum.shareit.integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoSpecified;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemService itemService;

    private User owner;
    private User user;
    private ItemRequest itemRequest;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        owner = User.builder().name("Owner").email("owner@email.com").build();
        user = User.builder().name("User").email("user@email.com").build();
        em.persist(owner);
        em.persist(user);

        itemRequest = ItemRequest.builder().description("Need item").requester(user).created(LocalDateTime.now()).build();
        em.persist(itemRequest);

        itemDto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        em.flush();
    }

    @Test
    void shouldSaveItem() {
        ItemDto result = itemService.create(owner.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery(
                "SELECT i FROM Item i WHERE i.id = :id", Item.class
        );
        Item persistedItem = query.setParameter("id", result.getId()).getSingleResult();

        assertNotNull(persistedItem);
        assertEquals(itemDto.getName(), persistedItem.getName());
        assertEquals(owner.getId(), persistedItem.getOwner().getId());
    }

    @Test
    void shouldSaveCommentWithRelations() {
        ItemDto item = itemService.create(owner.getId(), itemDto);
        createPastBooking(item.getId());

        CommentDto commentDto = CommentDto.builder().text("Text").build();

        CommentDto result = itemService.createComment(user.getId(), item.getId(), commentDto);

        TypedQuery<Comment> query = em.createQuery(
                "SELECT c FROM Comment c WHERE c.id = :id", Comment.class
        );
        Comment persistedComment = query.setParameter("id", result.getId()).getSingleResult();

        assertEquals("Text", persistedComment.getText());
        assertEquals(user.getId(), persistedComment.getAuthor().getId());
        assertEquals(item.getId(), persistedComment.getItem().getId());
    }

    @Test
    void shouldFindItemWithBookingsAndComments() {
        ItemDto item = itemService.create(owner.getId(), itemDto);
        createPastBooking(item.getId());
        createFutureBooking(item.getId());
        createComment(item.getId());

        em.flush();
        em.clear();

        ItemDtoSpecified result = itemService.findById(owner.getId(), item.getId());

        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertEquals(1, result.getComments().size());
    }

    @Test
    void shouldUpdateItem() {
        ItemDto created = itemService.create(owner.getId(), itemDto);
        ItemDto update = ItemDto.builder()
                .name("Updated")
                .description("New Description")
                .available(false)
                .build();

        itemService.update(owner.getId(), created.getId(), update);

        Item updatedItem = em.find(Item.class, created.getId());
        assertEquals("Updated", updatedItem.getName());
        assertEquals("New Description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void shouldDeleteItem() {
        ItemDto created = itemService.create(owner.getId(), itemDto);
        itemService.deleteById(created.getId());

        Item deletedItem = em.find(Item.class, created.getId());
        assertNull(deletedItem);
    }

    @Test
    void shouldFindByTextInNameOrDescription() {
        itemService.create(owner.getId(), itemDto);
        em.flush();

        Collection<ItemDto> results = itemService.search("desc");

        TypedQuery<Item> query = em.createQuery(
                "SELECT i FROM Item i WHERE lower(i.name) LIKE lower(concat('%', :text,'%')) " +
                        "OR lower(i.description) LIKE lower(concat('%', :text,'%'))", Item.class
        );
        List<Item> expected = query.setParameter("text", "desc").getResultList();

        assertEquals(expected.size(), results.size());
    }

    @Test
    void shouldRollbackWhenNotOwner() {
        ItemDto created = itemService.create(owner.getId(), itemDto);
        ItemDto update = ItemDto.builder().name("Updated").build();

        assertThrows(AccessDeniedException.class,
                () -> itemService.update(user.getId(), created.getId(), update));

        Item item = em.find(Item.class, created.getId());
        assertEquals("Item", item.getName());
    }

    @Test
    void shouldReturnAllItemsForOwnerWithBookingsAndComments() {
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("Need another item")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        em.persist(itemRequest2);
        ItemDto itemDtoOther = ItemDto.builder()
                .name("Item2")
                .description("Description")
                .available(true)
                .requestId(itemRequest2.getId())
                .build();
        ItemDto item1 = itemService.create(owner.getId(), itemDto);
        ItemDto item2 = itemService.create(owner.getId(), itemDtoOther);

        createPastBooking(item1.getId());
        createFutureBooking(item1.getId());
        createComment(item1.getId());

        em.flush();
        em.clear();

        Collection<ItemDtoSpecified> result = itemService.findForTheUser(owner.getId());

        assertThat(result, hasSize(2));

        ItemDtoSpecified firstItem = result.stream()
                .filter(i -> i.getId().equals(item1.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(firstItem.getLastBooking(), notNullValue());
        assertThat(firstItem.getNextBooking(), notNullValue());
        assertThat(firstItem.getComments(), hasSize(1));

        ItemDtoSpecified secondItem = result.stream()
                .filter(i -> i.getId().equals(item2.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(secondItem.getLastBooking(), nullValue());
        assertThat(secondItem.getNextBooking(), nullValue());
        assertThat(secondItem.getComments(), empty());
    }

    @Test
    void shouldReturnItemsWithoutBookingsIfUserIsNotOwner() {
        ItemDto item = itemService.create(owner.getId(), itemDto);
        createPastBooking(item.getId());
        createFutureBooking(item.getId());
        createComment(item.getId());

        em.flush();
        em.clear();

        Collection<ItemDtoSpecified> result = itemService.findForTheUser(user.getId());

        assertThat(result, empty());

        ItemDto userItem = itemService.create(user.getId(), itemDto);
        createPastBooking(userItem.getId());

        em.flush();
        em.clear();

        result = itemService.findForTheUser(user.getId());
        ItemDtoSpecified dto = result.iterator().next();

        assertThat(dto.getLastBooking(), notNullValue());
        assertThat(dto.getNextBooking(), nullValue());
        assertThat(dto.getComments(), empty());
    }

    @Test
    void shouldIncludeCommentsForItems() {
        ItemDto item = itemService.create(owner.getId(), itemDto);
        createComment(item.getId());

        em.flush();
        em.clear();

        Collection<ItemDtoSpecified> result = itemService.findForTheUser(owner.getId());
        ItemDtoSpecified dto = result.iterator().next();

        assertThat(dto.getComments(), hasSize(1));
        assertThat(dto.getComments().getFirst().getText(), equalTo("Test comment"));
    }

   /* @Test
    void shouldReturnItemsWithNullBookingsIfNoBookings() {
        ItemDto item = itemService.create(owner.getId(), itemDto);

        em.flush();
        em.clear();

        Collection<ItemDtoSpecified> result = itemService.findForTheUser(owner.getId());
        ItemDtoSpecified dto = result.iterator().next();

        assertThat(dto.getLastBooking(), nullValue());
        assertThat(dto.getNextBooking(), nullValue());
    }*/

    @Test
    void shouldThrowNotFoundExceptionIfUserNotExists() {
        Long invalidUserId = 0L;

        assertThatThrownBy(() -> itemService.findForTheUser(invalidUserId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id: 0 is not in repository");
    }

    @Test
    void shouldReturnOnlyItemsBelongingToUser() {
        ItemDto ownerItem = itemService.create(owner.getId(), itemDto);
        ItemDto userItem = itemService.create(user.getId(), itemDto);

        em.flush();
        em.clear();

        Collection<ItemDtoSpecified> ownerItems = itemService.findForTheUser(owner.getId());
        assertThat(ownerItems, hasSize(1));
        assertThat(ownerItems.iterator().next().getId(), equalTo(ownerItem.getId()));

        Collection<ItemDtoSpecified> userItems = itemService.findForTheUser(user.getId());
        assertThat(userItems, hasSize(1));
        assertThat(userItems.iterator().next().getId(), equalTo(userItem.getId()));
    }

    private void createPastBooking(Long itemId) {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(em.find(Item.class, itemId))
                .booker(user)
                .status(Status.APPROVED)
                .build();

        em.persist(booking);
    }

    private void createFutureBooking(Long itemId) {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(em.find(Item.class, itemId))
                .booker(user)
                .status(Status.APPROVED)
                .build();

        em.persist(booking);
    }

    private void createComment(Long itemId) {
        Comment comment = Comment.builder().text("Test comment").item(em.find(Item.class, itemId))
                .author(user)
                .created(LocalDateTime.now())
                .build();

        em.persist(comment);
    }
}
