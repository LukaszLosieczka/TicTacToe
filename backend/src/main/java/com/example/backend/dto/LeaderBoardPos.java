package com.example.backend.dto;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeaderBoardPos {
    private String username;
    private Long wonGames;
    private Long lostGames;
    private Long draws;
    private Long points;

    public LeaderBoardPos(String username, Long wonGames, Long lostGames, Long draws){
        this.username = username;
        this.wonGames = wonGames;
        this.lostGames = lostGames;
        this.draws = draws;
        this.points = draws + 3 * wonGames;
    }
}
