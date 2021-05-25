package com.example.auth.service;

import com.example.auth.model.dto.AuthDto;
import com.example.auth.model.dto.UserDto;
import com.example.auth.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    private final JWTUtility jwtUtility;

    private final RestTemplate restTemplate;

    @Autowired
    public AuthService(JWTUtility jwtUtility,
                       RestTemplate restTemplate) {
        this.jwtUtility = jwtUtility;
        this.restTemplate = restTemplate;
    }

    public String login(UserDto userDto) {
        String response = this.restTemplate.getForObject("http://USER-SERVICE/v1/users/health", String.class);
        if (null != response) {
            String token = this.jwtUtility.generateToken(userDto);
            return token;
        } else {
            return null;
        }
    }

    public Boolean validate(AuthDto authDto) {
        // TODO: fetch user by handle, update userDto
        return this.jwtUtility.validateToken(authDto);
    }

}
