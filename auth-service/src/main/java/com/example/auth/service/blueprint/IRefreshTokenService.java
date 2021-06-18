package com.example.auth.service.blueprint;

import com.example.generic.model.dto.auth.AccountDto;
import com.example.generic.model.dto.auth.RefreshTokenDto;

public interface IRefreshTokenService {

    RefreshTokenDto generateRefreshToken(AccountDto accountDto);

    RefreshTokenDto findByToken(String token);

    void deleteByAccount(AccountDto accountDto);

}
