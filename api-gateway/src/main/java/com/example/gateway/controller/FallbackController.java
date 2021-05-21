package com.example.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/fallbacks")
public class FallbackController {

    @RequestMapping(method = RequestMethod.GET,
            value = "/users")
    public String userServiceFallbackMethod() {
        return "User service is experiencing issues";
    }
    @RequestMapping(method = RequestMethod.GET,
            value = "/posts")
    public String postServiceFallbackMethod() {
        return "Post service is experiencing issues";
    }

}
