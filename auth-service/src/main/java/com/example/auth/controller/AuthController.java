package com.example.auth.controller;

import com.example.auth.service.blueprint.IAuthService;
import com.example.auth.service.concrete.AuthService;
import com.example.data.model.dto.auth.AccountDto;
import com.example.data.model.dto.auth.TokenPairDto;
import com.example.data.model.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {

    private final IAuthService authService;

    @Autowired
    public AuthController(
            AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(value = "/health",
            method = RequestMethod.GET)
    public Response<String> health() {
        Response<String> response = Response.ok();
        response.setData("Auth service is up");
        return response;
    }

    @RequestMapping(value = "/register",
            method = RequestMethod.POST)
    public Response<AccountDto> register(
            @Valid @RequestBody AccountDto accountDto) {
        Response<AccountDto> response = Response.ok();
        response.setData(authService.register(accountDto));
        return response;
    }

    @RequestMapping(value = "/login",
            method = RequestMethod.POST)
    public Response<TokenPairDto> login(
            @Valid @RequestBody AccountDto accountDto,
            HttpServletRequest httpServletRequest) {
        Response<TokenPairDto> response = Response.ok();
        response.setData(authService.login(accountDto, httpServletRequest.getRemoteAddr()));
        return response;
    }

    @RequestMapping(value = "/validate/{token}",
            method = RequestMethod.GET)
    public Response<AccountDto> validate(
            @PathVariable String token,
            HttpServletRequest httpServletRequest) {
        Response<AccountDto> response = Response.ok();
        response.setData(authService.validate(token, httpServletRequest.getRemoteAddr()));
        return response;
    }

    @RequestMapping(value = "/refresh",
            method = RequestMethod.GET)
    public Response<TokenPairDto> refresh(
            @Valid @RequestBody TokenPairDto tokenPairDto,
            HttpServletRequest httpServletRequest) {
        Response<TokenPairDto> response = Response.ok();
        response.setData(authService.refresh(tokenPairDto, httpServletRequest.getRemoteAddr()));
        return response;
    }

}
