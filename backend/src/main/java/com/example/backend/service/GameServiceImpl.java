package com.example.backend.service;

import com.example.backend.dto.GameDto;
import com.example.backend.dto.Match;
import com.example.backend.dto.MoveDto;
import com.example.backend.mapper.GameMapper;
import com.example.backend.model.Game;
import com.example.backend.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
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
        if(game.getNextPlayer().equals(game.getPlayer1())){
            game.setPlayer1Sign("X");
            game.setPlayer2Sign("0");
        }
        else{
            game.setPlayer1Sign("0");
            game.setPlayer2Sign("X");
        }
        game.setIsFinished(false);
        return GameMapper.toGameDto(gameRepository.save(game));
    }

    @Override
    public GameDto makeMove(String gameId, String playerId, MoveDto move) {
        Game game = this.gameRepository.findById(UUID.fromString(gameId))
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        if(!game.getPlayer1().equals(playerId) && !game.getPlayer2().equals(playerId)){
            throw new IllegalArgumentException("The game is not yours!");
        }
        if(game.getIsFinished()){
            throw new IllegalArgumentException("Game is finished!");
        }
        if(!game.getNextPlayer().equals(playerId)){
            throw new IllegalArgumentException("Not your turn!");
        }
        String sign = playerId.equals(game.getPlayer1()) ? game.getPlayer1Sign() : game.getPlayer2Sign();
        String[][] board = GameMapper.convertStringToBoard(game.getBoard());
        if(!board[move.getRow()][move.getColumn()].equals("null")){
            throw new IllegalArgumentException("Incorrect move!");
        }
        board[move.getRow()][move.getColumn()] = sign;
        game.setBoard(GameMapper.convertBoardToString(board));
        if(checkForWinner(sign, board)){
            game.setWinner(playerId);
            game.setIsFinished(true);
            game.setNextPlayer(null);
        }
        else if(checkForDraw(board)){
            game.setIsFinished(true);
            game.setNextPlayer(null);
        }
        else{
            game.setNextPlayer(game.getPlayer1().equals(playerId) ? game.getPlayer2() : game.getPlayer1());
        }
        return GameMapper.toGameDto(this.gameRepository.save(game));
    }

    private Boolean checkForWinner(String player, String[][] board){
        for (int i = 0; i < 3; i++) {
            if (checkRow(i, player, board)) return true;
        }

        for (int i = 0; i < 3; i++) {
            if (checkColumn(i, player, board)) return true;
        }

        return checkDiagonal(player, board);
    }

    private boolean checkRow(int i, String player, String[][] board) {
        return board[i][0].equals(player) && board[i][1].equals(player) && board[i][2].equals(player);
    }

    private boolean checkColumn(int i, String player, String[][] board) {
        return board[0][i].equals(player) && board[1][i].equals(player) && board[2][i].equals(player);
    }

    private boolean checkDiagonal(String player, String[][] board) {
        if (board[0][0].equals(player) && board[1][1].equals(player) && board[2][2].equals(player)) {
            return true;
        }
        return board[0][2].equals(player) && board[1][1].equals(player) && board[2][0].equals(player);
    }

    private Boolean checkForDraw(String[][] board) {
        return Arrays.stream(board).flatMap(Arrays::stream).noneMatch(x -> x.equals("null"));
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
