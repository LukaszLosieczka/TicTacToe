package com.example.backend.service;

import com.example.backend.dto.Match;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Service
@RequiredArgsConstructor
@Slf4j
class QueueServiceImpl implements QueueService{
    private final Queue<String> queue = new LinkedList<>();

    @Override
    public void addPlayerToQueue(String playerId) {
        if(!queue.contains(playerId)){
            queue.add(playerId);
        }
    }

    @Override
    public Boolean hasMatch() {
        return queue.size() % 2 == 0;
    }

    @Override
    public Match findMatch() {
        return Match.builder()
                .player1Id(this.queue.poll())
                .player2Id(this.queue.poll())
                .build();
    }
}
