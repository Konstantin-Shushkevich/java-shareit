package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class User {

    long id;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 20)
    String name;

    @NotNull
    @NotBlank
    @Size(max = 255)
    @Email
    private String email;
}
