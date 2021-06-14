package com.example.auth.service.blueprint;

import com.example.data.model.dto.auth.AccountDto;
import com.example.data.model.dto.auth.RefreshTokenDto;

public interface IRefreshTokenService {

    RefreshTokenDto generateRefreshToken(AccountDto accountDto);

    RefreshTokenDto findByToken(String token);

    void deleteByAccount(AccountDto accountDto);

}
