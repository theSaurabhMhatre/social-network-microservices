package com.example.post.service.concrete;

import com.example.post.service.blueprint.IPostService;
import org.springframework.stereotype.Service;

@Service
public class PostService
        implements IPostService {

    public void health() {
        System.out.println("Inside health() method of PostService");
    }

}
