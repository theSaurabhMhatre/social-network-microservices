package com.example.auth.model.mapper;

import com.example.auth.model.entity.RefreshToken;
import com.example.data.model.dto.auth.RefreshTokenDto;

public class RefreshTokenMapper {

    public static RefreshTokenDto toRefreshTokenDto(RefreshToken refreshToken) {
        return RefreshTokenDto.builder()
                .id(refreshToken.getId())
                .token(refreshToken.getToken())
                .expiration(refreshToken.getExpiration())
                .accountDto(AccountMapper.toAccountDto(refreshToken.getAccount()))
                .build();
    }

    public static RefreshToken toRefreshToken(RefreshTokenDto refreshTokenDto) {
        return RefreshToken.builder()
                .id(refreshTokenDto.getId())
                .token(refreshTokenDto.getToken())
                .expiration(refreshTokenDto.getExpiration())
                .account(AccountMapper.toAccount(refreshTokenDto.getAccountDto()))
                .build();
    }

}
