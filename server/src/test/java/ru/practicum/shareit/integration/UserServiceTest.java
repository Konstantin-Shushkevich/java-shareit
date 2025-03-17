package ru.practicum.shareit.integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserEmailNotUniqueException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceTest {
    private final EntityManager em;
    private final UserService userService;
    private final UserDto userDto = UserDto.builder().name("user").email("user@email.com").build();

    @Test
    public void shouldAddUser() {
        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void shouldNotAddUserWhenEmailNotUnique() {
        userService.create(userDto);

        assertThatThrownBy(() -> userService.create(userDto))
                .isInstanceOf(UserEmailNotUniqueException.class)
                .hasMessage("User with email: user@email.com is already exists");
    }

    @Test
    public void shouldFindUserById() {
        UserDto userDtoSaved = userService.create(userDto);

        UserDto result = userService.findById(userDtoSaved.getId());

        assertNotNull(result);
        assertEquals(userDtoSaved.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    public void shouldReturnEmptyListWhenNoUsers() {
        Collection<UserDto> result = userService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnAllUsers() {
        UserDto userDtoSecond = UserDto.builder().name("user2").email("user2@email.com").build();
        userService.create(userDto);
        UserDto savedSecondUser = userService.create(userDtoSecond);

        Collection<UserDto> result = userService.findAll();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(userDto -> userDto.getId().equals(savedSecondUser.getId())));
        assertTrue(result.stream().anyMatch(userDto -> userDto.getEmail().equals("user2@email.com")));
    }

    @Test
    void shouldUpdateAllFields() {
        UserDto userDtoSaved = userService.create(userDto);
        UserDto userDroForUpdate = UserDto.builder().name("user updated").email("userUpd@email.com").build();

        UserDto updatedUser = userService.update(userDtoSaved.getId(), userDroForUpdate);

        assertEquals(userDtoSaved.getId(), updatedUser.getId());
        assertEquals("user updated", updatedUser.getName());
        assertEquals("userUpd@email.com", updatedUser.getEmail());
    }

    @Test
    void shouldKeepOriginalNameWhenNotProvided() {
        UserDto userDtoSaved = userService.create(userDto);
        UserDto userDroForUpdate = UserDto.builder().email("userUpd@email.com").build();

        UserDto updatedUser = userService.update(userDtoSaved.getId(), userDroForUpdate);

        assertEquals("user", updatedUser.getName());
        assertEquals("userUpd@email.com", updatedUser.getEmail());
    }

    @Test
    void shouldKeepOriginalEmailWhenNotProvided() {
        UserDto userDtoSaved = userService.create(userDto);
        UserDto userDroForUpdate = UserDto.builder().name("user updated").build();

        UserDto updatedUser = userService.update(userDtoSaved.getId(), userDroForUpdate);

        assertEquals("user updated", updatedUser.getName());
        assertEquals("user@email.com", updatedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionIfTryToUpdateNonExistingUser() {
        UserDto userDtoSaved = userService.create(userDto);

        assertThrows(NotFoundException.class,
                () -> userService.update(0L, userDtoSaved));
    }

    @Test
    void shouldDeleteExistingUser() {
        UserDto userDtoSaved = userService.create(userDto);

        userService.deleteById(userDtoSaved.getId());

        assertTrue(userService.findAll().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenUserForDeleteNotExists() {
        assertThrows(NotFoundException.class,
                () -> userService.deleteById(0L));
    }

    @Test
    void shouldNotAffectOtherUsersAfterDelete() {
        UserDto anotherUser = UserDto.builder().name("user2").email("user2@email.com").build();

        UserDto userDtoForDelete = userService.create(userDto);
        UserDto savedAnotherUser = userService.create(anotherUser);

        userService.deleteById(userDtoForDelete.getId());

        Collection<UserDto> usersAfterDelete = userService.findAll();
        assertEquals(1, usersAfterDelete.size());
        assertTrue(usersAfterDelete.contains(savedAnotherUser));
    }
}
