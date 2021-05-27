package com.example.auth.service;

import com.example.auth.model.dto.AuthDto;
import com.example.auth.model.dto.UserDto;
import com.example.auth.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JWTUtility jwtUtility;

    @Autowired
    public AuthService(JWTUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    public String login(UserDto userDto) {
        // TODO: validate user credentials
        if (userDto != null) {
            String token = jwtUtility.generateToken(userDto);
            return token;
        } else {
            return null;
        }
    }

    public AuthDto validate(String token) {
        // TODO: fetch user details from database
        String handle = jwtUtility.getSubjectFromToken(token);
        UserDto userDto = new UserDto("123", "demo", "pass", "USER");
        return jwtUtility.validateToken(token, userDto);
    }

}
