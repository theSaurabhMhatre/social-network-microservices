package com.example.post.controller;

import com.example.generic.model.response.Response;
import com.example.post.service.concrete.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/posts")
public class PostController {

    private final PostService service;

    @Autowired
    public PostController(
            PostService service) {
        this.service = service;
    }

    @RequestMapping(value = "/health",
            method = RequestMethod.GET)
    public Response<String> health(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("Account") String account) {
        service.health();
        Response<String> response = Response.ok();
        response.setData("Post service is up");
        return response;
    }

}
