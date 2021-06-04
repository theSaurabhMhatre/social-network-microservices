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

@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {

    private IAuthService authService;

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

    @RequestMapping(value = "/register",
            method = RequestMethod.POST)
    public Response register(@RequestBody AccountDto accountDto) {
        Response<AccountDto> response = Response.ok();
        response.setData(authService.register(accountDto));
        return response;
    }

    @RequestMapping(value = "/login",
            method = RequestMethod.POST)
    public Response login(@RequestBody AccountDto accountDto,
                          HttpServletRequest httpServletRequest) {
        Response<TokenPairDto> response = Response.ok();
        response.setData(authService.login(accountDto, httpServletRequest.getRemoteAddr()));
        return response;
    }

    @RequestMapping(value = "/validate/{token}",
            method = RequestMethod.GET)
    public Response validate(@PathVariable String token,
                             HttpServletRequest httpServletRequest) throws Exception {
        Response<AccountDto> response = Response.ok();
        response.setData(authService.validate(token, httpServletRequest.getRemoteAddr()));
        return response;
    }

    @RequestMapping(value = "/refresh",
            method = RequestMethod.GET)
    public Response refresh(@RequestBody TokenPairDto tokenPairDto,
                            HttpServletRequest httpServletRequest) throws Exception {
        Response<TokenPairDto> response = Response.ok();
        response.setData(authService.refresh(tokenPairDto, httpServletRequest.getRemoteAddr()));
        return response;
    }

}
