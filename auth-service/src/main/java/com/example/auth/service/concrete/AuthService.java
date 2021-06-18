package com.example.auth.service.concrete;

import com.example.auth.model.entity.Account;
import com.example.auth.model.mapper.AccountMapper;
import com.example.auth.repository.AccountRepository;
import com.example.auth.service.blueprint.IAuthService;
import com.example.auth.service.blueprint.IRefreshTokenService;
import com.example.auth.service.blueprint.IRoleService;
import com.example.auth.utility.JWTUtils;
import com.example.auth.utility.MessageUtils;
import com.example.exception.hierarchy.UpsertException;
import com.example.exception.hierarchy.ValidationException;
import com.example.functional.result.Result;
import com.example.generic.model.dto.auth.AccountDto;
import com.example.generic.model.dto.auth.RefreshTokenDto;
import com.example.generic.model.dto.auth.TokenDto;
import com.example.generic.model.dto.auth.TokenPairDto;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.example.generic.model.constant.AuthConstants.Claims.ACCOUNT_ID;
import static com.example.generic.model.constant.AuthConstants.Claims.REMOTE_ADDR;

@Service
@Transactional
public class AuthService
        implements IAuthService {

    private final JWTUtils jwtUtils;

    private final IRoleService roleService;

    private final IRefreshTokenService refreshTokenService;

    private final AccountRepository accountRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(
            JWTUtils jwtUtils,
            IRoleService roleService,
            IRefreshTokenService refreshTokenService,
            AccountRepository accountRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.roleService = roleService;
        this.refreshTokenService = refreshTokenService;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountDto register(AccountDto accountDto) {
        accountDto.setRoles(Set.of(roleService.getDefaultRole()));
        accountDto.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        AccountDto registeredAccountDto = Optional
                .of(accountRepository.save(AccountMapper.toAccount(accountDto)))
                .map(AccountMapper::toAccountDto)
                .orElseThrow(() -> upsertException("generic.error.save.failed"));
        return registeredAccountDto;
    }

    public TokenPairDto login(AccountDto accountDto, String remote) {
        Account account = Optional
                .ofNullable(accountRepository.findByHandle(accountDto.getHandle()))
                .orElseThrow(() -> validationException("validation.error.no.account"));
        Boolean invalidPassword = !passwordEncoder.matches(accountDto.getPassword(), account.getPassword());
        if (invalidPassword) throw validationException("validation.error.invalid.credentials");
        AccountDto accountDtoTmp = AccountMapper.toAccountDto(account);
        refreshTokenService.deleteByAccount(accountDtoTmp);
        TokenPairDto tokenPairDto = TokenPairDto
                .builder()
                .accessToken(jwtUtils.generateToken(accountDtoTmp, remote))
                .refreshToken(refreshTokenService.generateRefreshToken(accountDtoTmp).getToken())
                .build();
        return tokenPairDto;
    }

    public AccountDto validate(String token, String remote) {
        TokenDto tokenDto = Optional
                .ofNullable(jwtUtils.validateToken(token, remote))
                .orElseThrow(() -> validationException("jwt.error.token.expired"));
        Account account = accountRepository
                .findByIdAndHandle(tokenDto.getClaims().get(ACCOUNT_ID), tokenDto.getSubject());
        AccountDto accountDto = Optional
                .ofNullable(account)
                .map(AccountMapper::toAccountDto)
                .orElseThrow(() -> validationException("jwt.error.invalid.token"));
        return accountDto;
    }

    public TokenPairDto refresh(TokenPairDto tokenPairDto, String remote) {
        Exception exception = Result
                .attempt(() -> jwtUtils.validateToken(tokenPairDto.getAccessToken(), remote))
                .ifSuccessThrow(() -> validationException("jwt.error.valid.token"))
                .orElseThrow(() -> exception("generic.error.exception.occurred"));
        RefreshTokenDto refreshTokenDto = refreshTokenService.findByToken(tokenPairDto.getRefreshToken());
        Map<String, Object> claims = Map.of(REMOTE_ADDR, remote,
                ACCOUNT_ID, refreshTokenDto.getAccountDto().getId());
        Boolean invalidRefreshToken =
                !jwtUtils.validateClaims((ExpiredJwtException) exception, claims) ||
                !tokenPairDto.getRefreshToken().equals(refreshTokenDto.getToken());
        if (invalidRefreshToken) throw validationException("jwt.error.invalid.pair");
        AccountDto accountDto = refreshTokenDto.getAccountDto();
        refreshTokenService.deleteByAccount(accountDto);
        TokenPairDto refreshedTokenPairDto = TokenPairDto
                .builder()
                .accessToken(jwtUtils.generateToken(accountDto, remote))
                .refreshToken(refreshTokenService.generateRefreshToken(accountDto).getToken())
                .build();
        return refreshedTokenPairDto;
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
