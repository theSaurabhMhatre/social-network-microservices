package com.example.user.controller;

import com.example.generic.model.dto.auth.AccountDto;
import com.example.generic.model.response.Response;
import com.example.user.client.api.PostClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/users")
public class UserController {

    private final PostClient client;

    @Autowired
    public UserController(
            PostClient client) {
        this.client = client;
    }

    @RequestMapping(value = "/health",
            method = RequestMethod.GET)
    public Response<String> health() {
        System.out.println(client.health());
        Response<String> response = Response.ok();
        response.setData("User service is up");
        return response;
    }

    @RequestMapping(value = "/random",
            method = RequestMethod.GET)
    public Response<AccountDto> testFunction() {
        Response<AccountDto> response = Response.ok();
        AccountDto accountDto = AccountDto
                .builder()
                .id("123")
                .handle("demo")
                .password("pass")
                .build();
        response.setData(accountDto);
        return response;
    }

    @RequestMapping(value = "/test",
            method = RequestMethod.GET)
    public Response<String> testFunction(
            @RequestHeader("Account") String account) {
        Response<String> response = Response.ok();
        response.setData(String.format("ACCOUNT: %s", account));
        return response;
    }

}
