package com.example.auth.utility;

import com.example.data.model.dto.auth.AccountDto;
import com.example.data.model.dto.auth.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.example.data.model.constant.AuthConstants.Claims.REMOTE_ADDR;
import static com.example.data.model.constant.AuthConstants.Claims.ACCOUNT_ID;

@Component
public class JWTUtility
        implements Serializable {

    private static final long serialVersionUID = 123456654321L;

    @Value(value = "${jwt.secret}")
    private String secretKey;

    @Value(value = "${jwt.expiration}")
    private String expirationTime;

    private String doGenerateToken(Map<String, Object> claims,
                                   String subject) {
        long expirationTimeValue = Long.parseLong(expirationTime);
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeValue * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
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
            TokenDto tokenDto = new TokenDto();
            Map<String, String> claims = Map.of(
                    ACCOUNT_ID, getAccountIdFromToken(token),
                    REMOTE_ADDR, getRemoteAddressFromToken(token)
            );
            tokenDto.setSubject(getSubjectFromToken(token));
            tokenDto.setClaims(claims);
            return tokenDto;
        } else {
            // TODO: throw custom exception
            throw new Exception();
        }
    }

    private Claims getAllClaimsFromToken(String token)
            throws Exception {
        // TODO: throw exception if parsing fails
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
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
