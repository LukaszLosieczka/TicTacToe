package com.example.backend.service;

import com.example.backend.dto.RegisterUser;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public User registerNewUserAccount(RegisterUser userDto) {
        log.info("User: " + SecurityContextHolder.getContext().getAuthentication().getName());
        if(userRepository.findByLogin(userDto.getLogin()) != null){
            throw new IllegalArgumentException("Użytkownik z loginem: " + userDto.getLogin() + " już istnieje");
        }
        Optional<Role> role = roleRepository.findById("ROLE_USER");
        if(role.isEmpty()){
            throw new RuntimeException("User role not found");
        }
        User user = new User();
        user.setRole(role.get());
        user.setSurname(userDto.getSurname());
        user.setName(userDto.getName());
        user.setLogin(userDto.getLogin());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        return user;
    }
}
