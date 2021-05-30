package com.example.user.controller;

import com.example.commons.model.response.Response;
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
    public Response health() {
        Response<String> response = Response.ok();
        response.setData("User service is up");
        return response;
    }

    @RequestMapping(value = "/random",
            method = RequestMethod.GET)
    public Response testFunction() {
        Response<UserDto> response = Response.ok();
        response.setData(new UserDto("123", "demo", "pass", "USER"));
        return response;
    }

    @RequestMapping(value = "/test",
            method = RequestMethod.GET)
    public Response testFunction(@RequestHeader("User") String user) {
        Response<String> response = Response.ok();
        response.setData(String.format("USER: %s", user));
        return response;
    }

}
