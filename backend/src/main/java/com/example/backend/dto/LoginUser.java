package com.example.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser {
    @NotEmpty
    private String login;

    @NotEmpty
    private String password;
}
