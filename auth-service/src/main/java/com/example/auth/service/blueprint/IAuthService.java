package com.example.auth.service.blueprint;

import com.example.data.model.dto.auth.AccountDto;
import com.example.data.model.dto.auth.TokenPairDto;

public interface IAuthService {

    AccountDto register(AccountDto accountDto);

    TokenPairDto login(AccountDto accountDto, String remote);

    AccountDto validate(String token, String remote) throws Exception;

    TokenPairDto refresh(TokenPairDto tokenPairDto, String remote) throws Exception;

}
