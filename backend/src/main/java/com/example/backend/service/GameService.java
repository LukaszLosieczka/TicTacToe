package com.example.backend.service;

import com.example.backend.dto.GameDto;
import com.example.backend.dto.Match;

public interface GameService {

    GameDto createNewGame(Match match);
}
