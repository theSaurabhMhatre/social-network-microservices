package com.example.auth.utility;

import com.example.data.model.dto.auth.AccountDto;
import com.example.data.model.dto.auth.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.example.data.model.constant.AuthConstants.ACCESS_TOKEN_EXPIRATION;
import static com.example.data.model.constant.AuthConstants.Claims.ACCOUNT_ID;
import static com.example.data.model.constant.AuthConstants.Claims.REMOTE_ADDR;

@Component
public class JWTUtils
        implements Serializable {

    private static final long serialVersionUID = 123456654321L;

    @Value(value = "${jwt.secret}")
    private String secretKey;

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        Date createdDate = new Date();
        Date expirationDate = new Date(createdDate.getTime() + ACCESS_TOKEN_EXPIRATION * 1000);
        String token = Jwts
                .builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        return token;
    }

    public String generateToken(AccountDto accountDto, String remote) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ACCOUNT_ID, accountDto.getId());
        claims.put(REMOTE_ADDR, remote);
        return doGenerateToken(claims, accountDto.getHandle());
    }

    public TokenDto validateToken(String token, String remote) {
        Boolean invalidToken =
                isTokenExpired(token) ||
                !remote.equals(getRemoteAddressFromToken(token));
        if (invalidToken) return null;
        Map<String, String> claims = Map.of(ACCOUNT_ID, getAccountIdFromToken(token),
                REMOTE_ADDR, getRemoteAddressFromToken(token));
        TokenDto tokenDto = TokenDto
                .builder()
                .subject(getSubjectFromToken(token))
                .claims(claims)
                .build();
        return tokenDto;
    }

    public Boolean validateClaims(ExpiredJwtException expiredJwtException, Map<String, Object> claims) {
        Map<String, Object> actualClaims = expiredJwtException.getClaims();
        Boolean valid = claims
                .entrySet()
                .stream()
                .allMatch((entry) -> entry.getValue().equals(actualClaims.get(entry.getKey())));
        return valid;
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims = Jwts
                .parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private <T> T getClaimFromToken(String token, String key, Class<T> clazz) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get(key, clazz);
    }

    private String getSubjectFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private String getAccountIdFromToken(String token) {
        return getClaimFromToken(token, ACCOUNT_ID, String.class);
    }

    private String getRemoteAddressFromToken(String token) {
        return getClaimFromToken(token, REMOTE_ADDR, String.class);
    }

    private Boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

}
