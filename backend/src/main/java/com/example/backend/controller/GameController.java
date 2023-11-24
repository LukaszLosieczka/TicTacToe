package com.example.backend.controller;

import com.example.backend.dto.Match;
import com.example.backend.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private SimpMessagingTemplate messagingTemplate;
    private QueueService queueService;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        log.info(message);
        Thread.sleep(1000); // simulated delay
        return "Hello!";
    }

    @MessageMapping("/queue")
    public void addToQueue(){
        String playerId = SecurityContextHolder.getContext().getAuthentication().getName();
        queueService.addPlayerToQueue(playerId);
        if(queueService.hasMatch()){
            Match match = queueService.findMatch();
            messagingTemplate.convertAndSendToUser(
                    match.getPlayer1Id(),
                    "/queue/notifications",
                    "We have find you a game"
            );
            messagingTemplate.convertAndSendToUser(
                    match.getPlayer2Id(),
                    "/queue/notifications",
                    "We have find you a game"
            );
        }
    }

    @GetMapping("game/test")
    public void test(){
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
