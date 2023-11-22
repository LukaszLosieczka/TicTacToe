package com.example.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUser {

    @NotEmpty()
    @Size(min = 5, message = "Email musi mieć przynajmniej 5 znaki")
    private String email;

    @NotEmpty()
    @Size(min = 3, message = "Login musi mieć przynajmniej 3 znaki")
    private String login;

    @NotEmpty()
    @Size(min = 6, max = 32, message = "Hasło musi zawierać od 6 do 32 znaków")
    private String password;
}