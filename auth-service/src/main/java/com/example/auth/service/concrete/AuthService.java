package com.example.auth.service.concrete;

import com.example.auth.model.entity.Account;
import com.example.auth.model.mapper.AccountMapper;
import com.example.auth.repository.AccountRepository;
import com.example.auth.service.blueprint.IAuthService;
import com.example.auth.service.blueprint.IRefreshTokenService;
import com.example.auth.service.blueprint.IRoleService;
import com.example.auth.utility.JWTUtility;
import com.example.data.model.dto.auth.AccountDto;
import com.example.data.model.dto.auth.RefreshTokenDto;
import com.example.data.model.dto.auth.TokenDto;
import com.example.data.model.dto.auth.TokenPairDto;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Set;

import static com.example.data.model.constant.AuthConstants.Claims.ACCOUNT_ID;
import static com.example.data.model.constant.AuthConstants.Claims.REMOTE_ADDR;

@Service
@Transactional
public class AuthService
        implements IAuthService {

    private final JWTUtility jwtUtility;

    private final IRoleService roleService;

    private final IRefreshTokenService refreshTokenService;

    private final AccountRepository accountRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(JWTUtility jwtUtility,
                       IRoleService roleService,
                       IRefreshTokenService refreshTokenService,
                       AccountRepository accountRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.jwtUtility = jwtUtility;
        this.roleService = roleService;
        this.refreshTokenService = refreshTokenService;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountDto register(AccountDto accountDto) {
        accountDto.setRoles(Set.of(roleService.getDefaultRole()));
        accountDto.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        Account account = accountRepository.save(AccountMapper.toAccount(accountDto));
        return AccountMapper.toAccountDto(account);
    }

    public TokenPairDto login(AccountDto accountDto,
                              String remote) {
        Account account = accountRepository.findByHandle(accountDto.getHandle());
        if (!ObjectUtils.isEmpty(account) &&
                passwordEncoder.matches(accountDto.getPassword(), account.getPassword())) {
            accountDto = AccountMapper.toAccountDto(account);
            refreshTokenService.deleteByAccount(accountDto);
            TokenPairDto tokenPairDto = TokenPairDto.builder()
                    .accessToken(jwtUtility.generateToken(accountDto, remote))
                    .refreshToken(refreshTokenService.generateRefreshToken(accountDto).getToken())
                    .build();
            return tokenPairDto;
        } else {
            return null;
        }
    }

    public AccountDto validate(String token,
                               String remote) throws Exception {
        try {
            TokenDto tokenDto = jwtUtility.validateToken(token, remote);
            Account account = accountRepository
                    .findByIdAndHandle(tokenDto.getClaims().get(ACCOUNT_ID), tokenDto.getSubject());
            if (ObjectUtils.isEmpty(account)) {
                throw new Exception();
            }
            return AccountMapper.toAccountDto(account);
        } catch (Exception ex) {
            throw new Exception(getErrorCause(ex));
        }
    }

    public TokenPairDto refresh(TokenPairDto tokenPairDto,
                                String remote) throws Exception {
        try {
            TokenDto tokenDto = jwtUtility.validateToken(tokenPairDto.getAccessToken(), remote);
            throw new Exception("Current access token is valid, token not refreshed.");
        } catch (Exception ex) {
            if (getErrorCause(ex).equals("Token expired, please refresh token / login again.")) {
                RefreshTokenDto refreshTokenDto = refreshTokenService
                        .findByToken(tokenPairDto.getRefreshToken());
                Map<String, Object> claims = Map.of(
                        ACCOUNT_ID, refreshTokenDto.getAccountDto().getId(),
                        REMOTE_ADDR, remote
                );
                if (jwtUtility.validateClaims((ExpiredJwtException) ex, claims) &&
                        tokenPairDto.getRefreshToken().equals(refreshTokenDto.getToken())) {
                    AccountDto accountDto = refreshTokenDto.getAccountDto();
                    refreshTokenService.deleteByAccount(accountDto);
                    tokenPairDto = TokenPairDto.builder()
                            .accessToken(jwtUtility.generateToken(accountDto, remote))
                            .refreshToken(refreshTokenService.generateRefreshToken(accountDto).getToken())
                            .build();
                    return tokenPairDto;
                }
            }
            throw new Exception(ex.getMessage());
        }
    }

    private String getErrorCause(Exception ex) {
        // TODO: read error messages from property file
        if (ex instanceof ExpiredJwtException) {
            return "Token expired, please refresh token / login again.";
        }
        return ex.getMessage();
    }

}
