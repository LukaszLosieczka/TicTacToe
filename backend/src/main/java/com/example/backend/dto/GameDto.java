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
    private String player1;
    private String player2;
    private String[][] board;
    private String nextPlayer;
    private LocalDateTime creationDate;
    private Boolean isFinished;
    private String winner;

    public void changeBoardState(int x, int y, String value){
        this.board[x][y] = value;
    }
}
