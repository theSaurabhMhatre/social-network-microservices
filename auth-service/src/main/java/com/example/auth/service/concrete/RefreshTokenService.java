package com.example.auth.service.concrete;

import com.example.auth.model.entity.RefreshToken;
import com.example.auth.model.mapper.AccountMapper;
import com.example.auth.model.mapper.RefreshTokenMapper;
import com.example.auth.repository.RefreshTokenRepository;
import com.example.auth.service.blueprint.IRefreshTokenService;
import com.example.data.model.dto.auth.AccountDto;
import com.example.data.model.dto.auth.RefreshTokenDto;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

import static com.example.data.model.constant.AuthConstants.REFRESH_TOKEN_EXPIRATION;
import static com.example.data.model.constant.AuthConstants.REFRESH_TOKEN_SIZE;

@Service
@Transactional
public class RefreshTokenService
        implements IRefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshTokenDto generateRefreshToken(AccountDto accountDto) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(RandomStringUtils.randomAlphanumeric(REFRESH_TOKEN_SIZE))
                .account(AccountMapper.toAccount(accountDto))
                .expiration(new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRATION * 1000))
                .build();
        refreshToken = refreshTokenRepository.save(refreshToken);
        return RefreshTokenMapper.toRefreshTokenDto(refreshToken);
    }

    public RefreshTokenDto findByToken(String token)
            throws Exception {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken.getExpiration().before(new Date())) {
            deleteByAccount(AccountMapper.toAccountDto(refreshToken.getAccount()));
            throw new Exception("Refresh token expired, please login again.");
        }
        return RefreshTokenMapper.toRefreshTokenDto(refreshToken);
    }

    public void deleteByAccount(AccountDto accountDto) {
        refreshTokenRepository.deleteByAccount(AccountMapper.toAccount(accountDto));
    }

}
