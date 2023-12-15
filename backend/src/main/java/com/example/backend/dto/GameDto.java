package com.example.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameDto {
    private String id;
    private UserDto player1;
    private UserDto player2;
    private String[][] board;
    private String nextPlayerId;
    private LocalDateTime creationDate;
    private Boolean isFinished;
    private String winner;

    public void changeBoardState(int x, int y, String value){
        this.board[x][y] = value;
    }
}
