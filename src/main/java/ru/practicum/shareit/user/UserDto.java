package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.validation.CreateUserValidation;
import ru.practicum.shareit.user.validation.PatchUserValidation;

@Data
@Builder
public class UserDto {

    private Long id;

    @NotNull(groups = CreateUserValidation.class, message = "Not able to add user if name is NULL")
    @NotBlank(groups = CreateUserValidation.class, message = "Not able to add user with blank name")
    private String name;

    @NotNull(groups = CreateUserValidation.class, message = "Not able to add user if email is NULL")
    @NotBlank(groups = CreateUserValidation.class, message = "Not able to add user with blank email")
    @Size(groups = {CreateUserValidation.class, PatchUserValidation.class}, max = 255,
            message = "Maximum email address size exceeded (greater than 255)")
    @Email(groups = {CreateUserValidation.class, PatchUserValidation.class})
    private String email;
}
