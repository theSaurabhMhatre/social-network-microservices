package com.example.post.controller;

import com.example.generic.model.response.Response;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/posts")
public class PostController {

    @RequestMapping(value = "/health",
            method = RequestMethod.GET)
    public Response<String> health(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("Account") String account) {
        Response<String> response = Response.ok();
        response.setData("Post service is up");
        return response;
    }

}
