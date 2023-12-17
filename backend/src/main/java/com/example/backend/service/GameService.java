package com.example.backend.service;

import com.example.backend.dto.*;

import java.util.List;

public interface GameService {

    GameDto createNewGame(Match match);

    GameDto getCurrentGame(String playerId);

    GameDto makeMove(String gameId, String playerId, MoveDto move);

    List<LeaderBoardPos> getLeaderBoard();
}
