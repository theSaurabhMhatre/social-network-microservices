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
public class JWTUtility implements Serializable {

    private static final long serialVersionUID = 123456654321L;

    @Value(value = "${jwt.secret}")
    private String secretKey;

    @Value(value = "${jwt.expiration}")
    private String expirationTime;

    private Claims getAllClaimsFromToken(String token) {
        // TODO: throw exception if parsing fails
        return Jwts.parser()
                .setSigningKey(this.secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        long expirationTime = Long.parseLong(this.expirationTime);
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTime * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, this.secretKey)
                .compact();
    }

    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String generateToken(UserDto userDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDto.getId());
        claims.put("role", userDto.getRole());
        return doGenerateToken(claims, userDto.getId());
    }

    public Boolean validateToken(AuthDto authDto) {
        final String id = getSubjectFromToken(authDto.getToken());
        return id.equals(authDto.getUserDto().getId()) && !isTokenExpired(authDto.getToken());
    }

}
