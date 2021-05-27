package com.example.user.config.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
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

@Component
public class JWTFilter
        extends OncePerRequestFilter {

    private static final String ID = "id";

    private static final String ROLE = "role";

    private static final String PASSWORD = "password";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String id = null;
        String pass = null;
        String role = null;
        String auth = null;
        if ((auth = httpServletRequest.getHeader(AUTHORIZATION_HEADER)) != null &&
                (id = httpServletRequest.getHeader(ID)) != null &&
                (pass = httpServletRequest.getHeader(PASSWORD)) != null &&
                (role = httpServletRequest.getHeader(ROLE)) != null) {
            var token = getAuthenticationToken(auth, id, pass, role);
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(String credentials,
                                                                       String username,
                                                                       String password,
                                                                       String role) {
        var userDetails = new User(username, password, getUserAuthorities(List.of(role)));
        var token = new UsernamePasswordAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());
        return token;
    }

    private List<GrantedAuthority> getUserAuthorities(List<String> roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach((role) -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });
        return new ArrayList<GrantedAuthority>(authorities);
    }

}
