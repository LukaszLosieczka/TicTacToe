package com.example.backend.mapper;

import com.example.backend.dto.GameDto;
import com.example.backend.model.Game;

public class GameMapper {

    private final static String delimiter = ",";

    public static GameDto toGameDto(Game game){
        return GameDto.builder()
                .id(game.getId().toString())
                .player1(UserMapper.toUserDto(game.getPlayer1()))
                .player2(UserMapper.toUserDto(game.getPlayer2()))
                .board(GameMapper.convertStringToBoard(game.getBoard()))
                .nextPlayerId(game.getNextPlayerId())
                .creationDate(game.getCreationDate())
                .winner(game.getWinner())
                .isFinished(game.getIsFinished())
                .build();
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
