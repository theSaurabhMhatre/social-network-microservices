package com.example.auth.service.blueprint;

import com.example.generic.model.dto.auth.AccountDto;
import com.example.generic.model.dto.auth.TokenPairDto;

public interface IAuthService {

    AccountDto register(AccountDto accountDto);

    TokenPairDto login(AccountDto accountDto, String remote);

    AccountDto validate(String token, String remote);

    TokenPairDto refresh(TokenPairDto tokenPairDto, String remote);

}
