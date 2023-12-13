package com.example.backend.service;

import com.example.backend.dto.GameDto;
import com.example.backend.dto.Match;
import com.example.backend.dto.MoveDto;

public interface GameService {

    GameDto createNewGame(Match match);

    GameDto getCurrentGame(String playerId);

    GameDto makeMove(String gameId, String playerId, MoveDto move);
}
