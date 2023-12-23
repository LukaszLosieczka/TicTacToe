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

    @Query(value="SELECT player.username as username, player.id as userId, COUNT(DISTINCT wonGames.id) as wonGames, COUNT(DISTINCT lostGames.id) as lostGames, COUNT(DISTINCT draws.id) as draws " +
            "FROM " +
            "(SELECT g.player_id as id, g.player_username as username FROM game as g " +
            "UNION " +
            "SELECT g.player2_id as id, g.player2_username as username FROM game as g) as player " +
            "LEFT JOIN game as wonGames ON wonGames.winner = player.id AND wonGames.is_finished = TRUE " +
            "LEFT JOIN game as lostGames ON (lostGames.player_username = player.username " +
            "         OR lostGames.player2_username = player.username) AND lostGames.winner IS NOT NULL " +
            "         AND lostGames.winner != player.id AND lostGames.is_finished = TRUE " +
            "LEFT JOIN game as draws ON (draws.player_username = player.username OR draws.player2_username = player.username) " +
            "                            AND draws.winner IS NULL AND draws.is_finished = TRUE " +
            "GROUP BY player.username, player.id",
            nativeQuery = true)
    List<Object> getLeaderBoard();
}