package com.example.auth.utility;

import com.example.auth.model.dto.AuthDto;
import com.example.auth.model.dto.UserDto;
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

@Component
public class JWTUtility
        implements Serializable {

    private static final long serialVersionUID = 123456654321L;

    private static final String ID = "id";

    private static final String ROLE = "role";

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

    public String generateToken(UserDto userDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ID, userDto.getId());
        claims.put(ROLE, userDto.getRole());
        return doGenerateToken(claims, userDto.getHandle());
    }


    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public AuthDto validateToken(String token,
                                 UserDto userDto) {
        String id = getIdFromToken(token);
        if (id.equals(userDto.getId()) &&
                !isTokenExpired(token)) {
            AuthDto authDto = new AuthDto();
            authDto.setUserDto(userDto);
            Map<String, Object> claims = new HashMap<>();
            claims.put(ID, getIdFromToken(token));
            claims.put(ROLE, getRoleFromToken(token));
            authDto.setClaims(claims);
            return authDto;
        } else {
            // TODO: throw exception
            return null;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        // TODO: throw exception if parsing fails
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T getClaimFromToken(String token,
                                    Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private <T> T getClaimFromToken(String token,
                                    String key,
                                    Class<T> clazz) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get(key, clazz);
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private String getIdFromToken(String token) {
        return getClaimFromToken(token, ID, String.class);
    }

    private String getRoleFromToken(String token) {
        return getClaimFromToken(token, ROLE, String.class);
    }

    private Boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

}
