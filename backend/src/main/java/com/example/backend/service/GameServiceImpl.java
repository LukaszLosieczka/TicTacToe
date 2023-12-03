package com.example.backend.service;

import com.example.backend.dto.GameDto;
import com.example.backend.dto.Match;
import com.example.backend.mapper.GameMapper;
import com.example.backend.model.Game;
import com.example.backend.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class GameServiceImpl implements GameService{

    private final GameRepository gameRepository;

    @Override
    public GameDto createNewGame(Match match) {
        Game game = new Game();
        game.setId(UUID.randomUUID());
        game.setPlayer1(match.getPlayer1Id());
        game.setPlayer2(match.getPlayer2Id());
        game.setBoard(GameMapper.convertBoardToString(new String[3][3]));
        game.setNextPlayer(startingPlayer(match.getPlayer1Id(), match.getPlayer2Id()));
        game.setIsFinished(false);
        return GameMapper.toGameDto(gameRepository.save(game));
    }

    private String startingPlayer(String player1, String player2){
        Random random = new Random();
        if(random.nextInt(2) == 0){
            return player1;
        }
        else{
            return player2;
        }
    }
}
