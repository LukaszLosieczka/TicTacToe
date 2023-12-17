package com.example.backend.dto;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserStats {
    private String username;
    private Long wonGames;
    private Long lostGames;
    private Long draws;
}
