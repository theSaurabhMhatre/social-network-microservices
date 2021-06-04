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
public class JWTUtility
        implements Serializable {

    private static final long serialVersionUID = 123456654321L;

    @Value(value = "${jwt.secret}")
    private String secretKey;

    private String doGenerateToken(Map<String, Object> claims,
                                   String subject) {
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + ACCESS_TOKEN_EXPIRATION * 1000);
        final String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        return token;
    }

    public String generateToken(AccountDto accountDto,
                                String remote) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ACCOUNT_ID, accountDto.getId());
        claims.put(REMOTE_ADDR, remote);
        return doGenerateToken(claims, accountDto.getHandle());
    }

    public TokenDto validateToken(String token,
                                  String remote) throws Exception {
        if (!isTokenExpired(token) &&
                remote.equals(getRemoteAddressFromToken(token))) {
            Map<String, String> claims = Map.of(
                    ACCOUNT_ID, getAccountIdFromToken(token),
                    REMOTE_ADDR, getRemoteAddressFromToken(token)
            );
            TokenDto tokenDto = TokenDto.builder()
                    .subject(getSubjectFromToken(token))
                    .claims(claims)
                    .build();
            return tokenDto;
        } else {
            // TODO: throw custom exception
            throw new Exception();
        }
    }

    public Boolean validateClaims(ExpiredJwtException expiredJwtException,
                                  Map<String, Object> claims) {
        Claims trueClaims = expiredJwtException.getClaims();
        for (Map.Entry<String, Object> claim : claims.entrySet()) {
            Object trueClaim = trueClaims.get(claim.getKey(), claim.getValue().getClass());
            if (!trueClaim.equals(claim.getValue())) {
                return false;
            }
        }
        return true;
    }

    private Claims getAllClaimsFromToken(String token)
            throws Exception {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    private <T> T getClaimFromToken(String token,
                                    Function<Claims, T> claimsResolver) throws Exception {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private <T> T getClaimFromToken(String token,
                                    String key,
                                    Class<T> clazz) throws Exception {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get(key, clazz);
    }

    private String getSubjectFromToken(String token)
            throws Exception {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private Date getExpirationDateFromToken(String token)
            throws Exception {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private String getAccountIdFromToken(String token)
            throws Exception {
        return getClaimFromToken(token, ACCOUNT_ID, String.class);
    }

    private String getRemoteAddressFromToken(String token)
            throws Exception {
        return getClaimFromToken(token, REMOTE_ADDR, String.class);
    }

    private Boolean isTokenExpired(String token)
            throws Exception {
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

}
