package com.example.backend.dto;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String playerId;
    private String playerUsername;
    private String playerSign;
}
