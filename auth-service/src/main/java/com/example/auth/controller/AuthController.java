package com.example.auth.controller;

import com.example.auth.model.dto.AuthDto;
import com.example.auth.model.dto.UserDto;
import com.example.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(value = "/health",
            method = RequestMethod.GET)
    public String health() {
        return "Auth service is up";
    }

    @RequestMapping(value = "/login",
            method = RequestMethod.POST)
    public String login(@RequestBody UserDto userDto) {
        return authService.login(userDto);
    }

    @RequestMapping(value = "/validate/{token}",
            method = RequestMethod.GET)
    public AuthDto validate(@PathVariable String token) {
        return authService.validate(token);
    }

}
