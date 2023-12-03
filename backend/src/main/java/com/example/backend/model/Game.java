package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "player_1", nullable = false)
    private String player1;

    @Column(name = "player_2", nullable = false)
    private String player2;

    @Column(name = "board", nullable = false)
    private String board;

    @Column(name = "next_player")
    private String nextPlayer;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "is_finished")
    private Boolean isFinished;

    @Column(name = "winner")
    private String winner;

    @PrePersist
    private void setCreationTime(){
        this.setCreationDate(LocalDateTime.now());
    }

}