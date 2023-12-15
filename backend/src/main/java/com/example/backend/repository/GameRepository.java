package com.example.backend.repository;

import com.example.backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {

    Optional<Game> findById(UUID id);

    @Query("SELECT g FROM Game g " +
            "WHERE g.isFinished = false AND (g.player1.playerId = :player OR g.player2.playerId = :player)")
    List<Game> findByIsFinishedFalseAndPlayer1OrPlayer2(String player);
}