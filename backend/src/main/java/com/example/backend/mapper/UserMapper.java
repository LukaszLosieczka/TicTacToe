package com.example.backend.mapper;

import com.example.backend.dto.UserDto;
import com.example.backend.dto.UserQueue;
import com.example.backend.model.User;

public class UserMapper {

    public static User toUser(UserQueue userQueue){
        return User.builder()
                .playerId(userQueue.getPlayerId())
                .playerUsername(userQueue.getPlayerUsername())
                .build();
    }

    public static UserDto toUserDto(User user){
        return UserDto.builder()
                .playerId(user.getPlayerId())
                .playerUsername(user.getPlayerUsername())
                .playerSign(user.getPlayerSign())
                .build();
    }
}
