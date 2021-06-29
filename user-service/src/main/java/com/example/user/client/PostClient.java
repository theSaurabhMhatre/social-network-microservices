package com.example.user.client;

import com.example.generic.model.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.example.generic.model.constant.ClientConstants.POST_CLIENT;

@FeignClient(name = POST_CLIENT)
public interface PostClient {

    @RequestMapping(value = "/v1/posts/health",
            method = RequestMethod.GET)
    Response<String> health();

}
