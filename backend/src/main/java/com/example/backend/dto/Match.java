package com.example.backend.dto;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Match {
    String player1Id;
    String player2Id;
}
