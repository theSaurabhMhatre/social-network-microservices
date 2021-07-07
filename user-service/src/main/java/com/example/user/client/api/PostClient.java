package com.example.user.client.api;

import com.example.generic.model.response.Response;
import com.example.user.client.fallback.PostClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.example.generic.model.constant.ClientConstants.POST_CLIENT;

@Primary
@FeignClient(name = POST_CLIENT,
        fallback = PostClientFallback.class)
public interface PostClient {

    @RequestMapping(value = "/v1/posts/health",
            method = RequestMethod.GET)
    Response<String> health();

}
