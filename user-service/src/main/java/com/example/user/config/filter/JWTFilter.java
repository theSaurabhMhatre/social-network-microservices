package com.example.user.config.filter;

import com.example.commons.utility.ConversionUtils;
import com.example.user.model.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.commons.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.example.commons.constants.AuthConstants.USER_DATA;

@Component
public class JWTFilter
        extends OncePerRequestFilter {

    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        UserDto userDto = getUserFromHeader(httpServletRequest);
        String auth = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.isEmpty(auth) &&
                !StringUtils.isEmpty(userDto.getId()) &&
                !StringUtils.isEmpty(userDto.getPassword()) &&
                !StringUtils.isEmpty(userDto.getRole())) {
            var token = getAuthenticationToken(auth, userDto);
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private UserDto getUserFromHeader(HttpServletRequest httpServletRequest) {
        String userContents = httpServletRequest.getHeader(USER_DATA);
        try {
            return ConversionUtils.deserialize(userContents, UserDto.class);
        } catch (JsonProcessingException e) {
            // TODO: handle this
            return null;
        }
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(String credentials,
                                                                       UserDto userDto) {
        List<String> roles = List.of(userDto.getRole());
        var userDetails = new User(userDto.getId(), userDto.getPassword(), getUserAuthorities(roles));
        var token = new UsernamePasswordAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());
        return token;
    }

    private List<GrantedAuthority> getUserAuthorities(List<String> roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach((role) -> {
            authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role));
        });
        return new ArrayList<GrantedAuthority>(authorities);
    }

}
