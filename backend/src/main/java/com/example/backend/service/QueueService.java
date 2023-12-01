package com.example.backend.service;

import com.example.backend.dto.Match;

public interface QueueService {

    void addPlayerToQueue(String playerId);

    Boolean hasMatch();

    Match findMatch();
}
