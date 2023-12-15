package com.example.backend.controller;

import com.example.backend.dto.GameDto;
import com.example.backend.dto.Match;
import com.example.backend.dto.MoveDto;
import com.example.backend.service.AuthService;
import com.example.backend.service.GameService;
import com.example.backend.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final QueueService queueService;
    private final GameService gameService;
    private final AuthService authService;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        Thread.sleep(1000); // simulated delay
        log.info("Sending Hello to " + SecurityContextHolder.getContext().getAuthentication().getName());
        return "Hello!";
    }

    @MessageMapping("/queue")
    public void addToQueue(String accessToken){
        String playerId = SecurityContextHolder.getContext().getAuthentication().getName();
        String playerUsername = authService.getUserName(accessToken);
        queueService.addPlayerToQueue(playerId, playerUsername);
        if(queueService.hasMatch()){
            Match match = queueService.findMatch();
            GameDto gameDto = gameService.createNewGame(match);
            messagingTemplate.convertAndSendToUser(
                    match.getPlayer1().getPlayerId(),
                    "/queue/notifications",
                    gameDto
            );
            messagingTemplate.convertAndSendToUser(
                    match.getPlayer2().getPlayerId(),
                    "/queue/notifications",
                    gameDto
            );
        }
    }

    @MessageMapping("/queue/disconnect")
    public void removeFromQueue(){
        String playerId = SecurityContextHolder.getContext().getAuthentication().getName();
        queueService.removePlayerFromQueue(playerId);
    }

    @MessageMapping("/move/{gameId}")
    public void makeMove(@DestinationVariable String gameId, MoveDto move){
        String playerId = SecurityContextHolder.getContext().getAuthentication().getName();
        GameDto gameDto = this.gameService.makeMove(gameId, playerId, move);
        messagingTemplate.convertAndSendToUser(
                playerId.equals(gameDto.getPlayer1().getPlayerId()) ? gameDto.getPlayer2().getPlayerId()
                        : gameDto.getPlayer1().getPlayerId(),
                "/queue/game/notifications",
                gameDto
        );
        if(gameDto.getIsFinished()){
            messagingTemplate.convertAndSendToUser(
                    playerId,
                    "/queue/game/notifications",
                    gameDto
            );
        }
    }

    @GetMapping("game/current-game")
    public ResponseEntity<Object> getCurrentGame(){
        String playerId = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            GameDto currentGame = this.gameService.getCurrentGame(playerId);
            return new ResponseEntity<>(currentGame, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("game/stats/leader-board")
    public ResponseEntity<Object> getLeaderBoard(){
        return null;
    }

    @GetMapping("game/test")
    public void test(){
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
