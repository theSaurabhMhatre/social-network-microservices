package com.example.gateway.controller;

import com.example.data.model.response.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/fallbacks")
public class FallbackController {

    @RequestMapping(value = "/auth",
            method = RequestMethod.GET)
    public Response authServiceFallbackMethod() {
        Response<String> response = Response.ok();
        response.setData("Auth service is experiencing issues");
        return response;
    }

    @RequestMapping(value = "/posts",
            method = RequestMethod.GET)
    public Response postServiceFallbackMethod() {
        Response<String> response = Response.ok();
        response.setData("Post service is experiencing issues");
        return response;
    }

    @RequestMapping(value = "/users",
            method = RequestMethod.GET)
    public Response userServiceFallbackMethod() {
        Response<String> response = Response.ok();
        response.setData("User service is experiencing issues");
        return response;
    }

}
