package com.example.auth.service;

import com.example.auth.model.dto.TokenDto;
import com.example.auth.model.dto.UserDto;
import com.example.auth.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class AuthService {

    private final JWTUtility jwtUtility;

    @Autowired
    public AuthService(JWTUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    public String login(UserDto userDto,
                        String remote) {
        // TODO: validate user credentials
        if (!ObjectUtils.isEmpty(userDto)) {
            String token = jwtUtility.generateToken(userDto, remote);
            return token;
        } else {
            return null;
        }
    }

    public UserDto validate(String token,
                            String remote) {
        try {
            TokenDto tokenDto = jwtUtility.validateToken(token, remote);
            // TODO: fetch user details from database using handle and id
            UserDto userDto = new UserDto("123", "demo", "pass", "USER");
            if (ObjectUtils.isEmpty(userDto)) {
                throw new Exception();
            }
            return userDto;
        } catch (Exception ex) {
            return null;
        }
    }

}
