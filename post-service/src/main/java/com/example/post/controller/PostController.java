package com.example.post.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/posts")
public class PostController {

    @RequestMapping(value = "/health",
            method = RequestMethod.GET)
    public String health() {
        return "Post service is up";
    }

}
