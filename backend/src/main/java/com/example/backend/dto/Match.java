package com.example.backend.dto;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Match {
    UserQueue player1;
    UserQueue player2;
}
