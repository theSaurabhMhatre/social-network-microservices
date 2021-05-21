package com.example.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/users")
public class UserController {

    @RequestMapping(method = RequestMethod.GET)
    public String testFunction() {
        return "User service is up";
    }

}
