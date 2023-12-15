package com.example.backend.service;

import com.example.backend.dto.GameDto;
import com.example.backend.dto.Match;
import com.example.backend.dto.MoveDto;
import com.example.backend.mapper.GameMapper;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.Game;
import com.example.backend.model.User;
import com.example.backend.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
class GameServiceImpl implements GameService{

    private final GameRepository gameRepository;

    @Override
    public GameDto createNewGame(Match match) {
        Game game = new Game();
        game.setId(UUID.randomUUID());
        game.setPlayer1(UserMapper.toUser(match.getPlayer1()));
        game.setPlayer2(UserMapper.toUser(match.getPlayer2()));
        game.setBoard(GameMapper.convertBoardToString(new String[3][3]));
        game.setNextPlayerId(startingPlayer(game.getPlayer1(), game.getPlayer2()).getPlayerId());
        if(game.getNextPlayerId().equals(game.getPlayer1().getPlayerId())){
            game.getPlayer1().setPlayerSign("X");
            game.getPlayer2().setPlayerSign("0");
        }
        else{
            game.getPlayer1().setPlayerSign("0");
            game.getPlayer2().setPlayerSign("X");
        }
        game.setIsFinished(false);
        return GameMapper.toGameDto(gameRepository.save(game));
    }

    @Override
    public GameDto getCurrentGame(String playerId) {
        Game currentGame = this.gameRepository.findByIsFinishedFalseAndPlayer1OrPlayer2(playerId).stream()
                .max(Comparator.comparing(Game::getCreationDate))
                .orElseThrow(() -> new IllegalArgumentException("Game not found!"));
        return GameMapper.toGameDto(currentGame);
    }

    @Override
    public GameDto makeMove(String gameId, String playerId, MoveDto move) {
        Game game = this.gameRepository.findById(UUID.fromString(gameId))
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        if(!game.getPlayer1().getPlayerId().equals(playerId) && !game.getPlayer2().getPlayerId().equals(playerId)){
            throw new IllegalArgumentException("The game is not yours!");
        }
        if(game.getIsFinished()){
            throw new IllegalArgumentException("Game is finished!");
        }
        if(!game.getNextPlayerId().equals(playerId)){
            throw new IllegalArgumentException("Not your turn!");
        }
        User player = playerId.equals(game.getPlayer1().getPlayerId()) ? game.getPlayer1() : game.getPlayer2();
        String[][] board = GameMapper.convertStringToBoard(game.getBoard());
        if(!board[move.getRow()][move.getColumn()].equals("null")){
            throw new IllegalArgumentException("Incorrect move!");
        }
        board[move.getRow()][move.getColumn()] = player.getPlayerSign();
        game.setBoard(GameMapper.convertBoardToString(board));
        if(checkForWinner(player.getPlayerSign(), board)){
            game.setWinner(playerId);
            game.setIsFinished(true);
            game.setNextPlayerId(null);
        }
        else if(checkForDraw(board)){
            game.setIsFinished(true);
            game.setNextPlayerId(null);
        }
        else{
            game.setNextPlayerId(game.getPlayer1().equals(player) ? game.getPlayer2().getPlayerId()
                    : game.getPlayer1().getPlayerId());
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

    private User startingPlayer(User player1, User player2){
        Random random = new Random();
        if(random.nextInt(2) == 0){
            return player1;
        }
        else{
            return player2;
        }
    }
}
