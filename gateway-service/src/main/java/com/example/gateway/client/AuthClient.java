package com.example.gateway.client;

import com.example.generic.model.dto.auth.AccountDto;
import com.example.generic.model.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.example.generic.model.constant.ClientConstants.AUTH_CLIENT;

@FeignClient(name = AUTH_CLIENT)
public interface AuthClient {

    @RequestMapping(value = "/v1/auth/validate/{token}",
            method = RequestMethod.GET)
    Response<AccountDto> validate(@PathVariable String token);

}
