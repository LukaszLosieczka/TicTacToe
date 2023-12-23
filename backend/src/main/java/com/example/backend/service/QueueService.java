package com.example.backend.service;

import com.example.backend.dto.Match;

public interface QueueService {

    void addPlayerToQueue(String playerId, String playerUsername);

    void removePlayerFromQueue(String playerId);

    Boolean hasMatch();

    Match findMatch();
}
