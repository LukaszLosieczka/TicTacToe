package com.example.backend.dto;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserQueue {
    String playerId;
    String playerUsername;
}
