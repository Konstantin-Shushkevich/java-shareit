package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.user.validation.CreateUserValidation;
import ru.practicum.shareit.user.validation.PatchUserValidation;

@Builder
@Getter @Setter @ToString
@EqualsAndHashCode
public class UserDto {

    private Long id;

    //TODO привести валидацию к подобию с таблицами в БД
    @NotBlank(groups = CreateUserValidation.class, message = "Not able to add user with blank name")
    private String name;

    @NotBlank(groups = CreateUserValidation.class, message = "Not able to add user with blank email")
    @Size(groups = {CreateUserValidation.class, PatchUserValidation.class}, max = 255,
            message = "Maximum email address size exceeded (greater than 255)")
    @Email(groups = {CreateUserValidation.class, PatchUserValidation.class})
    private String email;
}
