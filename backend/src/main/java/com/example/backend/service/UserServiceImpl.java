package com.example.backend.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.example.backend.dto.RegisterUser;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AWSCognitoIdentityProvider awsCognitoIdentityProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByJwt(Jwt jwt) {
        String username = jwt.getSubject();
        GetUserRequest getUserRequest = new GetUserRequest()
                .withAccessToken(jwt.getTokenValue());
        GetUserResult getUserResult = awsCognitoIdentityProvider.getUser(getUserRequest);
        List<AttributeType> attributes = getUserResult.getUserAttributes();
        Map<String, String> attributeMap = attributes.stream()
                .collect(Collectors.toMap(AttributeType::getName, AttributeType::getValue));
        List<GrantedAuthority> authorities = getAuthorities(attributeMap);
        return new org.springframework.security.core.userdetails.User(username, "", authorities);
    }

    private List<GrantedAuthority> getAuthorities(Map<String, String> attributeMap) {
        String groupAttributeName = "custom:groups";
        String groups = attributeMap.getOrDefault(groupAttributeName, "");
        return Arrays.stream(groups.split(","))
                .map(group -> new SimpleGrantedAuthority("ROLE_" + group.toUpperCase()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("This method is not supported.");
    }

    @Override
    public User registerNewUserAccount(RegisterUser userDto) {
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
