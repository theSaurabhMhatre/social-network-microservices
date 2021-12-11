package com.example.user.service.concrete;

import com.example.user.client.api.PostClient;
import com.example.user.service.blueprint.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService
        implements IUserService {

    private final PostClient postClient;

    @Autowired
    public UserService(
            PostClient postClient) {
        this.postClient = postClient;
    }

    public void health() {
        System.out.println(postClient.health());
    }

}
