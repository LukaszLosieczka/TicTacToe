package com.example.backend.service;

import com.example.backend.dto.Match;
import com.example.backend.dto.UserQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

@Service
@RequiredArgsConstructor
@Slf4j
class QueueServiceImpl implements QueueService{
    private final Queue<UserQueue> queue = new LinkedList<>();

    @Override
    public void addPlayerToQueue(String playerId, String playerUsername) {
        if(queue.stream().noneMatch(user -> user.getPlayerId().equals(playerId))){
            queue.add(UserQueue.builder().playerId(playerId).playerUsername(playerUsername).build());
        }
    }

    @Override
    public void removePlayerFromQueue(String playerId) {
        Optional<UserQueue> userOptional = queue.stream()
                .filter(user -> user.getPlayerId().equals(playerId))
                .findFirst();
        userOptional.ifPresent(queue::remove);
    }

    @Override
    public Boolean hasMatch() {
        return queue.size() % 2 == 0;
    }

    @Override
    public Match findMatch() {
        return Match.builder()
                .player1(this.queue.poll())
                .player2(this.queue.poll())
                .build();
    }
}
