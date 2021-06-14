package com.example.auth.service.concrete;

import com.example.auth.model.entity.RefreshToken;
import com.example.auth.model.mapper.AccountMapper;
import com.example.auth.model.mapper.RefreshTokenMapper;
import com.example.auth.repository.RefreshTokenRepository;
import com.example.auth.service.blueprint.IRefreshTokenService;
import com.example.data.component.utility.MessageUtils;
import com.example.data.model.dto.auth.AccountDto;
import com.example.data.model.dto.auth.RefreshTokenDto;
import com.example.exception.hierarchy.UpsertException;
import com.example.exception.hierarchy.ValidationException;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

import static com.example.data.model.constant.AuthConstants.REFRESH_TOKEN_EXPIRATION;
import static com.example.data.model.constant.AuthConstants.REFRESH_TOKEN_SIZE;

@Service
@Transactional
public class RefreshTokenService
        implements IRefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshTokenDto generateRefreshToken(AccountDto accountDto) {
        RefreshToken refreshToken = RefreshToken
                .builder()
                .token(RandomStringUtils.randomAlphanumeric(REFRESH_TOKEN_SIZE))
                .account(AccountMapper.toAccount(accountDto))
                .expiration(new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRATION * 1000))
                .build();
        RefreshTokenDto refreshTokenDto = Optional
                .of(refreshTokenRepository.save(refreshToken))
                .map(RefreshTokenMapper::toRefreshTokenDto)
                .orElseThrow(() -> upsertException("generic.error.save.failed"));
        return refreshTokenDto;
    }

    public RefreshTokenDto findByToken(String token) {
        RefreshToken refreshToken = Optional
                .ofNullable(refreshTokenRepository.findByToken(token))
                .orElseThrow(() -> validationException("jwt.error.invalid.refresh"));
        Boolean validToken = refreshToken.getExpiration().after(new Date());
        if (validToken) return RefreshTokenMapper.toRefreshTokenDto(refreshToken);
        deleteByAccount(AccountMapper.toAccountDto(refreshToken.getAccount()));
        throw validationException("jwt.error.refresh.expired");
    }

    public void deleteByAccount(AccountDto accountDto) {
        refreshTokenRepository.deleteByAccount(AccountMapper.toAccount(accountDto));
    }

    private ValidationException validationException(String key) {
        return new ValidationException(MessageUtils.getMessage(key));
    }

    private UpsertException upsertException(String key) {
        return new UpsertException(MessageUtils.getMessage(key));
    }

    private RuntimeException exception(String key) {
        return new RuntimeException(MessageUtils.getMessage(key));
    }

}
