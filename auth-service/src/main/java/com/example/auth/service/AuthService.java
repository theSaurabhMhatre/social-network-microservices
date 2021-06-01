package com.example.auth.service;

import com.example.auth.model.constant.ERole;
import com.example.auth.utility.JWTUtility;
import com.example.data.model.dto.auth.AccountDto;
import com.example.data.model.dto.auth.RoleDto;
import com.example.data.model.dto.auth.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Set;

@Service
public class AuthService {

    private final JWTUtility jwtUtility;

    @Autowired
    public AuthService(JWTUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    public String login(AccountDto accountDto,
                        String remote) {
        // TODO: validate user credentials
        if (!ObjectUtils.isEmpty(accountDto)) {
            String token = jwtUtility.generateToken(accountDto, remote);
            return token;
        } else {
            return null;
        }
    }

    public AccountDto validate(String token,
                               String remote) {
        try {
            TokenDto tokenDto = jwtUtility.validateToken(token, remote);
            // TODO: fetch account details from database using handle and id
            AccountDto accountDto = AccountDto.builder()
                    .id("123")
                    .handle("demo")
                    .password("pass")
                    .roles(Set.of(RoleDto.builder().role(ERole.ROLE_USER.toString()).build()))
                    .build();
            if (ObjectUtils.isEmpty(accountDto)) {
                throw new Exception();
            }
            return accountDto;
        } catch (Exception ex) {
            return null;
        }
    }

}
