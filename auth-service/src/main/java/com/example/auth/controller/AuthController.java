package com.example.auth.controller;

import com.example.auth.model.dto.UserDto;
import com.example.auth.service.AuthService;
import com.example.commons.model.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(value = "/health",
            method = RequestMethod.GET)
    public Response health() {
        Response<String> response = Response.ok();
        response.setData("Auth service is up");
        return response;
    }

    @RequestMapping(value = "/login",
            method = RequestMethod.POST)
    public Response login(@RequestBody UserDto userDto,
                          HttpServletRequest httpServletRequest) {
        Response<String> response = Response.ok();
        response.setData(authService.login(userDto, httpServletRequest.getRemoteAddr()));
        return response;
    }

    @RequestMapping(value = "/validate/{token}",
            method = RequestMethod.GET)
    public Response validate(@PathVariable String token,
                             HttpServletRequest httpServletRequest) {
        Response<UserDto> response = Response.ok();
        response.setData(authService.validate(token, httpServletRequest.getRemoteAddr()));
        return response;
    }

}
