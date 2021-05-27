package com.example.user.controller;

import com.example.user.model.dto.UserDto;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/users")
public class UserController {

    @RequestMapping(value = "/health",
            method = RequestMethod.GET)
    public String health() {
        return "User service is up";
    }

    @RequestMapping(value = "/random",
            method = RequestMethod.GET)
    public UserDto testFunction() {
        return new UserDto("123", "demo", "pass", "USER");
    }

    @RequestMapping(value = "/test",
            method = RequestMethod.GET)
    public String testFunction(@RequestHeader("id") String id,
                               @RequestHeader("role") String role,
                               @RequestHeader("Authorization") String token) {
        return String.format("ID: %s; ROLE: %s; TOKEN: %s", id, role, token);
    }

}
