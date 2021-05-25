package com.example.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/fallbacks")
public class FallbackController {

    @RequestMapping(value = "/auth",
            method = RequestMethod.GET)
    public String authServiceFallbackMethod() {
        return "Auth service is experiencing issues";
    }

    @RequestMapping(value = "/posts",
            method = RequestMethod.GET)
    public String postServiceFallbackMethod() {
        return "Post service is experiencing issues";
    }

    @RequestMapping(value = "/users",
            method = RequestMethod.GET)
    public String userServiceFallbackMethod() {
        return "User service is experiencing issues";
    }

}
