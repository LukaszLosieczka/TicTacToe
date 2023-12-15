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

    @Embedded
    private User player1;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="playerId", column=@Column(name="player2_id")),
            @AttributeOverride(name="playerUsername", column=@Column(name="player2_username")),
            @AttributeOverride(name="playerSign", column=@Column(name="player2_sign"))
    })
    private User player2;

    @Column(name = "board", nullable = false)
    private String board;

    @Column(name = "next_player")
    private String nextPlayerId;

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