package com.example.backend.mapper;

import com.example.backend.dto.GameDto;
import com.example.backend.model.Game;

import java.util.UUID;

public class GameMapper {

    private final static String delimiter = ",";

    public static GameDto toGameDto(Game game){
        return GameDto.builder()
                .id(game.getId().toString())
                .player1(game.getPlayer1())
                .player2(game.getPlayer2())
                .board(GameMapper.convertStringToBoard(game.getBoard()))
                .nextPlayer(game.getNextPlayer())
                .creationDate(game.getCreationDate())
                .winner(game.getWinner())
                .isFinished(game.getIsFinished())
                .build();
    }

    public static Game toGame(GameDto gameDto){
        Game game = new Game();
        game.setId(UUID.fromString(gameDto.getId()));
        game.setPlayer1(gameDto.getPlayer1());
        game.setPlayer2(game.getPlayer2());
        game.setBoard(GameMapper.convertBoardToString(gameDto.getBoard()));
        game.setNextPlayer(gameDto.getNextPlayer());
        game.setCreationDate(gameDto.getCreationDate());
        game.setWinner(gameDto.getWinner());
        game.setIsFinished(gameDto.getIsFinished());
        return game;
    }

    public static String convertBoardToString(String[][] board){
        StringBuilder stringBoard = new StringBuilder();
        for (String[] row : board) {
            for (String value : row) {
                stringBoard.append(value).append(GameMapper.delimiter);
            }
        }
        return stringBoard.substring(0, stringBoard.length() - 1);
    }

    public static String[][] convertStringToBoard(String stringBoard){
        String[] allValues = stringBoard.split(GameMapper.delimiter);
        String[][] board = new String[3][3];
        int row = 0;
        int column = 0;
        for(String value : allValues){
            board[row][column] = value;
            column = (column + 1) % 3;
            if(column == 0) row += 1;
        }
        return board;
    }
}
