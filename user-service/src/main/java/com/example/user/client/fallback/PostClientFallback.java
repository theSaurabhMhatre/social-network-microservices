package com.example.user.client.fallback;

import com.example.generic.model.response.Response;
import com.example.user.client.api.PostClient;
import org.springframework.stereotype.Component;

@Component
public class PostClientFallback
        implements PostClient {

    @Override
    public Response<String> health() {
        Response<String> response = Response.ok();
        response.setData("Post service is experiencing issues");
        return response;
    }

}
